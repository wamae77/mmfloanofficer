package com.deefrent.rnd.fieldapp.view.homepage

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.HomeDashboardItems
import com.deefrent.rnd.fieldapp.data.MainDashboardItemAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentDashboardBinding
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.IndividualAccountDetails
import com.deefrent.rnd.fieldapp.room.entities.MerchantAgentDetails
import com.deefrent.rnd.fieldapp.room.repos.AssessCustomerRepository
import com.deefrent.rnd.fieldapp.room.repos.CustomerDetailsRepository
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.HomeDashboardItemsCallBack
import com.deefrent.rnd.fieldapp.view.auth.onboarding.AccountLookUpViewModel
import com.deefrent.rnd.fieldapp.view.auth.userlogin.PinViewModel
import com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer.OnboardCustomerViewModel
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import com.deefrent.rnd.fieldapp.viewModels.UserViewModel
import com.deefrent.rnd.fieldapp.worker.SyncLocalDataWorker
import com.deefrent.rnd.jiboostfieldapp.BuildConfig
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import es.dmoral.toasty.Toasty
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class DashboardFragment : BaseDaggerFragment(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    HomeDashboardItemsCallBack {
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private val accountLookUpViewModel: AccountLookUpViewModel by activityViewModels()
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private val assessmentDashboardViewModel: AssessmentDashboardViewModel by activityViewModels()
    private val pinViewmodel: PinViewModel by activityViewModels()
    private lateinit var repository: CustomerDetailsRepository
    private lateinit var assrep: AssessCustomerRepository

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var dashboardListAdapter: MainDashboardItemAdapter
    private var tokenSharedPreferences: SharedPreferences? = null
    private var locationSharedPreferences: SharedPreferences? = null
    private lateinit var userViewModel: UserViewModel
    private lateinit var individualAccountDetailsList: ArrayList<IndividualAccountDetails>
    private lateinit var merchantAgentDetailsList: ArrayList<MerchantAgentDetails>
    private lateinit var roomDBViewModel: RoomDBViewModel
    private lateinit var fieldAppDatabase: FieldAppDatabase
    private var localDBCount = 0
    private var incompleteLocalDataCount = 0
    private var isAuthenticated: Boolean = false


    //var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private lateinit var mLastLocation: Location
    private lateinit var mCurrLocationMarker: Marker
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mMap: GoogleMap
    private var totalDisb = ""
    private var totalRepay = ""
    private var actualBal = ""
    private var pendingRegCount = 0
    private var offlineCount = 0
    private var offC = 0
    private var pendingAssessCount = 0
    private var showBalance = false

    @Inject
    lateinit var viewmodel: OnboardCustomerViewModel
    /*  private val viewmodel by lazy {
          ViewModelProvider(requireActivity()).get(OnboardCustomerViewModel::class.java)
      }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        if (isNetworkAvailable(requireContext())) {
            dropdownItemsViewModel.loadDropDownItem()
            dropdownItemsViewModel.getEmployment()
            dropdownItemsViewModel.getCurrentOccupation()
        } else {
            onNoNetworkDialog(requireContext())
        }
        binding.apply {
            if (showBalance) {
                tvDisbValue.text = String.format(getString(R.string.kesh), totalDisb)
                tvRepayValue.text = String.format(getString(R.string.kesh), totalRepay)
                tvBalValue.text = String.format(getString(R.string.kesh), actualBal)
            } else {
                tvDisbValue.text = "✽✽✽✽✽"
                tvRepayValue.text = "✽✽✽✽✽"
                tvBalValue.text = "✽✽✽✽✽"
            }
        }
        Log.d("dash", "onCreateView: ${this::class.java.simpleName}")
        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        getLocalCustomerDetails()
        getLocalCustomerAssessmentDetails()
        //  getAssAndRegCount()

        binding?.apply {
            pinViewmodel.tellerBal.observe(viewLifecycleOwner) {
                totalDisb = FormatDigit.formatDigits(it.totalDisbursement)
                totalRepay = FormatDigit.formatDigits(it.loanRepayment)
                actualBal = FormatDigit.formatDigits(it.balance)

                /*tvDisbValue.text=String.format(getString(R.string.kesh),totalDisb)
                tvRepayValue.text=String.format(getString(R.string.kesh),totalRepay)
                tvBalValue.text=String.format(getString(R.string.kesh),actualBal)*/
            }
            val username = AppPreferences.getPreferences(requireContext(), "username")
            tvName.text = String.format(getString(R.string.fa), username?.capitalizeWords)
        }
        individualAccountDetailsList = ArrayList()
        merchantAgentDetailsList = ArrayList()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        tokenSharedPreferences =
            requireContext().getSharedPreferences("accessToken", Context.MODE_PRIVATE)
        locationSharedPreferences =
            requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
        getToken()
        //getUserDetails()
        dashboardListAdapter =
            MainDashboardItemAdapter(viewmodel.prepareDashboardItems().toSet().toList(), this)
        binding.rvDashboard.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDashboard.adapter = dashboardListAdapter

        handleBackButton()
        fieldAppDatabase = FieldAppDatabase.getFieldAppDatabase(requireContext())!!

        //location
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity());
        requestLocationPermission()
        fetchLocation()

        binding.ivSync.setOnClickListener {
            //showDialog()
        }
        /*binding.ivProfilePic.setOnClickListener { v ->
            v.findNavController()
                .navigate(R.id.action_dashboardFragment_to_DSRProfileFragment)
        }*/

        binding.ivOverflow.setOnClickListener { v ->
            // Initializing the popup menu and giving the reference as current context
            val popupMenu = PopupMenu(requireContext(), v)
            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.dashboard_pop_up, popupMenu.menu)
            popupMenu.menu.getItem(2).title = "App Version ${BuildConfig.VERSION_NAME}"
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked
                when (menuItem.title) {
                    "Change Pin" -> findNavController(this).navigate(R.id.action_dashboardFragment_to_changePINFragment)
                    "Logout" -> showLogoutDialog()
                }
                true
            }
            // Showing the popup menu
            popupMenu.show()
        }
        accountLookUpViewModel.statusLogoutCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        binding.mainPBar.makeGone()
                        AppPreferences.setPreference(
                            requireContext(),
                            "isFirstLogin",
                            "false"
                        )
                        com.deefrent.rnd.common.utils.Constants.token = ""
                        //make progress gone
                        findNavController(this).navigate(R.id.action_dashboardFragment_to_phoneLookupFragment)
                        //findNavController(this).navigate(R.id.loginFragment)
                        accountLookUpViewModel.stopObserving()
                    }

                    0 -> {
                        binding.mainPBar.makeGone()
                        if ("${accountLookUpViewModel.statusLogoutMessage.value}".contains("failed to connect")) {
                            toastyErrors("Check your internet connection and try again")
                        } else {
                            onInfoDialogWarn(accountLookUpViewModel.statusLogoutMessage.value)
                        }

                    }

                    else -> {
                        binding.mainPBar.makeGone()
                        onInfoDialog(getString(R.string.error_occurred))
                    }
                }
            }
        }
        return view
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Please confirm that you would like to logout. Please note this will clear your session")

        builder.setPositiveButton("LOGOUT") { _, _ ->
            //send logout request
            binding.mainPBar.makeVisible()
            binding.tvWait.text = "Logging out..."
            accountLookUpViewModel.logoutUser()
        }

        builder.setNegativeButton("CANCEL") { _, _ ->

        }

        builder.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvTrackIncomplete.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_incompleteRegDashboardFragment)
            }
            tvTrackAssessment.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_incompleteAssesmentFragment)
            }
            tvTrackOffline.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_offline)
            }
            btnState.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_tellerStatementsFragment)
            }
            pinViewmodel.authSuccess.observe(viewLifecycleOwner) {
                if (it == true) {
                    pinViewmodel.unsetAuthSuccess()
                    isAuthenticated = true
                    showBalance()
                    pinViewmodel.stopObserving()
                }
            }

            btnViewBal.setOnClickListener {
                if (showBalance) {
                    isAuthenticated = false
                    hideBalance()
                } else {
//                    if (isAuthenticated) {
//                        showBalance()
//                    } else {
                    findNavController().navigate(R.id.action_dashboardFragment_to_authPinFragment)
                    //}
                }
            }
        }
    }


    private fun showBalance() {
        binding.apply {
            showBalance = true

            tvDisbValue.text = String.format(getString(R.string.kesh), totalDisb)
            tvRepayValue.text = String.format(getString(R.string.kesh), totalRepay)
            tvBalValue.text = String.format(getString(R.string.kesh), actualBal)
            btnViewBal.setText("Hide Balance")
        }

    }

    private fun hideBalance() {
        binding.apply {
            showBalance = false
            tvDisbValue.text = "✽✽✽✽✽"
            tvRepayValue.text = "✽✽✽✽✽"
            tvBalValue.text = "✽✽✽✽✽"
            btnViewBal.setText("Show Balance")
        }

    }

    private fun checkMerchantDetailsTableCount() {
        fieldAppDatabase.merchantAgentDetailsDao().countMerchantAgentDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Int?> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    Log.d("room", "onError: ${e.localizedMessage}")
                    checkCustomerDetailsTableCount()
                }

                override fun onSuccess(t: Int) {
                    Log.d("dash", "checkMerchantAgentDetailsTableCount: $t")
                    if (t > 0) {
                        localDBCount++
                        checkCustomerDetailsTableCount()
                        binding.ivHasLocalData.visibility = View.VISIBLE
                    } else {
                        checkCustomerDetailsTableCount()
                        binding.ivHasLocalData.visibility = View.GONE
                    }
                }
            })
    }

    private fun checkCustomerDetailsTableCount() {
        fieldAppDatabase.individualAccountDetailsDao().countCustomerDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Int?> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    if (localDBCount > 0) {
                        //binding.ivHasLocalData.visibility = View.VISIBLE
                        startSyncDataWork()
                    }
                    Log.d("room", "onError: ${e.localizedMessage}")
                }

                override fun onSuccess(t: Int) {
                    Log.d("dash", "checkCustomerDetailsTableCount: $t")
                    if (t > 0) {
                        localDBCount++
                        if (localDBCount > 0) {
                            //binding.ivHasLocalData.visibility = View.VISIBLE
                            startSyncDataWork()
                        }
                    } else {
                        if (localDBCount > 0) {
                            //binding.ivHasLocalData.visibility = View.VISIBLE
                            startSyncDataWork()
                        } else {
                            //binding.ivHasLocalData.visibility = View.GONE
                            startSyncDataWork()
                        }
                    }

                }
            })
    }


    private fun startSyncDataWork() {
        val syncDataConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        // Create an one time work request
        val syncDataWork = OneTimeWorkRequest
            .Builder(SyncLocalDataWorker::class.java)
            .setConstraints(syncDataConstraints)
            .build()
        WorkManager.getInstance(requireContext()).enqueue((syncDataWork))
        binding.ivHasLocalData.visibility = View.GONE
    }


    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            /*ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )*/
            return
        }
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
            /*ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )*/
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

    private var progressDialog: ProgressDialog? = null
    private fun callDialog(message: String?, context: Context?) {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage(message)
        progressDialog!!.show()
        val handler = Handler()
        val runnable = Runnable {
            progressDialog?.dismiss()
            Toasty.success(requireContext(), "Data Synced successfully", Toasty.LENGTH_LONG).show()
        }
        progressDialog?.setOnDismissListener(DialogInterface.OnDismissListener {
            handler.removeCallbacks(
                runnable
            )
        })

        handler.postDelayed(runnable, 3000)
    }

    private fun getToken(): String? {
        val token = tokenSharedPreferences?.getString("token", "nothing")
        Log.d("Dashboard Fragment", "token: $token")
        return token
    }

    /*private fun getUserDetails(longitude: String, latitude: String) {
        //Constants.callDialog("Getting user details...", requireContext())
        val getUserDetailsBody = GetUserDetailsBody(latitude, longitude)
        userViewModel.getUserDetails(getUserDetailsBody)
            .observe(viewLifecycleOwner) { getUserDetailsResponse ->
                if (getUserDetailsResponse != null) {
                    val username = getUserDetailsResponse.userDetails.username
                    val mobileNo = getUserDetailsResponse.userDetails.mobileNo
                    val guiId = getUserDetailsResponse.userDetails.guiId
                    binding.tvName.text = "FA: $username - $mobileNo"
                    loginSessionSharedViewModel.setGetUserDetailsResponse(getUserDetailsResponse)
                    //Constants.cancelDialog()
                } else {
                    //Constants.cancelDialog()
                    Toasty.error(
                        requireContext(),
                        "An error occurred. Please try again",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }*/


    private fun getLocalCustomerDetails() {
        GlobalScope.launch(Dispatchers.IO) {
            val cList = repository.getIncompleteCustomerDetails(false)
            val completeOfflineList = repository.getCompleteOfflineCustomerDetails(true)
            val incompleteReg = "${cList.size}"
            binding.tvTrackIncomplete.paint.isUnderlineText = true
            binding.tvTrackAssessment.paint.isUnderlineText = true
            binding.tvTrackOffline.paint.isUnderlineText = true
            withContext(Dispatchers.Main) {
                accountLookUpViewModel.logData.observe(viewLifecycleOwner) {
                    Log.d("TAG", "pendingAssessCount: $pendingAssessCount")
                    val incompReg = incompleteReg.toInt() + it.pendingCompletionCount.toInt()
                    pendingRegCount = incompReg
                    binding.tvTrackIncomplete.text =
                        "${incompReg.toString().trim()} Incomplete Registration"
                    Log.d("TAG", "getLocalCustomerDetails: ${cList.size}")
                }
                offlineCount = completeOfflineList.size
                val offCount = offlineCount + offC
                binding.tvTrackOffline.text = "$offCount Offline Transaction"
                Log.d("TAG", "getLocalCustomerAssessmentDetail11s: $offlineCount")
                viewmodel.customerCompeteList.postValue(completeOfflineList)
                viewmodel._customerList.postValue(cList)
            }
        }
    }

    private fun getLocalCustomerAssessmentDetails() {
        GlobalScope.launch(Dispatchers.IO) {
            val assessList = assrep.getIncompleteAssessed(false)
            val assessOfflineWithList = assrep.getOfflineAssessed(true)
            val incompleteAss = "${assessList.size}"
            offC = assessOfflineWithList.size
            binding.tvTrackIncomplete.paint.isUnderlineText = true
            binding.tvTrackAssessment.paint.isUnderlineText = true
            withContext(Dispatchers.Main) {
                Log.d("TAG", "getLocalCustomerAssessmentDetails: $offlineCount")
                accountLookUpViewModel.logData.observe(viewLifecycleOwner) {
                    val incompAss = incompleteAss.toInt() + it.pendingAssessmentCount.toInt()
                    pendingAssessCount = incompAss
                    binding.tvTrackAssessment.text =
                        "${incompAss.toString().trim()} Pending Assessment"
                    Log.d("TAG", "getLocalCustomerASSDetails: ${assessList.size}")
                    Log.d("TAG", "getLocalrASSDetails: ${it.pendingAssessmentCount}")
                }
                val offCount = offlineCount + offC.toInt()
                binding.tvTrackOffline.text = "$offCount Offline Transaction"
                assessmentDashboardViewModel._customerAssessList.postValue(assessList)
                assessmentDashboardViewModel.customerCompeteOfflineWithList.postValue(
                    assessOfflineWithList
                )

            }
        }
    }

    private fun getAssAndRegCount() {
        val totalCount = pendingAssessCount + pendingRegCount
        Log.d("TAG", "getAssAndRegCount: $totalCount")
        Log.d("TAG", "getAssAndRegCount1: $pendingAssessCount")
        Log.d("TAG", "getAssAndRegCount2: $pendingRegCount")
        binding.apply {
            if (totalCount == 0) {
                binding.tvTrackIncomplete.makeGone()
                binding.tvTrackAssessment.makeGone()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
        loginSessionSharedViewModel.setIsFromLoginScreen(false)
        loginSessionSharedViewModel.setIsFromIncompleteDialog(false)
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 100
        private lateinit var currentLocation: Location

        // private var isFromLogin by Delegates.notNull<Boolean>()
        private var isFromLogin = true

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    /*override fun onMapReady(googleMap: GoogleMap) {
        val currentLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
        if (isFromLogin) {
            getUserDetails(
                currentLocation.longitude.toString(),
                currentLocation.latitude.toString()
            )
        }
        *//*googleMap.addMarker(
            MarkerOptions()
                .position(currentLocation)
                .title("My Current Location")
                .draggable(true)
        )*//*
        val editor = locationSharedPreferences!!.edit()
        editor.putString("latitude", currentLocation.latitude.toString()).apply()
        editor.putString("longitude", currentLocation.longitude.toString()).apply()
        Log.d(
            "location",
            "onMapReady: Latitude: ${currentLocation.latitude}, Longitude: ${currentLocation.longitude}"
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15F))
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn())
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null)
        *//*googleMap.setOnMapClickListener { newLatLon ->
            googleMap.clear()
            googleMap.addMarker(
                MarkerOptions().position(
                    newLatLon
                ).title("Selected Location")
            )
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
            Log.d("DashboardFragment", "onMapReady: $addresses")
        }*//*
        googleMap.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                marker.position
                val addresses: List<Address>
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    addresses = geocoder.getFromLocation(
                        marker.position.latitude,
                        marker.position.longitude,
                        1
                    )
                    val city = addresses[0].getAddressLine(1)
                    Log.d("DashboardFragment", "address 0: ${addresses[0]}")
                    Log.d("DashboardFragment", "city 0: $city")
                    //Toast.makeText(requireContext(), city, Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
    }*/

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /* if (ContextCompat.checkSelfPermission(
                     requireContext(),
                     Manifest.permission.ACCESS_FINE_LOCATION
                 )
                 == PackageManager.PERMISSION_GRANTED
             ) {
                 buildGoogleApiClient();
                 mMap.isMyLocationEnabled = true;
             }*/
        } else {
            //   buildGoogleApiClient();
            //  mMap.isMyLocationEnabled = true;
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        with(mGoogleApiClient) { this.connect() }
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        //mCurrLocationMarker.remove()
        if (isFromLogin) {
            //getUserDetails(location.longitude.toString(), location.latitude.toString())
        }
        val editor = locationSharedPreferences!!.edit()
        editor.putString("latitude", location.latitude.toString()).apply()
        editor.putString("longitude", location.longitude.toString()).apply()
        Log.d(
            "location",
            "onMapReady: Latitude: ${location.latitude}, Longitude: ${location.longitude}"
        )
        //Place current location marker
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Location")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mCurrLocationMarker = mMap.addMarker(markerOptions)

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))

        //stop location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        /* if (ContextCompat.checkSelfPermission(
                 requireContext(),
                 Manifest.permission.ACCESS_FINE_LOCATION
             )
             == PackageManager.PERMISSION_GRANTED
         ) {
             LocationServices.FusedLocationApi.requestLocationUpdates(
                 mGoogleApiClient,
                 mLocationRequest,
                 this
             )
         }*/
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(view: View, homeDashboardItems: HomeDashboardItems) {
        when (homeDashboardItems.itemPosition) {
            1 -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_onboardCustomerLookupFragment)
            }

            2 -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_assessmentDashboardFragment)
            }

            3 -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_loanLookupFragment)
            }

            4 -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_incompleteRegDashboardFragment)
            }

            5 -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_targetsFragment)
            }
            /*5 -> {
                //toastySuccess("Coming soon")
                findNavController().navigate(R.id.action_dashboardFragment_to_billPaymentLookupFragment)
            }*/
            6 -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_listDiaryFragment)
            }

            7 -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_mileageFragment)
            }

            8 -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_step1IdLookUpFuneralCashPlanFragment)
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val customerDetailsDao =
            FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).customerDetailsDao()
        repository = CustomerDetailsRepository(customerDetailsDao)
        val assessCustomerDao =
            FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).assessCustomerDao()
        assrep = AssessCustomerRepository(assessCustomerDao)
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    exitDialog()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    fun exitDialog() {
        try {
            val builder = android.app.AlertDialog.Builder(requireActivity())
                .setTitle("Confirm Exit!")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("YES") { _, which ->
                    try {
                        requireActivity().finishAffinity()
                    } catch (e: Exception) {
                        requireActivity().finish()
                        Log.e("IllegalStateException", " Can not be called to deliver a result")
                    }
                }
                .setNegativeButton("CANCEL") { dialog, which ->
                    dialog.dismiss()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
            if (!requireActivity().isFinishing) {
                builder.show()
            }
        } catch (e: Exception) {
            Log.e("", "")
        }
    }


}