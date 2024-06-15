package com.example.garasee.view.customView

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.garasee.R

class NumberEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        inputType = InputType.TYPE_CLASS_NUMBER
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateNumber(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateNumber(number: String) {
        error = if (number.isEmpty()) {
            null
        } else if (!number.matches("\\d+".toRegex())) {
            context.getString(R.string.invalid_number)
        } else {
            null
        }
    }
}
