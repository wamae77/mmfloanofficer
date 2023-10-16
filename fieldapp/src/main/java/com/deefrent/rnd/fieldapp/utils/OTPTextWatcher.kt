package com.deefrent.rnd.fieldapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.deefrent.rnd.fieldapp.R

class OTPTextWatcher internal constructor(private val numberOfDigits: Int, private val previousView: View?, private val currentView: View, private val nextView: View?) : TextWatcher {
    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        if (numberOfDigits == 6){
            when (currentView.id) {
                R.id.codeOne -> if (text.length == 1) nextView!!.requestFocus()
                R.id.codeTwo -> if (text.length == 1) nextView!!.requestFocus()
                R.id.codeThree -> if (text.length == 1) nextView!!.requestFocus()
                R.id.codeFour -> if (text.length == 1) nextView!!.requestFocus()
                R.id.codeFive -> if (text.length == 1) nextView!!.requestFocus()
                //You can use EditText4 same as above to hide the keyboard
            }
        } else {
            when (currentView.id) {
                R.id.codeOne -> {
                    Log.e("BTNONE","Text changed. Length"+text.length.toString()+"NEXT"+nextView?.id.toString())
                    if (text.length == 1)  nextView!!.requestFocus()
                }
                R.id.codeTwo -> {
                    Log.e("BTNTWO","Text changed. Length"+text.length.toString()+"NEXT"+nextView?.id.toString())
                    if (text.length == 1) nextView!!.requestFocus() else previousView!!.requestFocus()
                }
                R.id.codeThree -> {
                    Log.e("BTNTHREE","Text changed. Length"+text.length.toString()+"NEXT"+nextView?.id.toString())
                    if (text.length == 1) nextView!!.requestFocus() else previousView!!.requestFocus()
                }
                R.id.codeFour -> {
                    Log.e("BTNFOUR","Text changed. Length"+text.length.toString()+"NEXT"+nextView?.id.toString())
                    if (text.length == 1) currentView.requestFocus() else previousView!!.requestFocus()
                }

//                R.id.codeOneTwo -> if (text.length == 1)  nextView!!.requestFocus()
//                R.id.codeTwoTwo -> if (text.length == 1) nextView!!.requestFocus() else previousView!!.requestFocus()
//                R.id.codeThreeTwo -> if (text.length == 1) nextView!!.requestFocus() else previousView!!.requestFocus()
//                R.id.codeFourTwo -> if (text.length == 1) currentView.requestFocus() else previousView!!.requestFocus()
//
//                R.id.codeOneThree -> if (text.length == 1)  nextView!!.requestFocus()
//                R.id.codeTwoThree -> if (text.length == 1) nextView!!.requestFocus() else previousView!!.requestFocus()
//                R.id.codeThreeThree -> if (text.length == 1) nextView!!.requestFocus() else previousView!!.requestFocus()
//                R.id.codeFourThree -> if (text.length == 1) currentView.requestFocus() else previousView!!.requestFocus()
            }
        }

    }

    override fun beforeTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) {
    }

    override fun onTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) {
    }

}