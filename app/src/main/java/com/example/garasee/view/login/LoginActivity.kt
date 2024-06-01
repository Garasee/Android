package com.example.garasee.view.login

import android.animation.ObjectAnimator
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.garasee.databinding.ActivityLoginBinding
import com.example.garasee.R
import com.example.garasee.data.pref.UserModel
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val viewModelFactory = ViewModelFactory.getInstance(application as Application)
            viewModel = ViewModelProvider(this@LoginActivity, viewModelFactory).get(LoginViewModel::class.java)
        }

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
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            if (checkInput(email, password)) {
                viewModel.login(email, password).observe(this) { user ->
                    if (user != null) {
                        viewModel.saveSession(UserModel(user.userId, user.token, true))
                        showSuccessDialog()
                    } else {
                        showErrorDialog()
                    }
                }
            }
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
            setMessage("You have logged in. Let's share your story!")
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
            setMessage("Login failed. Make sure your email and password are correct!.")
            setIcon(R.drawable.baseline_error_outline_24)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

    }

}
