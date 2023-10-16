package com.deefrent.rnd.fieldapp.ui.onboardedAccounts

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.OnboardedMerchantsListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentMerchantAccountsBinding
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.Merchant
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import com.deefrent.rnd.fieldapp.viewModels.OnboardedAccountsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.polyak.iconswitch.IconSwitch

class MerchantAccountsFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMerchantAccountsBinding? = null
    private val binding get() = _binding!!
    private val onboardedAccountsViewModel: OnboardedAccountsViewModel by activityViewModels()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val merchantsList = ArrayList<Merchant>()
    private lateinit var merchantsListAdapter: OnboardedMerchantsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMerchantAccountsBinding.inflate(inflater, container, false)
        val view = binding.root
        val merchants = OnboardedAccountsFragment.merchants
        merchantsList.clear()
        if (merchants.isNotEmpty()) {
            binding.tvNoData.visibility = View.GONE
            binding.rvAccounts.visibility = View.VISIBLE
            binding.viewSeparator.visibility = View.VISIBLE
            binding.tvSwitchViewDescription.visibility = View.VISIBLE
            binding.switchMapList.visibility = View.VISIBLE
            merchantsList.addAll(merchants)
            merchantsListAdapter =
                OnboardedMerchantsListAdapter(merchantsList, requireContext(), this)
            linearLayoutManager = LinearLayoutManager(requireContext())
            binding.rvAccounts.layoutManager = linearLayoutManager
            binding.rvAccounts.adapter = merchantsListAdapter
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
            mapFragment.getMapAsync(this)
        } else {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvAccounts.visibility = View.GONE
            binding.viewSeparator.visibility = View.GONE
            binding.tvSwitchViewDescription.visibility = View.GONE
            binding.switchMapList.visibility = View.GONE
        }
        binding.switchMapList.setCheckedChangeListener { current ->
            when (current) {
                IconSwitch.Checked.LEFT -> {
                    binding.mapView.visibility = View.GONE
                    binding.rvAccounts.visibility = View.VISIBLE
                    binding.tvSwitchViewDescription.text = "Switch to Map View"
                }
                IconSwitch.Checked.RIGHT -> {
                    binding.rvAccounts.visibility = View.GONE
                    binding.mapView.visibility = View.VISIBLE
                    binding.tvSwitchViewDescription.text = "Switch to List View"
                }
            }
        }
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //val nairobi = LatLng(-1.339084, 36.973837)
        Log.d("MerchantAccounts", "onMapReady: ${merchantsList.size}")
        for (agent in merchantsList) {
            val location = LatLng(agent.latitude.toDouble(), agent.longitude.toDouble())
            //val marker =
            googleMap.addMarker(
                MarkerOptions().position(location).title(agent.businessName.capitalizeWords)
                    .snippet(agent.natureOfBusiness)
            )
            //marker?.showInfoWindow()
            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15F))
        }
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
        // Zoom in, animating the camera.
        //googleMap.animateCamera(CameraUpdateFactory.zoomIn())
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(5F), 2000, null)
        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun observeSharedViewModel() {
        onboardedAccountsViewModel.apply {
            onboardedAccountsResponse.observe(viewLifecycleOwner,
                { onboardedAccountsResponse2 ->
                    val merchants = onboardedAccountsResponse2.data.Merchants.Merchant
                    Log.d(TAG, "observeSharedViewModel: ${merchants.size}")
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //dashBoardItemsList.clear()
    }

    companion object {
        private const val TAG = "Merchant Accounts"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MerchantAccountsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}