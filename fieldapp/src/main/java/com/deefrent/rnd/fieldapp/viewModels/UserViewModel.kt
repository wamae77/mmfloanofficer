package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.repositories.UserRepo

class UserViewModel : ViewModel() {
    private var userRepo: UserRepo = UserRepo()

    /*fun getUserDetails(getUserDetailsBody: GetUserDetailsBody): LiveData<GetUserDetailsResponse> {
        return userRepo.getUserDetails(getUserDetailsBody)
    }*/
}