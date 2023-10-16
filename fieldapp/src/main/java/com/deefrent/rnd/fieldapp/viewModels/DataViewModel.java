package com.deefrent.rnd.fieldapp.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.deefrent.rnd.fieldapp.models.assets.AssetsResponse;
import com.deefrent.rnd.fieldapp.models.bankBranches.BankBranchesResponse;
import com.deefrent.rnd.fieldapp.models.banks.BanksResponse;
import com.deefrent.rnd.fieldapp.models.businessTypes.BusinessTypesResponse;
import com.deefrent.rnd.fieldapp.models.complainTypes.ComplainTypesResponse;
import com.deefrent.rnd.fieldapp.models.constituencies.ConstituenciesResponse;
import com.deefrent.rnd.fieldapp.repositories.DataRepo;
import com.deefrent.rnd.fieldapp.models.counties.CountiesResponse;
import com.deefrent.rnd.fieldapp.models.employmentTypes.EmploymentTypesResponse;
import com.deefrent.rnd.fieldapp.models.idType.GetIDTypeResponse;
import com.deefrent.rnd.fieldapp.models.kcbBranches.KcbBranchesResponse;
import com.deefrent.rnd.fieldapp.models.liquidationTypes.LiquidationTypesResponse;
import com.deefrent.rnd.fieldapp.models.merchantAgentTypes.MerchantAgentTypesResponse;
import com.deefrent.rnd.fieldapp.models.personalAccountTypes.PersonalAccountTypeResponse;
import com.deefrent.rnd.fieldapp.models.userTypes.UserTypeResponse;
import com.deefrent.rnd.fieldapp.models.wards.WardsResponse;

public class DataViewModel extends ViewModel {
    private DataRepo dataRepo;

    public DataViewModel() {
        dataRepo = new DataRepo();
    }

    public LiveData<UserTypeResponse> fetchUserTypes() {
        return dataRepo.getUserTypes();
    }

    public LiveData<PersonalAccountTypeResponse> fetchAccountTypes() {
        return dataRepo.getAccountTypes();
    }

    public LiveData<KcbBranchesResponse> fetchKcbBranches() {
        return dataRepo.getKcbBranches();
    }

    public LiveData<BankBranchesResponse> fetchBankBranches(String bankCode) {
        return dataRepo.getBankBranches(bankCode);
    }

    public LiveData<EmploymentTypesResponse> fetchEmploymentTypes() {
        return dataRepo.getEmploymentTypes();
    }

    public LiveData<BusinessTypesResponse> fetchBusinessTypes(){
        return dataRepo.getBusinessTypes();
    }

    public LiveData<LiquidationTypesResponse> fetchLiquidationTypes(){
        return dataRepo.getLiquidationTypes();
    }

    public LiveData<BanksResponse> fetchBanks(){
        return dataRepo.getBanks();
    }

    public LiveData<CountiesResponse> fetchCounties(){
        return dataRepo.getCounties();
    }

    public LiveData<ConstituenciesResponse> fetchConstituencies(int countyCode) {
        return dataRepo.getConstituencies(countyCode);
    }

    public LiveData<WardsResponse> fetchWards(int constituencyCode) {
        return dataRepo.getWards(constituencyCode);
    }

    public LiveData<MerchantAgentTypesResponse> fetchMerchantAgentTypes() {
        return dataRepo.getMerchantAgentTypes();
    }

    public LiveData<AssetsResponse> fetchAssets() {
        return dataRepo.getAssets();
    }

    public LiveData<ComplainTypesResponse> fetchComplainTypes() {
        return dataRepo.getComplainTypes();
    }

    public LiveData<GetIDTypeResponse> fetchIDTypes() {
        return dataRepo.getIDTypes();
    }
}
