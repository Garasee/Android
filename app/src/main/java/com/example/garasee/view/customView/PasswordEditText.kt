package com.example.garasee.view.customView

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.garasee.R

class PasswordEditText : AppCompatEditText {
    private var isPasswordVisible = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        updateVisibilityIcon()

        setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= right - compoundDrawablesRelative[2].bounds.width()) {
                    togglePasswordVisibility()
                    updateVisibilityIcon()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
            }
        })
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        updateVisibilityIcon()

        transformationMethod = if (isPasswordVisible)
            null
        else
            PasswordTransformationMethod.getInstance()

        invalidate()
    }

    private fun updateVisibilityIcon() {
        val icon = if (isPasswordVisible)
            R.drawable.baseline_visibility_24
        else
            R.drawable.baseline_visibility_off_24

        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, icon, 0)

        requestLayout()
        invalidate()
    }

    private fun validatePassword(password: String) {
        if (password.isEmpty()) {
            error = null
        } else if (password.length < 8) {
            error = context.getString(R.string.invalid_password)
        } else {
            error = null
        }
    }
}