package com.deefrent.rnd.fieldapp.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.room.entities.*

@Dao
interface DropdownItemDao {
    /**genders*/
    @Query("SELECT * from gender_entity_table")
    fun getAllGender():LiveData<List<Gender>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGender(genderItems: List<Gender>)
    @Query("DELETE from gender_entity_table")
    fun deleteGender():Int
    @Query("SELECT * from gender_entity_table")
    fun retrieveGender():List<Gender>
    /**education level*/
    @Query("SELECT * from education_level_table")
    fun getAllEduLevel(): LiveData<List<EducationLevel>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEduLevel(eduLevel: List<EducationLevel>)
    @Query("DELETE from education_level_table")
    fun deleteEduLevel():Int
    @Query("SELECT * from education_level_table")
    fun retrieveEduLevel():List<EducationLevel>
    /**how did you hear abt us(Identifiers)*/
    @Query("SELECT * from identifier_table")
    fun getAllIdentifiers(): LiveData<List<IdentifyEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIdentifiers(eduLevel: List<IdentifyEntity>)
    @Query("DELETE from identifier_table")
    fun deleteIdentifiers():Int

    /**emplyment status*/
    @Query("DELETE from EmploymentEntity")
    fun deleteEmpStatus():Int
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEmpStatus(emp: List<EmploymentEntity>)
    @Query("SELECT * from EmploymentEntity")
    fun getAllEmpStatus(): List<EmploymentEntity>
    @Query("SELECT * from EmploymentEntity")
    fun getEmpStatus(): LiveData<List<EmploymentEntity>>

    /**Occupation status*/
    @Query("DELETE from OccupationEntity")
    fun deleteOccupation():Int
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOccupation(emp: List<OccupationEntity>)
    @Query("SELECT * from OccupationEntity")
    fun getOccupation(): LiveData<List<OccupationEntity>>


    /**type of bzness*/
    @Query("SELECT * from business_type_table")
    fun getAllBznessType(): LiveData<List<BusinessType>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBznessType(bType: List<BusinessType>)
    @Query("DELETE from business_type_table")
    fun deleteBznessType():Int
    /**economic sector*/
    @Query("SELECT * from economic_sector_table")
    fun getAllEconomicSector(): LiveData<List<EconomicSector>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEconomicSector(ecoSector: List<EconomicSector>)
    @Query("DELETE from economic_sector_table")
    fun deleteEconomicSector():Int
    /**type of establishment*/
    @Query("SELECT * from establishment_type_table")
    fun getAllEstaType(): LiveData<List<EstablishmentType>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEstaType(estabshType: List<EstablishmentType>)
    @Query("DELETE from establishment_type_table")
    fun deleteEstaType():Int
    /**district*/
    @Query("SELECT * from district_table")
    fun getDistrict(): LiveData<List<DistrictEntity>>

    @Query("SELECT * from district_table")
    fun getAllDistrict(): List<DistrictEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistrict(district: List<DistrictEntity>)
    @Query("DELETE from district_table")
    fun deleteDistrict():Int

    /**Villages*/
    @Query("SELECT * from VillageEntity")
    fun getAllVillages(): List<VillageEntity>
    @Query("SELECT * from VillageEntity Where districtId =:districtID")
    fun getAllVillageWithID(districtID:String): List<VillageEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVillages(district: List<VillageEntity>)
    @Query("DELETE from VillageEntity")
    fun deleteVilage():Int

    /**Rship*/
    @Query("SELECT * from rship_type_table")
    fun getAllRshipType(): LiveData<List<RshipTypeEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRshipType(rshipType: List<RshipTypeEntity>)
    @Query("DELETE from rship_type_table")
    fun deleteRshipType():Int
    /**Asset types*/
    @Query("SELECT * from asset_type_table")
    fun getAllAssetType(): LiveData<List<AssetTypeEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAssetType(aType: List<AssetTypeEntity>)
    @Query("DELETE from asset_type_table")
    fun deleteAssetType():Int
    /**ID Types*/
    @Query("SELECT * from identity_type_table")
    fun getAllIdentityType(): LiveData<List<IdentityTypeEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIdentityType(iType: List<IdentityTypeEntity>)
    @Query("DELETE from identity_type_table")
    fun deleteIdentityType():Int
    /**Accomodation status*/
    @Query("SELECT * from accommodation_status_table")
    fun getAllAccommodationStatus(): LiveData<List<AccStatusEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccommodationStatus(accStatus: List<AccStatusEntity>)
    @Query("DELETE from accommodation_status_table")
    fun deleteAccommodationStatus():Int

    /**Subbranch*/
    @Query("SELECT * from officer_subbranch_table")
    fun getAllOfficerSubBranches(): LiveData<List<SubBranchEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOfficerSubBranches(officerSubBranches: List<SubBranchEntity>)
    @Query("DELETE from officer_subbranch_table")
    fun deleteOfficerSubBranch():Int
}