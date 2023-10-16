package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.SavingAccountData
import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import kotlinx.android.synthetic.main.gender_dropdown_item_list.view.*


class SavingAccountAdapter(context: Context, list: List<SavingAccountData>) :
    ArrayAdapter<SavingAccountData>(context, 0, list) {
    var name1 = ""
    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val name = getItem(position)
        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.gender_dropdown_item_list,
            parent,
            false
        )
        val accNumber = name?.accountNo?.replace("(?<=.{3}).(?=.{3})".toRegex(), "*")
        val formatName = name?.accountName?.capitalizeWords
        /* val splited: List<String> = formatNmae?.split("\\s".toRegex())
         if( splited.count() == 2){
             val lastName=splited[1]
             name1=camelCase(lastName)
         }
         val firstName=splited[0]
         val name2=camelCase(firstName)
         val finalName= "$name2 $name1"*/

        view.tv_gender.text = "$formatName - A/C# $accNumber"

        return view
    }

}