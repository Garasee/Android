package com.example.garasee.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.garasee.R
import com.example.garasee.data.api.ErrorResponse
import com.example.garasee.databinding.ActivitySignupBinding
import com.example.garasee.repository.SignupRepository
import com.example.garasee.view.login.LoginActivity
import com.example.garasee.view.welcome.WelcomeActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import android.text.TextWatcher

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupRepository: SignupRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences =
            applicationContext.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN_KEY", "") ?: ""
        signupRepository = SignupRepository(token)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.edRegisterConfirmpassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordMatch()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val ccp = binding.countryCodeHolder
            val phonenumber = binding.edRegisterPhone
            ccp.registerCarrierNumberEditText(phonenumber)
            val fullphonenumber = ccp.fullNumber

            Log.d("phone number", "Number: $fullphonenumber")

            if (validatePasswordMatch()) {
                lifecycleScope.launch {
                    try {
                        val response = signupRepository.register(name, email, password)
                        val message = response.message
                        if (!response.error) {
                            showAlertDialog(
                                title = "Hooray!",
                                errorMessage = "Your account has been created.",
                                type = DialogType.SUCCESS,
                                icon = R.drawable.outline_how_to_reg_24,
                                doAction = {
                                    finish()
                                }
                            )
                        } else {
                            showAlertDialog(
                                title = "Oh no!",
                                errorMessage = message,
                                type = DialogType.ERROR,
                                icon = R.drawable.baseline_error_outline_24,
                                doAction = {}
                            )
                        }
                    } catch (e: HttpException) {
                        val jsonInString = e.response()?.errorBody()?.string()
                        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                        val errorMessage = errorBody.message
                        showAlertDialog(
                            title = "Oh no!",
                            errorMessage = errorMessage ?: "An unexpected error occurred.",
                            type = DialogType.ERROR,
                            icon = R.drawable.baseline_error_outline_24,
                            doAction = {}
                        )
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.password_do_not_match), Toast.LENGTH_SHORT).show()
            }
        }

        binding.signinnow.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun validatePasswordMatch(): Boolean {
        val password = binding.edRegisterPassword.text.toString()
        val confirmPassword = binding.edRegisterConfirmpassword.text.toString()
        return if (password == confirmPassword) {
            binding.edRegisterConfirmpassword.error = null
            true
        } else {
            binding.edRegisterConfirmpassword.error = getString(R.string.password_do_not_match)
            false
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(100)
        val nameview = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(100)
        val phoneview = ObjectAnimator.ofFloat(binding.edRegisterPhone, View.ALPHA, 1f).setDuration(100)
        val cityview = ObjectAnimator.ofFloat(binding.edRegisterCity, View.ALPHA, 1f).setDuration(100)
        val emailview = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(100)
        val passview = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val confirmpassview = ObjectAnimator.ofFloat(binding.edRegisterConfirmpassword, View.ALPHA, 1f).setDuration(100)
        val registerbuttonview = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val alreadyview = ObjectAnimator.ofFloat(binding.already, View.ALPHA, 1f).setDuration(100)
        val signupnowview = ObjectAnimator.ofFloat(binding.signinnow, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(nameview, phoneview, cityview, emailview, passview, confirmpassview)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together, registerbuttonview, alreadyview, signupnowview)
            start()
        }
    }

    enum class DialogType {
        ERROR,
        SUCCESS
    }

    private fun showAlertDialog(
        title: String,
        errorMessage: String,
        icon: Int,
        type: DialogType,
        doAction: () -> Unit
    ) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(errorMessage)
            setIcon(icon)
            setPositiveButton("OK") { _, _ ->
                if (type == DialogType.SUCCESS) {
                    doAction()
                }
            }
        }

        val alertDialog: AlertDialog = builder.create().apply {
            setCancelable(false)
            show()
        }
    }
}
