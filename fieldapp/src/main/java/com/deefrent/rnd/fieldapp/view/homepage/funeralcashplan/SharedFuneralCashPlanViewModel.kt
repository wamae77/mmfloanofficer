package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import android.util.Log
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.FindCustomerData
import request.Dependant
import java.util.ArrayList

class SharedFuneralCashPlanViewModel : ViewModel() {
    /**
     * Shared FindCustomer
     */
    val listFindCustomerByNameData =
        mutableListOf<FindCustomerData>().toSet().toMutableList()

    /**
     * Response
     */
    private var _dependantsList: ArrayList<Dependant> = arrayListOf()
    val dependantsList: List<Dependant> get() = _dependantsList.toSet().toMutableList()
    fun addDependantToList(dependant: Dependant) {
        _dependantsList.add(dependant)
        Log.e("DEPENDATS", "${_dependantsList}")
    }


    /**
     * Response
     */
    fun removeDependantToList(dependant: Dependant) {
        _dependantsList.remove(dependant)
        _dependantsList.addAll(_dependantsList)
        Log.e("DEPENDATS", "${_dependantsList}")
    }

}