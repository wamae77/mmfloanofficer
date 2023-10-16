package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.updatecdetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentBusinessAddressBinding
import com.deefrent.rnd.fieldapp.dtos.UpdateBusinessAddressDTO
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.DistrictEntity
import com.deefrent.rnd.fieldapp.room.entities.VillageEntity
import com.deefrent.rnd.fieldapp.room.repos.DropdownItemRepository
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UpdateBusinessAddressFragment : Fragment() {
    private lateinit var binding:FragmentBusinessAddressBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private lateinit var repo:DropdownItemRepository

    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }
    private var distictName=""
    private var national_identity=""
    private var cityName=""
    private var cityId =""
    private var districtId =""
    private var phoneNumber=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBusinessAddressBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnContinue.text = "Update"
            tvPbText.makeGone()
            pb.makeGone()
            ivBack.setOnClickListener {
                findNavController() .navigateUp()
            }
            dropdownItemsViewModel.getAllDistrict().observe(viewLifecycleOwner){dList->
                Log.d("TAG", "onViewCreated:dList${dList} ")
                populateDistrict(dList)
            }
          /*  GlobalScope.launch(Dispatchers.IO) {
                val dList= repo.getAllDistrict()
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "onViewCreated:dList${dList} ")
                    populateDistrict(dList)

                }
            }*/
            viewmodel.detailsData.observe(viewLifecycleOwner){
                distictName=it.businessAddress.district
                national_identity=it.basicInfo.idNumber
                districtId= it.businessAddress.districtId.toString()
                cityId= it.businessAddress.villageId.toString()
                cityName= it.businessAddress.village
                etPAddress.setText(it.businessAddress.physicalAddress)
                etAccommodationStatus.setText(it.businessAddress.district)
                spinnerCity.setText(it.businessAddress.village)
                etBPhone.setText(it.businessAddress.phoneNumber)
                etnoEmp.setText(it.businessAddress.numberOfEmployees)
            }
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())){
                    val pAddress = etPAddress.text.toString()
                    val employee = etnoEmp.text.toString()
                    val validMsg = FieldValidators.VALIDINPUT
                    phoneNumber =
                        FieldValidators().formatPhoneNumber(binding.etBPhone.text.toString())
                    val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                    if (pAddress.isEmpty()) {
                        tlPAdress.error = "Required"
                    } else if (distictName.isEmpty()) {
                        toastyErrors("select district")
                    } else if (!validPhone.contentEquals(validMsg)) {
                        etBPhone.requestFocus()
                        tlbPhone.error = validPhone
                    } else if (employee.isEmpty()) {
                        tlNoEmp.error = "Required"
                    } else {
                        tlbPhone.error=""
                        tlNoEmp.error=""
                        tlCity.error=""
                        tlPAdress.error=""
                        btnContinue.isEnabled=false
                        progressbar.mainPBar.makeVisible()
                        val updateBusinessAddressDTO=UpdateBusinessAddressDTO()
                        updateBusinessAddressDTO.business_district_id=districtId
                        updateBusinessAddressDTO.business_physical_address=pAddress
                        updateBusinessAddressDTO.business_village_id=cityId
                        updateBusinessAddressDTO.business_phone=phoneNumber
                        updateBusinessAddressDTO.number_of_employees=employee
                        updateBusinessAddressDTO.id_number=national_identity
                        viewmodel.updateBusinessAddress(updateBusinessAddressDTO)

                    }
                }else{
                    onNoNetworkDialog(requireContext())
                }
            }
            viewmodel.statusBACode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            onInfoSuccessDialog("Customer additional information updated successfully")
                            btnContinue.isEnabled=false
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            btnContinue.isEnabled=true
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
    private fun populateDistrict(district: List<DistrictEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, district)
        binding.etAccommodationStatus.setAdapter(typeAdapter)
        binding.etAccommodationStatus.keyListener = null
        binding.etAccommodationStatus.setOnItemClickListener { parent, _, position, _ ->
            val selected: DistrictEntity = parent.adapter.getItem(position) as DistrictEntity
            distictName=selected.name
            districtId= selected.id.toString()
            GlobalScope.launch(Dispatchers.IO) {
                val villageList= repo.getVillagesWithID(selected.id.toString())
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "onViewCreated:villageList${villageList} ")
                    populateCity(villageList)

                }
            }
        }
    }
    private fun populateCity(district: List<VillageEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, district)
        binding.spinnerCity.setAdapter(typeAdapter)
        binding.spinnerCity.keyListener = null
        binding.spinnerCity.setOnItemClickListener { parent, _, position, _ ->
            val selected: VillageEntity = parent.adapter.getItem(position) as VillageEntity
            cityName=selected.name
            cityId= selected.id.toString()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val dropdownItemDao= FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).dropdownItemDao()
        repo= DropdownItemRepository(dropdownItemDao)
    }


}