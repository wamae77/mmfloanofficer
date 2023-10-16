package com.deefrent.rnd.fieldapp.data

data class CustomData(
    val title:String,
    val items:ArrayList<Pair<String,String>>,
    val action:CustomAction
)
enum class CustomAction{
    CustomerDetails,AdditionalDetails,BusinessDetails,BusinessAddress,ResidentialDetails,Nextofkin,addHouseholdMembers,
    AddExpenses,AddIncome
}
