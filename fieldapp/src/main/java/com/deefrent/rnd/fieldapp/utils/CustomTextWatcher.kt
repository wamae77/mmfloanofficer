package com.deefrent.rnd.fieldapp.utils

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by Tom Munyiri on 27/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */

class CustomTextWatcher(val textInputLayout: TextInputLayout): TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        textInputLayout.error = null
    }

    override fun afterTextChanged(s: Editable?) {

    }
}