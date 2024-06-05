package com.example.garasee.view.forgot

import android.animation.AnimatorSet
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
import com.example.garasee.R
import com.example.garasee.data.pref.UserModel
import com.example.garasee.databinding.ActivityForgotBinding
import com.example.garasee.databinding.ActivityLoginBinding
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.view.login.LoginActivity
import com.example.garasee.view.login.LoginViewModel
import com.example.garasee.view.main.MainActivity
import com.example.garasee.view.signup.SignupActivity
import com.example.garasee.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class ForgotActivity : AppCompatActivity() {
//    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityForgotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        lifecycleScope.launch {
//            val viewModelFactory = ViewModelFactory.getInstance(application as Application)
//            viewModel = ViewModelProvider(this@LoginActivity, viewModelFactory).get(LoginViewModel::class.java)
//        }

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
        binding.sendButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
//            if (checkInput(email)) {
//                viewModel.login(email, password).observe(this) { user ->
//                    if (user != null) {
//                        viewModel.saveSession(UserModel(user.userId, user.token, true))
//                        showSuccessDialog()
//                    } else {
//                        showErrorDialog()
//                    }
//                }
//            }
        }

        binding.signinnow.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun checkInput(email: String): Boolean {
        var isValid = true
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edLoginEmail.error = getString(R.string.invalid_email)
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
            setMessage("Login failed. Make sure your email and password are correct!.")
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
        val signinnow = ObjectAnimator.ofFloat(binding.signinnow, View.ALPHA, 1f).setDuration(100)
        val remember = ObjectAnimator.ofFloat(binding.remember, View.ALPHA, 1f).setDuration(100)
        val sendbuttonview = ObjectAnimator.ofFloat(binding.sendButton, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(emailview)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together, sendbuttonview, remember, signinnow)
            start()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}
