package com.deefrent.rnd.fieldapp.ui.existingAccount

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentMerchantPhysicalAddressBinding
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MerchantPhysicalAddressFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMerchantPhysicalAddressBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ExistingAccountSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMerchantPhysicalAddressBinding.inflate(inflater, container, false)
        val view = binding.root

        displayMerchantAgentDetails()
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    private fun displayMerchantAgentDetails() {
        existingAccountViewModel.apply {
            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                { merchantAgentDetailsResponse ->
                    BusinessName =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName
                    binding.tvTown.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.town}"
                    binding.tvStreet.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.streetName}"
                    StreetName =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.streetName
                    binding.tvBuilding.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.buildingName}"
                    BuildingName =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.buildingName
                    binding.tvRoomNumber.text =
                        "${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.roomNumber}"
                    RoomNumber =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.roomNumber
                    Latitude =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.latitude
                    Longitude =
                        merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.longitude
                })
        }
    }

    companion object {
        private lateinit var Latitude: String
        private lateinit var Longitude: String
        private lateinit var BusinessName: String
        private lateinit var RoomNumber: String
        private lateinit var BuildingName: String
        private lateinit var StreetName: String

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MerchantPhysicalAddressFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //val nairobi = LatLng(-1.339084, 36.973837)

        val location = LatLng(Latitude.toDouble(), Longitude.toDouble())
        val marker = googleMap.addMarker(
            MarkerOptions().position(location).title(BusinessName.capitalizeWords)
                .snippet("Room $RoomNumber, ${BuildingName.capitalizeWords}\n${StreetName.capitalizeWords}")
        )
        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
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