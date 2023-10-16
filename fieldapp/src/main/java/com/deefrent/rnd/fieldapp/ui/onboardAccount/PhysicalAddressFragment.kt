package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.models.counties.County
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.tasks.Task
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import android.graphics.Bitmap

import android.graphics.Canvas

import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.util.Log

import androidx.core.content.ContextCompat
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentPhysicalAddressBinding
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.viewModels.DataViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import com.google.android.gms.maps.model.*
import java.io.IOException
import java.util.*

class PhysicalAddressFragment : Fragment(), OnMapReadyCallback {
    private lateinit var roomDBViewModel: RoomDBViewModel
    private val onboardMerchantSharedViewModel: OnboardMerchantSharedViewModel by activityViewModels()
    private var _binding: FragmentPhysicalAddressBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataViewModel: DataViewModel
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationSharedPreferences: SharedPreferences? = null
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private var IsFromIncompleteDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        _binding = FragmentPhysicalAddressBinding.inflate(inflater, container, false)
        locationSharedPreferences =
            requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
        val view = binding.root
        observeLoginSharedViewModel()
        binding.btnContinue.setOnClickListener { v ->
            if (isValid()) {
                onboardMerchantSharedViewModel.setTownName(binding.etTown.text.toString())
                onboardMerchantSharedViewModel.setStreetName(binding.etStreetName.text.toString())
                onboardMerchantSharedViewModel.setBuildingName(binding.etBuildingName.text.toString())
                onboardMerchantSharedViewModel.setRoomNumber(binding.etRoomNumber.text.toString())
                onboardMerchantSharedViewModel.setCountyCode("47")
                saveDataLocally(
                    binding.etTown.text.toString(), binding.etStreetName.text.toString(),
                    binding.etBuildingName.text.toString(), binding.etRoomNumber.text.toString()
                )
            }
        }
        getCounties()
        //location
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity());
        requestLocationPermission()
        fetchLocation()
        return view
    }

    private fun observeLoginSharedViewModel() {
        loginSessionSharedViewModel.apply {
            isFromIncompleteDialog.observe(viewLifecycleOwner,
                { isFromIncompleteDialog ->
                    if (isFromIncompleteDialog) {
                        IsFromIncompleteDialog = true
                        onboardMerchantSharedViewModel.apply {
                            townName.observe(viewLifecycleOwner,
                                { townName ->
                                    binding.etTown.setText(townName)
                                })
                            streetName.observe(viewLifecycleOwner,
                                { streetName ->
                                    binding.etStreetName.setText(streetName)
                                })
                            buldingName.observe(viewLifecycleOwner,
                                { buldingName ->
                                    binding.etBuildingName.setText(buldingName)
                                })
                            roomNo.observe(viewLifecycleOwner,
                                { roomNo ->
                                    binding.etRoomNumber.setText(roomNo)
                                })
                        }
                    }
                })
        }
    }

    private fun saveDataLocally(
        Town: String,
        StreetName: String,
        BuildingName: String,
        RoomNumber: String
    ) {
        var RoomDBId = 0
        var CountyCode = "47"
        var lastStep1 = ""
        val lastStep2 = this::class.java.simpleName
        onboardMerchantSharedViewModel.apply {
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBId = roomDBId
                })
            lastStep.observe(viewLifecycleOwner,
                { lastStep ->
                    lastStep1 = if (IsFromIncompleteDialog) {
                        lastStep
                    } else {
                        lastStep2
                    }

                })
        }
        val Latitude = getLatitude()
        val Longitude = getLongitude()
        onboardMerchantSharedViewModel.setLastStep(this::class.java.simpleName)
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.updatePhysicalAddressDetails(
            CountyCode,
            Town,
            StreetName,
            BuildingName,
            RoomNumber,
            Latitude,
            Longitude, lastStep1,
            RoomDBId
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
    }

    private fun getLatitude(): String? {
        return locationSharedPreferences?.getString("latitude", "nothing")
    }

    private fun getLongitude(): String? {
        return locationSharedPreferences?.getString("longitude", "nothing")
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        val task: Task<Location> = fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location

                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }

    private fun getCounties() {
        dataViewModel.fetchCounties()
            .observe(viewLifecycleOwner) { fetchCountiesResponse ->
                if (fetchCountiesResponse != null) {
                    val counties: List<County> = fetchCountiesResponse.countiesData.counties
                    populateCounties(counties)
                } else {
                    Toasty.error(requireContext(), "An error occurred. Please try again", Toasty.LENGTH_LONG).show()
                }
            }
    }

    private fun populateCounties(counties: List<County>) {
        //populate counties dropdown
        val countiesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, counties)
        binding.acCounty.setAdapter(countiesAdapter)
        binding.acCounty.keyListener = null
        binding.acCounty.setOnItemClickListener { parent, _, position, _ ->
            val selected: County = parent.adapter.getItem(position) as County
            onboardMerchantSharedViewModel.setCountyCode(selected.countyCode.toString())
        }
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (binding.etTown.text.toString().isNullOrEmpty() || binding.etStreetName.text.toString()
                .isNullOrEmpty() || binding.etBuildingName.text.toString().isNullOrEmpty() ||
            binding.etRoomNumber.text.toString().isNullOrEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the details", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor? {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val currentLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
        val circleDrawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_map_pin
        )
        val markerIcon = circleDrawable?.let { getMarkerIconFromDrawable(it) }
        googleMap.addMarker(
            MarkerOptions()
                .position(currentLocation)
                .title("Current Location")
                .icon(markerIcon)
            //.draggable(true)
        )
        val editor = locationSharedPreferences!!.edit()
        editor.putString("latitude", currentLocation.latitude.toString()).apply()
        editor.putString("longitude", currentLocation.longitude.toString()).apply()
        val addresses: List<Address>
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                currentLocation.latitude,
                currentLocation.longitude,
                1
            ) as List<Address>
            val city = addresses[0].getAddressLine(0)
            val county = addresses[0].adminArea
            val locality = addresses[0].locality
            if (locality != null) {
                binding.etTown.setText(locality.toString())
            }
            Log.d("PhysicalAddressFragment", "address 0: ${addresses[0]}")
            Log.d("PhysicalAddressFragment", "city 0: $city")
            Log.d("PhysicalAddressFragment", "county 0: $county")
            Log.d("PhysicalAddressFragment", "locality 0: $locality")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /*googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                marker.position
                try {
                    addresses = geocoder.getFromLocation(
                        marker.position.latitude,
                        marker.position.longitude,
                        1
                    )
                    val city = addresses[0].getAddressLine(0)
                    val county=addresses[0].adminArea
                    val locality=addresses[0].locality
                    Log.d("PhysicalAddressFragment", "address 0: ${addresses[0]}")
                    Log.d("PhysicalAddressFragment", "city 0: $city")
                    Log.d("PhysicalAddressFragment", "county 0: $county")
                    Log.d("PhysicalAddressFragment", "locality 0: $locality")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })*/
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestLocationPermission()
            return
        } else {
            googleMap.isMyLocationEnabled = true
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15F))
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn())
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null)
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 100
        private lateinit var currentLocation: Location

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhysicalAddressFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}