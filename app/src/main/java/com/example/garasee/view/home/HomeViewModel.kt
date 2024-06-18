package com.example.garasee.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garasee.data.api.GetUserResponse
import com.example.garasee.data.api.PredictionResponse
import com.example.garasee.repository.ProfileRepository
import com.example.garasee.repository.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel (private val userRepository: UserRepository, private val profileRepository: ProfileRepository) : ViewModel() {

    private val _predict = MutableLiveData<PredictionResponse?>()
    val predict: LiveData<PredictionResponse?> = _predict

    private val _user = MutableLiveData<GetUserResponse?>()
    val user: LiveData<GetUserResponse?> = _user

    fun postPredict(brand: String, isNew: Boolean, year: Number, engineCapacity: Float, peakPower: Float, peakTorque: Float, injection: String, length: Float, width: Float, wheelBase: Float, doorAmount: Int, seatCapacity: Int) {
        viewModelScope.launch {
            _predict.value = userRepository.predict(brand, isNew, year, engineCapacity, peakPower, peakTorque, injection, length, width, wheelBase, doorAmount, seatCapacity)
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            _user.value = profileRepository.getUser()
        }
    }
}