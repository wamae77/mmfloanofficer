package com.deefrent.rnd.fieldapp.ui.customerGeomap

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.TextView

import android.graphics.Typeface

import android.view.Gravity

import android.widget.LinearLayout
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerLocationBinding
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter

import com.google.android.gms.maps.model.Marker
import kotlin.properties.Delegates

class CustomerLocationFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentCustomerLocationBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: CustomerGeomapSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isFromAgent360 = it.getBoolean("isFromAgent360")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerLocationBinding.inflate(inflater, container, false)
        val view = binding.root
        observeExistingAccountViewModel()
        binding.btnMakeCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$agentTelephone")
            startActivity(intent)
        }
        binding.btnGetDirections.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                //Uri.parse("http://maps.google.com/maps?saddr=-1.286389,36.817223&daddr=$Latitude,$Longitude")
                Uri.parse("")
            )
            startActivity(intent)
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    private fun observeExistingAccountViewModel() {
        lateinit var AccountNumber: String
        existingAccountViewModel.apply {
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    AccountNumber = accountNumber
                })
            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                { merchantAgentDetailsResponse ->
                    binding.tvAccountNumber.text =
                        "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $AccountNumber"
                    Latitude =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.latitude
                    Longitude =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.longitude
                    BusinessName =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName
                    binding.tvAgentTelephone.text =
                        "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                    RoomNo =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.roomNumber
                    BuildingName =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.buildingName
                    StreetName =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.streetName
                    County =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.countyName
                    agentTelephone =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo
                })
            userType.observe(viewLifecycleOwner,
                { userType ->
                    binding.tvFragmentTitle.text = "$userType Location"
                })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private lateinit var agentTelephone: String
        private lateinit var Latitude: String
        private lateinit var Longitude: String
        private lateinit var BusinessName: String
        private lateinit var RoomNo: String
        private lateinit var BuildingName: String
        private lateinit var StreetName: String
        private lateinit var County: String
        private var isFromAgent360 by Delegates.notNull<Boolean>()

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerLocationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //val nairobi = LatLng(-1.339084, 36.973837)

        val location = LatLng(Latitude.toDouble(), Longitude.toDouble())
        val marker = googleMap.addMarker(
            MarkerOptions().position(location).title(BusinessName.capitalizeWords)
                .snippet("Room $RoomNo, ${BuildingName.capitalizeWords}\n${StreetName.capitalizeWords}")
        )
        googleMap.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(requireContext())
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(requireContext())
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(requireContext())
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })
        marker?.showInfoWindow()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15F))
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn())
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null)
    }
}