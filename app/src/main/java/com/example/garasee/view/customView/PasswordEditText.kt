package com.example.garasee.view.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import com.example.garasee.R

class PasswordEditText : AppCompatEditText {
    private var isPasswordVisible = false
    private var iconBounds = Rect()

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

        updateVisibilityIcon()

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Adjust the touch area to account for the padding
                if (event.rawX >= (right - iconBounds.width() - paddingEnd) &&
                    event.rawX <= (right - paddingEnd)) {
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

        // Move the cursor to the end of the text
        setSelection(text?.length ?: 0)

        invalidate()
    }

    private fun updateVisibilityIcon() {
        val icon = if (isPasswordVisible)
            R.drawable.baseline_visibility_24
        else
            R.drawable.baseline_visibility_off_24

        // Set the icon with padding
        val drawable = context.getDrawable(icon)
        drawable?.bounds?.let { iconBounds = it }

        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)

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
