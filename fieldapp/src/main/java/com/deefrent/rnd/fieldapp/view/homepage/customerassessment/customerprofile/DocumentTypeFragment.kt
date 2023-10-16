package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.customerprofile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.fieldapp.data.adapters.DocumentTypeAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentDocumentTypeBinding
import com.deefrent.rnd.fieldapp.network.models.DocumentType
import com.deefrent.rnd.fieldapp.utils.callbacks.DocTypeCallBack
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.CustomerAssessmentHomeViewModel

class DocumentTypeFragment : Fragment(), DocTypeCallBack {
    private lateinit var binding: FragmentDocumentTypeBinding
    private lateinit var docTypeAdapter:DocumentTypeAdapter
    private var nationalId=""
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerAssessmentHomeViewModel::class.java)
    }
    private val items = ArrayList<DocumentType>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDocumentTypeBinding.inflate(layoutInflater)
        binding.apply {
            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v)
                    .navigateUp()
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        docTypeAdapter = DocumentTypeAdapter(items,this)
        binding.rvAssessMent.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        binding.rvAssessMent.adapter = docTypeAdapter
        viewmodel.iDLookUpData.observe(viewLifecycleOwner){
            nationalId=it.idNumber
            items.clear()
            items.addAll(it.documentTypes)
            docTypeAdapter.notifyDataSetChanged()
            if (it.documentTypes.isNotEmpty()) {
                binding.noRequest.makeGone()
            }else{
                binding.noRequest.makeVisible()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        items.clear()
    }

    override fun onItemSelected(pos: Int, listItems: DocumentType) {
        val directions=DocumentTypeFragmentDirections.actionDocumentTypeFragmentToViewDocumentFragment(nationalId,
            listItems.id,listItems.name)
        findNavController().navigate(directions)

    }


}