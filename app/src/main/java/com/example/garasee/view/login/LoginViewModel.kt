package com.example.garasee.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.garasee.data.pref.UserModel
import com.example.garasee.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun login(email: String, password: String) = liveData {
        try {
            val response = repository.login(email, password)
            if (response.isSuccess) {
                emit(response.content)
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}