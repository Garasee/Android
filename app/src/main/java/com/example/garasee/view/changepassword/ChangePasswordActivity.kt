package com.example.garasee.view.changepassword

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.garasee.R
import com.example.garasee.data.api.ErrorResponse
import com.example.garasee.databinding.ActivityChangePasswordBinding
import com.example.garasee.di.Injection
import com.example.garasee.repository.UserRepository
import com.example.garasee.view.main.MainActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var userRepository: UserRepository

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            userRepository = Injection.provideUserRepository(applicationContext)
        }

        setupView()
        setupAction()
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
        val toolbar = findViewById<Toolbar>(R.id.toolbar4)
        toolbar.title = ""

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupAction() {

        binding.signupButton.setOnClickListener {

            val oldPassword= binding.edRegisterOldpassword.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val confirmPassword = binding.edRegisterConfirmpassword.text.toString()

            if (checkInput(oldPassword, password, confirmPassword)) {
                binding.progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    try {
                        val response = userRepository.changePassword(oldPassword, password, confirmPassword)
                        val message = response.message
                        if (!response.isSuccess) {
                            showAlertDialog(
                                title = "Oh no!",
                                errorMessage = message,
                                type = DialogType.ERROR,
                                icon = R.drawable.baseline_error_outline_24,
                                doAction = {}
                            )
                        } else {
                            showAlertDialog(
                                title = "Hooray!",
                                errorMessage = "Your password has been updated.",
                                type = DialogType.SUCCESS,
                                icon = R.drawable.outline_how_to_reg_24,
                                doAction = {
                                    navigateToProfile()
                                }
                            )
                        }
                    } catch (e: HttpException) {
                        val jsonInString = e.response()?.errorBody()?.string()
                        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                        val errorMessage = when (val message = errorBody.message) {
                            is String -> message
                            is List<*> -> message.joinToString(", ")
                            else -> "An unexpected error occurred."
                        }
                        showAlertDialog(
                            title = "Oh no!",
                            errorMessage = errorMessage,
                            type = DialogType.ERROR,
                            icon = R.drawable.baseline_error_outline_24,
                            doAction = {}
                        )
                    } finally {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun navigateToProfile() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("fragment", "profile")
        }
        startActivity(intent)
    }

    private fun checkInput(oldPassword: String, password: String, confirmPassword: String): Boolean {

        var isValid = true

        if (oldPassword.isEmpty() || oldPassword.length < 8) {
            binding.edRegisterPassword.error = getString(R.string.invalid_password)
            isValid = false
        }
        if (password.isEmpty() || password.length < 8) {
            binding.edRegisterPassword.error = getString(R.string.invalid_password)
            isValid = false
        }
        if (confirmPassword != password) {
            binding.edRegisterConfirmpassword.error = getString(R.string.password_do_not_match)
            isValid = false
        }
        return isValid
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