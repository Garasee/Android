package com.example.garasee.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.garasee.R
import com.example.garasee.data.pref.UserModel
import com.example.garasee.databinding.ActivityLoginBinding
import com.example.garasee.di.Injection
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.view.forgot.ForgotActivity
import com.example.garasee.view.main.MainActivity
import com.example.garasee.view.signup.SignupActivity
import com.example.garasee.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val userPreference = Injection.provideUserPreference(applicationContext)
            token = userPreference.getToken().firstOrNull() ?: ""
            Log.d("LoginActivity", "Token: $token")

            if (token.isNotEmpty()) {
                navigateToMain()
                return@launch
            }

            val isLoggedIn = userPreference.isLoggedin().firstOrNull() ?: ""
            Log.d("LoginActivity", "IsLoggedIn: $isLoggedIn")


            val viewModelFactory = ViewModelFactory.getInstance(application as Application)
            viewModel = ViewModelProvider(this@LoginActivity, viewModelFactory)[LoginViewModel::class.java]
        }

        setupView()
        setupAction()
        playAnimation()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            if (checkInput(email, password)) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.login(email, password).observe(this) { user ->
                    binding.progressBar.visibility = View.GONE
                    if (user != null) {
                        viewModel.saveSession(UserModel(user.token, true))
                        showSuccessDialog()
                    } else {
                        showErrorDialog()
                    }
                }
            }
        }

        binding.signupnow.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun checkInput(email: String, password: String): Boolean {
        var isValid = true
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edLoginEmail.error = getString(R.string.invalid_email)
            isValid = false
        }
        if (password.isEmpty() || password.length < 8) {
            binding.edLoginPassword.error = getString(R.string.invalid_password)
            isValid = false
        }
        return isValid
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Hooray!")
            setMessage("You have logged in!")
            setIcon(R.drawable.outline_how_to_reg_24)
            setPositiveButton("OK") { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Oh no!")
            setMessage("Login failed. Make sure your email and password are correct!")
            setIcon(R.drawable.baseline_error_outline_24)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(100)
        val emailview = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(100)
        val passview = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(100)
        val forgot = ObjectAnimator.ofFloat(binding.forgotPassword, View.ALPHA, 1f).setDuration(100)
        val donthave = ObjectAnimator.ofFloat(binding.donthave, View.ALPHA, 1f).setDuration(100)
        val signupnow = ObjectAnimator.ofFloat(binding.signupnow, View.ALPHA, 1f).setDuration(100)
        val loginbuttonview = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(emailview, passview)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together, loginbuttonview, forgot, donthave, signupnow)
            start()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}
