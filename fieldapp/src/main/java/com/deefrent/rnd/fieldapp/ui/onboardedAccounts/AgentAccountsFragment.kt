package com.deefrent.rnd.fieldapp.ui.onboardedAccounts

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.OnboardedAgentsListAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentAgentAccountsBinding
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.Agent
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import com.deefrent.rnd.fieldapp.viewModels.OnboardedAccountsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.polyak.iconswitch.IconSwitch.Checked


class AgentAccountsFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentAgentAccountsBinding? = null
    private val binding get() = _binding!!
    private val onboardedAccountsViewModel: OnboardedAccountsViewModel by activityViewModels()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val agentsList = ArrayList<Agent>()
    private lateinit var agentsListAdapter: OnboardedAgentsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgentAccountsBinding.inflate(inflater, container, false)
        val view = binding.root
        val agents = OnboardedAccountsFragment.agents
        agentsList.clear()
        if (agents.isNotEmpty()) {
            binding.tvNoData.visibility = View.GONE
            binding.rvAccounts.visibility = View.VISIBLE
            binding.viewSeparator.visibility = View.VISIBLE
            binding.tvSwitchViewDescription.visibility = View.VISIBLE
            binding.switchMapList.visibility = View.VISIBLE
            agentsList.addAll(agents)
            agentsListAdapter = OnboardedAgentsListAdapter(agentsList, requireContext(), this)
            linearLayoutManager = LinearLayoutManager(requireContext())
            binding.rvAccounts.layoutManager = linearLayoutManager
            binding.rvAccounts.adapter = agentsListAdapter
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
                Checked.LEFT -> {
                    binding.mapView.visibility = View.GONE
                    binding.rvAccounts.visibility = View.VISIBLE
                    binding.tvSwitchViewDescription.text = "Switch to Map View"
                }
                Checked.RIGHT -> {
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
        Log.d(TAG, "onMapReady: ${agentsList.size}")
        for (agent in agentsList) {
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
            onboardedAccountsResponse.observe(
                viewLifecycleOwner
            ) { onboardedAccountsResponse ->
                val agents = onboardedAccountsResponse.data.Merchants.Agent
                Log.d(TAG, "observeSharedViewModel: ${agents.size}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "Agent Accounts"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgentAccountsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}