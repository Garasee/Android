package com.example.garasee.view.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.garasee.R

class NameEditText : AppCompatEditText {
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
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                validateName(s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateName(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                validateName(s.toString())
            }
        })
    }

    private fun validateName(name: String) {
        error = if (name.isEmpty()) {
            null
        } else if (name.isEmpty()) {
            context.getString(R.string.invalid_name)
        } else {
            null
        }
    }
}
