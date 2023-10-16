package com.deefrent.rnd.fieldapp.view.homepage.billers


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.deefrent.rnd.fieldapp.data.adapters.BillersAdapter
import com.deefrent.rnd.fieldapp.databinding.BillersFragmentBinding
import com.deefrent.rnd.fieldapp.network.models.Biller
import com.deefrent.rnd.fieldapp.utils.callbacks.BillersCallback
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.android.synthetic.main.billers_fragment.*
import java.util.*


class BillersFragment : Fragment(), BillersCallback {
    private lateinit var binding: BillersFragmentBinding
    private lateinit var billersAdapter: BillersAdapter
    var arrayList = ArrayList<Biller>()
    val displayList = ArrayList<Biller>()

    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(BillersViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BillersFragmentBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billersAdapter = BillersAdapter(requireContext(), displayList, this)
        binding.rvBillers.layoutManager = GridLayoutManager(activity, 1)
        binding.rvBillers.adapter = billersAdapter
        searchBiller()
        if (isNetworkAvailable(requireContext())) {
            if (arrayList.isEmpty()) {
                getBillers()
            }
        } else {
            binding.apply {
                llNoData.makeVisible()
                tvNoData.text = "Please connect to internet and refresh"
            }
        }
        binding.btnRefresh.setOnClickListener {
            if (isNetworkAvailable(requireContext())) {
                getBillers()
            } else {
                binding.apply {
                    llNoData.makeVisible()
                    tvNoData.text = "Please connect to internet and refresh"
                }
            }
        }
        binding.apply {
            viewmodel.responseStatus.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.progressbar.mainPBar.makeVisible()
                        }
                        GeneralResponseStatus.DONE -> {
                            binding.progressbar.mainPBar.makeGone()
                        }
                        GeneralResponseStatus.ERROR -> {
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }

        }

    }

    private fun getBillers() {
        binding.apply {
            progressbar.tvWait.text = "Fetching billers..."
            tvNoData.text = "No billers found"
            llNoData.makeGone()
            rvBillers.makeGone()
        }
        viewmodel.getAllBillers()
        viewmodel.billList.observe(viewLifecycleOwner, Observer {
            displayList.clear()
            arrayList.clear()
            displayList.addAll(it!!)
            billersAdapter.notifyDataSetChanged()
            arrayList.addAll(it)
            if (it.isEmpty()) {
                binding.search.makeGone()
                binding.llNoData.makeVisible()
            } else {
                binding.rvBillers.makeVisible()
                binding.llNoData.makeGone()
                binding.search.makeVisible()
            }
        })
    }

    private fun searchBiller() {
        val searchView = binding.search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    displayList.clear()
                    binding.llNoData.visibility = View.GONE
                    val search = newText.toLowerCase(Locale.US)
                    arrayList.forEach {
                        if (it.name.toLowerCase(Locale.US).contains(search)) {
                            displayList.add(it)
                        }
                    }
                    if (displayList.isEmpty()) {
                        binding.llNoData.visibility = View.VISIBLE
                    } else {
                        binding.llNoData.visibility = View.GONE
                    }
                    binding.rvBillers.adapter?.notifyDataSetChanged()
                } else {
                    binding.llNoData.visibility = View.GONE
                    displayList.clear()
                    displayList.addAll(arrayList)
                    binding.rvBillers.adapter?.notifyDataSetChanged()
                }
                return true
            }
        })

    }

    override fun onItemSelected(biller: Biller, pos: Int) {
        displayList.clear()
        val direction = BillersFragmentDirections.actionBillersFragmentToPaybillFragment(biller)
        //val direction =BillerFragmentDirections.actionBillerFragmentToPayBillFragment(billpay)
        findNavController().navigate(direction)
    }

}
