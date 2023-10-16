package com.deefrent.rnd.fieldapp.room.repos

import androidx.lifecycle.LiveData
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.Gender
import com.deefrent.rnd.fieldapp.room.daos.GenderDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GenderRepository(private val genderDao: GenderDao) {
    val  retrieveAllGender:LiveData<List<Gender>> = genderDao.getAllGender()
    fun getGenderNameAsync()= FieldAgentApi.retrofitService.getDropdownItemsAsync()
    fun insertItems(genderItems: List<Gender>){
        GlobalScope.launch(Dispatchers.IO) {
            genderDao.insertGender(genderItems)
        }
    }
    fun deleteGender(){
        GlobalScope.launch(Dispatchers.IO) {
            genderDao.deleteGender()
        }
    }
    fun retrieveGender():List<Gender>{
            return genderDao.retrieveGender()
    }


}