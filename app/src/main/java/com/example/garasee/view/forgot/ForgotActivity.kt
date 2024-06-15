package com.example.garasee.view.forgot

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.garasee.databinding.ActivityForgotBinding
import com.example.garasee.view.login.LoginActivity

class ForgotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
