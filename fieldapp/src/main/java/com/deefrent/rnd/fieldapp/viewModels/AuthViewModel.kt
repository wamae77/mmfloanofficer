package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.repositories.AuthRepo
import com.deefrent.rnd.fieldapp.responses.LoginResponse

class AuthViewModel : ViewModel() {
    private var authRepo: AuthRepo = AuthRepo()
    fun loginUser(username: String, password: String, grant_type: String): LiveData<LoginResponse> {
        return authRepo.loginUser(username, password, grant_type)
    }
}