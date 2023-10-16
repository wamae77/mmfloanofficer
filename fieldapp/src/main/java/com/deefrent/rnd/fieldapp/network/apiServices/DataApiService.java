package com.deefrent.rnd.fieldapp.network.apiServices;

import com.deefrent.rnd.fieldapp.models.assets.AssetsResponse;
import com.deefrent.rnd.fieldapp.models.bankBranches.BankBranchesResponse;
import com.deefrent.rnd.fieldapp.models.banks.BanksResponse;
import com.deefrent.rnd.fieldapp.models.businessTypes.BusinessTypesResponse;
import com.deefrent.rnd.fieldapp.models.complainTypes.ComplainTypesResponse;
import com.deefrent.rnd.fieldapp.models.constituencies.ConstituenciesResponse;
import com.deefrent.rnd.fieldapp.models.counties.CountiesResponse;
import com.deefrent.rnd.fieldapp.models.employmentTypes.EmploymentTypesResponse;
import com.deefrent.rnd.fieldapp.models.idType.GetIDTypeResponse;
import com.deefrent.rnd.fieldapp.models.kcbBranches.KcbBranchesResponse;
import com.deefrent.rnd.fieldapp.models.liquidationTypes.LiquidationTypesResponse;
import com.deefrent.rnd.fieldapp.models.merchantAgentTypes.MerchantAgentTypesResponse;
import com.deefrent.rnd.fieldapp.models.personalAccountTypes.PersonalAccountTypeResponse;
import com.deefrent.rnd.fieldapp.models.userTypes.UserTypeResponse;
import com.deefrent.rnd.fieldapp.models.wards.WardsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DataApiService {
    @GET("customer/get-user-types")
    Call<UserTypeResponse> getUserTypes();

    @GET("customer/get-account-types")
    Call<PersonalAccountTypeResponse> getAccountTypes();

    @GET("customer/get-kcb-branches")
    Call<KcbBranchesResponse> getKcbBranches();

    @GET("merchant/get-branch-by-bank-code/{bankCode}")
    Call<BankBranchesResponse> getBankBranches(@Path("bankCode")String bankCode);

    @GET("customer/get-employment-types")
    Call<EmploymentTypesResponse> getEmploymentTypes();

    @GET("merchant/get-business-type")
    Call<BusinessTypesResponse> getBusinessTypes();

    @GET("merchant/get-liquidation-type")
    Call<LiquidationTypesResponse> getLiquidationTypes();

    @GET("merchant/get-banks")
    Call<BanksResponse> getBanks();

    @GET("customer/get-counties")
    Call<CountiesResponse> getCounties();

    @GET("merchant/get-constituency-by-county-code/{countyCode}")
    Call<ConstituenciesResponse> getConstituencies(@Path("countyCode")int countyCode);

    @GET("merchant/get-ward-by-constituency-code/{constituencyCode}")
    Call<WardsResponse> getWards(@Path("constituencyCode")int constituencyCode);

    @GET("merchant/get-merch-agent-account-types")
    Call<MerchantAgentTypesResponse> getMerchantAgentTypes();

    @GET("customer/get-assets")
    Call<AssetsResponse> getAssets();

    @GET("customer/get-complain-types")
    Call<ComplainTypesResponse> getComplainTypes();

    @GET("customer/get-identification-type")
    Call<GetIDTypeResponse> getIDTypes();
}
