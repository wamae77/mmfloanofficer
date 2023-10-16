package com.deefrent.rnd.fieldapp.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.deefrent.rnd.fieldapp.network.models.BusinessType;
import com.deefrent.rnd.fieldapp.network.models.EconomicSector;
import com.deefrent.rnd.fieldapp.network.models.EducationLevel;
import com.deefrent.rnd.fieldapp.network.models.EstablishmentType;
import com.deefrent.rnd.fieldapp.network.models.Gender;
import com.deefrent.rnd.fieldapp.room.daos.AssessCustomerDao;
import com.deefrent.rnd.fieldapp.room.daos.CustomerDetailsDao;
import com.deefrent.rnd.fieldapp.room.daos.DropdownItemDao;
import com.deefrent.rnd.fieldapp.room.daos.GenderDao;
import com.deefrent.rnd.fieldapp.room.daos.IndividualAccountDetailsDao;
import com.deefrent.rnd.fieldapp.room.daos.MerchantAgentDetailsDao;
import com.deefrent.rnd.fieldapp.room.entities.AccStatusEntity;
import com.deefrent.rnd.fieldapp.room.entities.AssessBorrowing;
import com.deefrent.rnd.fieldapp.room.entities.AssessCollateral;
import com.deefrent.rnd.fieldapp.room.entities.AssessCustomerDocsEntity;
import com.deefrent.rnd.fieldapp.room.entities.AssessCustomerEntity;
import com.deefrent.rnd.fieldapp.room.entities.AssessGuarantor;
import com.deefrent.rnd.fieldapp.room.entities.AssessHouseholdMemberEntity;
import com.deefrent.rnd.fieldapp.room.entities.AssetTypeEntity;
import com.deefrent.rnd.fieldapp.room.entities.Collateral;
import com.deefrent.rnd.fieldapp.room.entities.CustomerDetailsEntity;
import com.deefrent.rnd.fieldapp.room.entities.CustomerDocsEntity;
import com.deefrent.rnd.fieldapp.room.entities.DistrictEntity;
import com.deefrent.rnd.fieldapp.room.entities.EmploymentEntity;
import com.deefrent.rnd.fieldapp.room.entities.Guarantor;
import com.deefrent.rnd.fieldapp.room.entities.HouseholdMemberEntity;
import com.deefrent.rnd.fieldapp.room.entities.IdentifyEntity;
import com.deefrent.rnd.fieldapp.room.entities.IdentityTypeEntity;
import com.deefrent.rnd.fieldapp.room.entities.IndividualAccountDetails;
import com.deefrent.rnd.fieldapp.room.entities.MerchantAgentDetails;
import com.deefrent.rnd.fieldapp.room.entities.OccupationEntity;
import com.deefrent.rnd.fieldapp.room.entities.OtherBorrowing;
import com.deefrent.rnd.fieldapp.room.entities.RshipTypeEntity;
import com.deefrent.rnd.fieldapp.room.entities.SubBranchEntity;
import com.deefrent.rnd.fieldapp.room.entities.VillageEntity;

@Database(
        entities = {
                IndividualAccountDetails.class,
                MerchantAgentDetails.class,
                EducationLevel.class,
                Gender.class,
                EmploymentEntity.class,
                IdentifyEntity.class,
                BusinessType.class,
                EconomicSector.class,
                EstablishmentType.class,
                DistrictEntity.class,
                VillageEntity.class,
                RshipTypeEntity.class,
                AssetTypeEntity.class,
                IdentityTypeEntity.class,
                AccStatusEntity.class,
                OccupationEntity.class,
                CustomerDetailsEntity.class,
                Guarantor.class,
                Collateral.class,
                OtherBorrowing.class,
                AssessCustomerEntity.class,
                HouseholdMemberEntity.class,
                CustomerDocsEntity.class,
                AssessHouseholdMemberEntity.class,
                AssessCollateral.class,
                AssessGuarantor.class,
                AssessBorrowing.class,
                AssessCustomerDocsEntity.class,
                SubBranchEntity.class
        },
        version = 3,
        exportSchema = false
)
public abstract class FieldAppDatabase extends RoomDatabase {
    private static FieldAppDatabase fieldAppDatabase;

    public static synchronized FieldAppDatabase getFieldAppDatabase(Context context) {
        if (fieldAppDatabase == null) {
            fieldAppDatabase = Room.databaseBuilder(
                            context,
                            FieldAppDatabase.class,
                            "field_app_db"
                    ).addMigrations(MIGRATION_2_3)
                    //.fallbackToDestructiveMigration()
                    .build();
        }
        return fieldAppDatabase;
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL("CREATE TABLE officer_subbranch_table (id INTEGER, name TEXT, PRIMARY KEY(id))");
            //add sub-branch id to CustomerDetailsEntity
            database.execSQL("ALTER TABLE CustomerDetailsEntity ADD subBranchId TEXT NOT NULL DEFAULT ''");
            //add sub-branch id to AssessCustomerEntity
            database.execSQL("ALTER TABLE AssessCustomerEntity ADD subBranchId TEXT NOT NULL DEFAULT ''");
            //add sub-branch to AssessCustomerEntity
            database.execSQL("ALTER TABLE AssessCustomerEntity ADD subBranch TEXT NOT NULL DEFAULT ''");
        }
    };

    public abstract IndividualAccountDetailsDao individualAccountDetailsDao();

    public abstract MerchantAgentDetailsDao merchantAgentDetailsDao();

    public abstract GenderDao genderDao();

    public abstract DropdownItemDao dropdownItemDao();

    public abstract CustomerDetailsDao customerDetailsDao();

    public abstract AssessCustomerDao assessCustomerDao();

}
