package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.updatecdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentNextOfKinBinding
import com.deefrent.rnd.fieldapp.dtos.UpdateNokDTO
import com.deefrent.rnd.fieldapp.room.entities.IdentityTypeEntity
import com.deefrent.rnd.fieldapp.room.entities.RshipTypeEntity
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel

class UpdateNextOfKinFragment : Fragment() {
    private lateinit var binding:FragmentNextOfKinBinding
    private var rshipId=""
    private var rshipName=""
    private var phoneNumber=""
    private var national_identity=""
    private var idName=""
    private var idTypeId=""
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentNextOfKinBinding.inflate(layoutInflater)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnContinue.text = "Update"
            tvPbText.makeGone()
            pb.makeGone()
            dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner){rship->
                populateRship(rship)
            }
            dropdownItemsViewModel.getAllIDTypes().observe(viewLifecycleOwner){idType->
                populateIdentityType(idType)
            }
            viewmodel.detailsData.observe(viewLifecycleOwner){
                rshipId=it.kinInfo.relationshipId
                rshipName=it.kinInfo.relationship.toString()
                idName=it.kinInfo.identityType.toString()
                idTypeId=it.kinInfo.identityNumber.toString()

                national_identity=it.basicInfo.idNumber
                spinnerIsource.setText(it.kinInfo.relationship)
                etBAddress.setText(it.kinInfo.firstName)
                etDSale.setText(it.kinInfo.lastName)
                etYear.setText(it.kinInfo.phone)
                spinnerID.setText(it.kinInfo.identityType)
                etGuarantorAddress.setText(it.kinInfo.identityNumber)
            }
            btnContinue.setOnClickListener {
               if (isNetworkAvailable(requireContext())){
                   val validMsg = FieldValidators.VALIDINPUT
                   phoneNumber =FieldValidators().formatPhoneNumber(binding.etYear.text.toString())
                   val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                   val names=etBAddress.text.toString()
                   val surname=etDSale.text.toString()
                   val kinId=etGuarantorAddress.text.toString()
                   if (rshipName.isEmpty()){
                       toastyErrors("Select relationship")
                   }else if (names.isEmpty()){
                       tlBAddress.error="Required"
                   }else if (surname.isEmpty()){
                       tlDSale.error="Required"
                   }else if (!validPhone.contentEquals(validMsg)) {
                       etYear.requestFocus()
                       tlYear.error = validPhone
                   }else if (idName.isEmpty()){
                       toastyErrors("Select Identity type")
                   }else if (kinId.isEmpty()){
                       tiGuarantorAddress.error="Required"
                   }else{
                       tlBAddress.error=""
                       tlDSale.error=""
                       tlYear.error=""
                       tiGuarantorAddress.error=""
                       btnContinue.isEnabled=false
                       progressbar.mainPBar.makeVisible()
                       val updateNokDTO=UpdateNokDTO()
                       updateNokDTO.firstName=names
                       updateNokDTO.lastName=surname
                       updateNokDTO.relationshipId=rshipId
                       updateNokDTO.phone=phoneNumber
                       updateNokDTO.identityTypeId=idName
                       updateNokDTO.identityNumber=kinId
                       updateNokDTO.id_number=national_identity
                       viewmodel.updateNOKDetails(updateNokDTO)

                   }
               }else{
                   onNoNetworkDialog(requireContext())
               }
            }
            viewmodel.statusNokCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            onInfoSuccessDialog("Next of kin details updated successfully")
                            btnContinue.isEnabled=false
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            btnContinue.isEnabled=true
                            onInfoDialog(viewmodel.statusMessage.value)
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            btnContinue.isEnabled=true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }

        }
    }
    private fun populateRship(rship: List<RshipTypeEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, rship)
        binding.spinnerIsource.setAdapter(typeAdapter)
        binding.spinnerIsource.keyListener = null
        binding.spinnerIsource.setOnItemClickListener { parent, _, position, _ ->
            val selected: RshipTypeEntity = parent.adapter.getItem(position) as RshipTypeEntity
            rshipName=selected.name
            rshipId= selected.id.toString()
        }
    }
    private fun populateIdentityType(idType: List<IdentityTypeEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, idType)
        binding.spinnerID.setAdapter(typeAdapter)
        binding.spinnerID.keyListener = null
        binding.spinnerID.setOnItemClickListener { parent, _, position, _ ->
            val selected: IdentityTypeEntity = parent.adapter.getItem(position) as IdentityTypeEntity
            idName=selected.name
            idTypeId= selected.id.toString()
        }
    }


}