package com.deefrent.rnd.fieldapp.repositories

class UserRepo {
    /*private var userApiService: UserApiService =
        UserDetailsApiClient.getRetrofitUserDetails()?.create(UserApiService::class.java) !!

    fun getUserDetails(getUserDetailsBody: GetUserDetailsBody): LiveData<GetUserDetailsResponse> {
        val data = MutableLiveData<GetUserDetailsResponse>()
        userApiService.getUserDetails(getUserDetailsBody)
            .enqueue(object : Callback<GetUserDetailsResponse> {
                override fun onResponse(
                    call: Call<GetUserDetailsResponse>,
                    response: Response<GetUserDetailsResponse>
                ) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<GetUserDetailsResponse>, t: Throwable) {
                    data.value = null
                    Log.d("Data Repo", t.localizedMessage)
                }
            })
        return data
    }*/
}