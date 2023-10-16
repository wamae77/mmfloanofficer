package com.deefrent.rnd.fieldapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.room.repos.DropdownItemRepository
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.*
import javax.inject.Inject

class DropdownItemsViewModel @Inject constructor() : ViewModel() {
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _responseGStatus = MutableLiveData<GeneralResponseStatus>()
    val responseGStatus: LiveData<GeneralResponseStatus>
        get() = _responseGStatus
    private val viewModelJob = Job()
    val repo: DropdownItemRepository
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getAllGender(): LiveData<List<Gender>> {
        return repo.retrieveAllGender
    }

    fun getAllEduLevel(): LiveData<List<EducationLevel>> {
        return repo.getAllEduLevel
    }

    fun getAllIdentifiers(): LiveData<List<IdentifyEntity>> {
        return repo.getAllIdentifiers
    }

    fun getAllBusinessType(): LiveData<List<BusinessType>> {
        return repo.getAllBznessType
    }

    fun getAllEconomicSector(): LiveData<List<EconomicSector>> {
        return repo.getAllEconomicStatus
    }

    fun getAllEstablishType(): LiveData<List<EstablishmentType>> {
        return repo.getAllEstablishType
    }

    fun getAllDistrict(): LiveData<List<DistrictEntity>> {
        return repo.getDistrict
    }

    fun getEmployments(): LiveData<List<EmploymentEntity>> {
        return repo.getAllEmpStatus
    }

    fun getOccupation(): LiveData<List<OccupationEntity>> {
        return repo.getAllOccupation
    }

    fun getAllRshipType(): LiveData<List<RshipTypeEntity>> {
        return repo.getAllRshipType
    }

    fun getAllAssetType(): LiveData<List<AssetTypeEntity>> {
        return repo.getAllAssetType
    }

    fun getAllIDTypes(): LiveData<List<IdentityTypeEntity>> {
        return repo.getAllIDTypes
    }

    fun getAllAccommodationStatus(): LiveData<List<AccStatusEntity>> {
        return repo.getAllAccommodationStatus
    }

    fun getAllOfficerSubBranches(): LiveData<List<SubBranchEntity>> {
        return repo.getAllOfficerSubBranches
    }

    init {
        val dropdownItemDao =
            FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).dropdownItemDao()
        repo = DropdownItemRepository(dropdownItemDao)
        _statusCode.value = null
    }

    fun loadDropDownItem() {
        uiScope.launch {
            _responseGStatus.value = GeneralResponseStatus.LOADING
            val requestProperties = repo.getAllDropDownItemsAsync()
            try {
                val response = requestProperties.await()
                if (response.status == 1) {
                    getVillages()
                    //empData.value=response.data.empStatus
                    Log.d("TAG", "loadDropdowndata:${response.data} ")
                    repo.insertItems(response.data.genders)
                    repo.insertEduLevel(response.data.educationLevels)
                    val items = response.data.identifies.map {
                        IdentifyEntity(it.id, it.name)
                    }
                    repo.insertIdentifiers(items)

                    repo.insertBznessType(response.data.businessTypes)
                    repo.insertEconomicStatus(response.data.economicSectors)
                    repo.insertEstablishType(response.data.establishmentTypes)
                    val district = response.data.districts.map { dis ->
                        DistrictEntity(dis.id, dis.name)
                    }
                    repo.insertDistrict(district)
                    val rship = response.data.relationshipTypes.map { rship ->
                        RshipTypeEntity(rship.id, rship.name)
                    }
                    repo.insertRshipType(rship)
                    val assetType = response.data.assetTypes.map { aType ->
                        AssetTypeEntity(aType.id, aType.name)
                    }
                    repo.insertAssetType(assetType)
                    val idType = response.data.identityTypes.map { idTyp ->
                        IdentityTypeEntity(idTyp.id, idTyp.name)
                    }
                    repo.insertIDTypes(idType)
                    val accStatus = response.data.accommodationStatus.map { accStatus ->
                        AccStatusEntity(accStatus.id, accStatus.name)
                    }
                    repo.insertAccommodationStatus(accStatus)
                    val officerSubBranches =
                        response.data.officerSubBranches.map { officerSubBranches ->
                            SubBranchEntity(officerSubBranches.id, officerSubBranches.name)
                        }
                    repo.insertOfficerSubBranches(officerSubBranches)
                    _responseGStatus.value = GeneralResponseStatus.DONE
                } else if (response.status == 0) {
                    _responseGStatus.value = GeneralResponseStatus.DONE
                }
            } catch (e: Throwable) {
                _responseGStatus.value = GeneralResponseStatus.DONE
                e.printStackTrace()
            }
        }
    }

    fun getEmployment() {
        uiScope.launch {
            val request = FieldAgentApi.retrofitService.getJobAsync()
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    val employmentEntity = accResponse.data.map {
                        EmploymentEntity(it.id, it.name)
                    }
                    repo.insertEmpStatus(employmentEntity)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

    }

    fun getCurrentOccupation() {
        uiScope.launch {
            val request = FieldAgentApi.retrofitService.getOccupationItemsAsync()
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    val occupationEntity = accResponse.data.map {
                        OccupationEntity(it.id, it.name)
                    }
                    repo.insertOccupation(occupationEntity)
                }

            } catch (e: Throwable) {
                e.printStackTrace()

            }
        }

    }

    private fun getVillages() {
        uiScope.launch {
            val request = FieldAgentApi.retrofitService.getVillagesAsync()
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    Log.d("TAG", "getVillages: getVillages")
                    val villItems = accResponse.data.map {
                        VillageEntity(it.id, it.districtId, it.name)
                    }
                    repo.insertVillages(villItems)
                }

            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

    }


    fun stopObserving() {
        _statusCode.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}