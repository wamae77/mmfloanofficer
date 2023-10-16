package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Keep
data class DropdownItemResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Keep
data class Data(
    @SerializedName("accommodationStatus")
    val accommodationStatus: List<AccommodationStatu>,
    @SerializedName("assetTypes")
    val assetTypes: List<AssetType>,
    @SerializedName("businessTypes")
    val businessTypes: List<BusinessType>,
    @SerializedName("districts")
    val districts: List<District>,
    @SerializedName("economicSectors")
    val economicSectors: List<EconomicSector>,
    @SerializedName("educationLevels")
    val educationLevels: List<EducationLevel>,
    @SerializedName("empStatus")
    val empStatus: List<EmpStatu>,
    @SerializedName("establishmentTypes")
    val establishmentTypes: List<EstablishmentType>,
    @SerializedName("eventTypes")
    val eventTypes: List<Any>,
    @SerializedName("expenseTypes")
    val expenseTypes: List<Any>,
    @SerializedName("feedbackTypes")
    val feedbackTypes: List<FeedbackType>,
    @SerializedName("genders")
    val genders: List<Gender>,
    @SerializedName("identifies")
    val identifies: List<Identify>,
    @SerializedName("identityTypes")
    val identityTypes: List<IdentityType>,
    @SerializedName("occupations")
    val occupations: List<Any>,
    @SerializedName("relationshipTypes")
    val relationshipTypes: List<RelationshipType>,
    @SerializedName("officerSubBranches")
    val officerSubBranches: List<OfficerSubBranch>
)

@Keep
data class AccommodationStatu(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

@Keep
data class AssetType(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

@Keep
@Entity(tableName = "business_type_table")
data class BusinessType(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

@Keep
data class District(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

@Keep
@Entity(tableName = "economic_sector_table")
data class EconomicSector(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

@Keep
@Entity(tableName = "education_level_table")
data class EducationLevel(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

@Keep
data class EmpStatu(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

@Entity(tableName = "establishment_type_table")
data class EstablishmentType(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

data class FeedbackType(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

@Entity(tableName = "gender_entity_table")
data class Gender(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

data class Identify(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class IdentityType(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class RelationshipType(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class OfficerSubBranch(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)