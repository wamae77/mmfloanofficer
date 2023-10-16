package com.deefrent.rnd.fieldapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deefrent.rnd.fieldapp.models.assets.AssetsResponse;
import com.deefrent.rnd.fieldapp.models.bankBranches.BankBranchesResponse;
import com.deefrent.rnd.fieldapp.models.banks.BanksResponse;
import com.deefrent.rnd.fieldapp.models.businessTypes.BusinessTypesResponse;
import com.deefrent.rnd.fieldapp.models.complainTypes.ComplainTypesResponse;
import com.deefrent.rnd.fieldapp.models.constituencies.ConstituenciesResponse;
import com.deefrent.rnd.fieldapp.network.apiServices.DataApiService;
import com.deefrent.rnd.fieldapp.models.counties.CountiesResponse;
import com.deefrent.rnd.fieldapp.models.employmentTypes.EmploymentTypesResponse;
import com.deefrent.rnd.fieldapp.models.idType.GetIDTypeResponse;
import com.deefrent.rnd.fieldapp.models.kcbBranches.KcbBranchesResponse;
import com.deefrent.rnd.fieldapp.models.liquidationTypes.LiquidationTypesResponse;
import com.deefrent.rnd.fieldapp.models.merchantAgentTypes.MerchantAgentTypesResponse;
import com.deefrent.rnd.fieldapp.models.personalAccountTypes.PersonalAccountTypeResponse;
import com.deefrent.rnd.fieldapp.models.userTypes.UserTypeResponse;
import com.deefrent.rnd.fieldapp.models.wards.WardsResponse;
import com.deefrent.rnd.fieldapp.network.apiClients.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataRepo {
    private DataApiService dataApiService;

    public DataRepo() {
        dataApiService = ApiClient.Companion.getRetrofit().create(DataApiService.class);
    }

    public LiveData<UserTypeResponse> getUserTypes() {
        MutableLiveData<UserTypeResponse> data = new MutableLiveData<>();
        dataApiService.getUserTypes().enqueue(new Callback<UserTypeResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserTypeResponse> call, @NonNull Response<UserTypeResponse> response) {
                data.setValue(response.body());
                /*try {
                    Log.d("Data Repo",""+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Data Repo", "onResponse:"+e.getLocalizedMessage());
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<UserTypeResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<PersonalAccountTypeResponse> getAccountTypes() {
        MutableLiveData<PersonalAccountTypeResponse> data = new MutableLiveData<>();
        dataApiService.getAccountTypes().enqueue(new Callback<PersonalAccountTypeResponse>() {
            @Override
            public void onResponse(@NonNull Call<PersonalAccountTypeResponse> call, @NonNull Response<PersonalAccountTypeResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<PersonalAccountTypeResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<KcbBranchesResponse> getKcbBranches() {
        MutableLiveData<KcbBranchesResponse> data = new MutableLiveData<>();
        dataApiService.getKcbBranches().enqueue(new Callback<KcbBranchesResponse>() {
            @Override
            public void onResponse(@NonNull Call<KcbBranchesResponse> call, @NonNull Response<KcbBranchesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<KcbBranchesResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<BankBranchesResponse> getBankBranches(String bankCode) {
        MutableLiveData<BankBranchesResponse> data = new MutableLiveData<>();
        dataApiService.getBankBranches(bankCode).enqueue(new Callback<BankBranchesResponse>() {
            @Override
            public void onResponse(@NonNull Call<BankBranchesResponse> call, @NonNull Response<BankBranchesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<BankBranchesResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<EmploymentTypesResponse> getEmploymentTypes() {
        MutableLiveData<EmploymentTypesResponse> data = new MutableLiveData<>();
        dataApiService.getEmploymentTypes().enqueue(new Callback<EmploymentTypesResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmploymentTypesResponse> call, @NonNull Response<EmploymentTypesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EmploymentTypesResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<BusinessTypesResponse> getBusinessTypes() {
        MutableLiveData<BusinessTypesResponse> data = new MutableLiveData<>();
        dataApiService.getBusinessTypes().enqueue(new Callback<BusinessTypesResponse>() {
            @Override
            public void onResponse(@NonNull Call<BusinessTypesResponse> call, @NonNull Response<BusinessTypesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<BusinessTypesResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<LiquidationTypesResponse> getLiquidationTypes() {
        MutableLiveData<LiquidationTypesResponse> data = new MutableLiveData<>();
        dataApiService.getLiquidationTypes().enqueue(new Callback<LiquidationTypesResponse>() {
            @Override
            public void onResponse(@NonNull Call<LiquidationTypesResponse> call, @NonNull Response<LiquidationTypesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<LiquidationTypesResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<BanksResponse> getBanks() {
        MutableLiveData<BanksResponse> data = new MutableLiveData<>();
        dataApiService.getBanks().enqueue(new Callback<BanksResponse>() {
            @Override
            public void onResponse(@NonNull Call<BanksResponse> call, @NonNull Response<BanksResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<BanksResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<CountiesResponse> getCounties() {
        MutableLiveData<CountiesResponse> data = new MutableLiveData<>();
        dataApiService.getCounties().enqueue(new Callback<CountiesResponse>() {
            @Override
            public void onResponse(@NonNull Call<CountiesResponse> call, @NonNull Response<CountiesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<CountiesResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<ConstituenciesResponse> getConstituencies(int countyCode) {
        MutableLiveData<ConstituenciesResponse> data = new MutableLiveData<>();
        dataApiService.getConstituencies(countyCode).enqueue(new Callback<ConstituenciesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ConstituenciesResponse> call, @NonNull Response<ConstituenciesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ConstituenciesResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<WardsResponse> getWards(int constituencyCode) {
        MutableLiveData<WardsResponse> data = new MutableLiveData<>();
        dataApiService.getWards(constituencyCode).enqueue(new Callback<WardsResponse>() {
            @Override
            public void onResponse(@NonNull Call<WardsResponse> call, @NonNull Response<WardsResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<WardsResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<MerchantAgentTypesResponse> getMerchantAgentTypes() {
        MutableLiveData<MerchantAgentTypesResponse> data = new MutableLiveData<>();
        dataApiService.getMerchantAgentTypes().enqueue(new Callback<MerchantAgentTypesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MerchantAgentTypesResponse> call, @NonNull Response<MerchantAgentTypesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<MerchantAgentTypesResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<AssetsResponse> getAssets() {
        MutableLiveData<AssetsResponse> data = new MutableLiveData<>();
        dataApiService.getAssets().enqueue(new Callback<AssetsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AssetsResponse> call, @NonNull Response<AssetsResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<AssetsResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<ComplainTypesResponse> getComplainTypes() {
        MutableLiveData<ComplainTypesResponse> data = new MutableLiveData<>();
        dataApiService.getComplainTypes().enqueue(new Callback<ComplainTypesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ComplainTypesResponse> call, @NonNull Response<ComplainTypesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ComplainTypesResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }

    public LiveData<GetIDTypeResponse> getIDTypes() {
        MutableLiveData<GetIDTypeResponse> data = new MutableLiveData<>();
        dataApiService.getIDTypes().enqueue(new Callback<GetIDTypeResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetIDTypeResponse> call, @NonNull Response<GetIDTypeResponse> response) {
                data.setValue(response.body());
                /*try {
                    Log.d("Data Repo",""+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Data Repo", "onResponse:"+e.getLocalizedMessage());
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<GetIDTypeResponse> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d("Data Repo", t.getLocalizedMessage());
            }
        });
        return data;
    }
}
