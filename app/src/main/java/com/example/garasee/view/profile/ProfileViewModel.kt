package com.example.garasee.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garasee.data.api.GetUserResponse
import com.example.garasee.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel (private val repository: ProfileRepository) : ViewModel() {

    private val _user = MutableLiveData<GetUserResponse?>()
    val user: LiveData<GetUserResponse?> = _user

    fun fetchUser() {
        viewModelScope.launch {
            _user.value = repository.getUser()
        }
    }
}
