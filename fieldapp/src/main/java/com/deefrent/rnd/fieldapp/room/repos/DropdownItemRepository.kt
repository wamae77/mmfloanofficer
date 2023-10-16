package com.deefrent.rnd.fieldapp.room.repos

import android.util.Log
import androidx.lifecycle.LiveData
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.room.daos.DropdownItemDao
import com.deefrent.rnd.fieldapp.room.entities.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DropdownItemRepository(private val dropdownItemDao: DropdownItemDao) {
    fun getAllDropDownItemsAsync() = FieldAgentApi.retrofitService.getDropdownItemsAsync()

    /**gender*/
    val retrieveAllGender: LiveData<List<Gender>> = dropdownItemDao.getAllGender()
    fun insertItems(genderItems: List<Gender>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteGender()
            dropdownItemDao.insertGender(genderItems)
        }
    }

    /**education level*/
    val getAllEduLevel: LiveData<List<EducationLevel>> = dropdownItemDao.getAllEduLevel()
    fun insertEduLevel(eduLevel: List<EducationLevel>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteEduLevel()
            dropdownItemDao.insertEduLevel(eduLevel)
        }
    }

    fun retrieveEduLevel(): List<EducationLevel> {
        return dropdownItemDao.retrieveEduLevel()
    }

    /**how did you hear abt us(Identifiers)*/
    val getAllIdentifiers: LiveData<List<IdentifyEntity>> = dropdownItemDao.getAllIdentifiers()
    fun insertIdentifiers(identify: List<IdentifyEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteIdentifiers()
            dropdownItemDao.insertIdentifiers(identify)
        }
    }

    /**employment status*/

    val getAllEmpStatus: LiveData<List<EmploymentEntity>> = dropdownItemDao.getEmpStatus()
    fun getEmploymentStatus(): List<EmploymentEntity> {
        return dropdownItemDao.getAllEmpStatus()
    }

    fun insertEmpStatus(empStatus: List<EmploymentEntity>) {
        Log.d("TAG", "insertEmploymentEntity: ${Gson().toJson(empStatus)}")
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteEmpStatus()
            dropdownItemDao.insertEmpStatus(empStatus)
        }
    }

    /**Occupation*/

    val getAllOccupation: LiveData<List<OccupationEntity>> = dropdownItemDao.getOccupation()

    fun insertOccupation(occupation: List<OccupationEntity>) {
        Log.d("TAG", "insertOccupationEntity: ${Gson().toJson(occupation)}")
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteOccupation()
            dropdownItemDao.insertOccupation(occupation)
        }
    }

    /**Type of business*/
    val getAllBznessType: LiveData<List<BusinessType>> = dropdownItemDao.getAllBznessType()
    fun insertBznessType(bType: List<BusinessType>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteBznessType()
            dropdownItemDao.insertBznessType(bType)
        }
    }

    /**Economic sector*/
    val getAllEconomicStatus: LiveData<List<EconomicSector>> =
        dropdownItemDao.getAllEconomicSector()

    fun insertEconomicStatus(economicStat: List<EconomicSector>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteEconomicSector()
            dropdownItemDao.insertEconomicSector(economicStat)
        }
    }

    /**Type of establishment*/
    val getAllEstablishType: LiveData<List<EstablishmentType>> = dropdownItemDao.getAllEstaType()
    fun insertEstablishType(establishType: List<EstablishmentType>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteEstaType()
            dropdownItemDao.insertEstaType(establishType)
        }
    }

    /**District*/
    val getDistrict: LiveData<List<DistrictEntity>> = dropdownItemDao.getDistrict()


    fun getAllDistrict(): List<DistrictEntity> {
        return dropdownItemDao.getAllDistrict()
    }

    fun insertDistrict(district: List<DistrictEntity>) {
        Log.d("TAG", "DistrictEntity: ${Gson().toJson(district)}")
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteDistrict()
            dropdownItemDao.insertDistrict(district)
        }
    }

    /**villages*/
    fun getVillages(): List<VillageEntity> {
        return dropdownItemDao.getAllVillages()
    }

    fun getVillagesWithID(districtID: String): List<VillageEntity> {
        return dropdownItemDao.getAllVillageWithID(districtID)
    }

    fun insertVillages(vill: List<VillageEntity>) {
        Log.d("TAG", "VillageEntity: ${Gson().toJson(vill)}")
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteVilage()
            dropdownItemDao.insertVillages(vill)
        }
    }

    /**Rship*/
    val getAllRshipType: LiveData<List<RshipTypeEntity>> = dropdownItemDao.getAllRshipType()
    fun insertRshipType(rship: List<RshipTypeEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteRshipType()
            dropdownItemDao.insertRshipType(rship)
        }
    }

    /**Asset types*/
    val getAllAssetType: LiveData<List<AssetTypeEntity>> = dropdownItemDao.getAllAssetType()
    fun insertAssetType(assType: List<AssetTypeEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteAssetType()
            dropdownItemDao.insertAssetType(assType)
        }
    }

    /**ID Types*/
    val getAllIDTypes: LiveData<List<IdentityTypeEntity>> = dropdownItemDao.getAllIdentityType()
    fun insertIDTypes(idType: List<IdentityTypeEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteIdentityType()
            dropdownItemDao.insertIdentityType(idType)
        }
    }

    /**Accomodation status*/
    val getAllAccommodationStatus: LiveData<List<AccStatusEntity>> =
        dropdownItemDao.getAllAccommodationStatus()

    fun insertAccommodationStatus(accStatus: List<AccStatusEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteAccommodationStatus()
            dropdownItemDao.insertAccommodationStatus(accStatus)
        }
    }

    /**Officer subbranches*/
    val getAllOfficerSubBranches: LiveData<List<SubBranchEntity>> =
        dropdownItemDao.getAllOfficerSubBranches()

    fun insertOfficerSubBranches(officerSubBranches: List<SubBranchEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            dropdownItemDao.deleteOfficerSubBranch()
            dropdownItemDao.insertOfficerSubBranches(officerSubBranches)
        }
    }

}