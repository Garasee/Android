package com.example.garasee.helper

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.garasee.di.Injection
import com.example.garasee.repository.HistoryRepository
import com.example.garasee.repository.UserRepository
import com.example.garasee.view.login.LoginViewModel
import com.example.garasee.view.main.MainViewModel
import com.example.garasee.view.history.HistoryViewModel
import com.example.garasee.repository.ProfileRepository
import com.example.garasee.view.home.HomeViewModel
import com.example.garasee.view.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private var historyRepository: HistoryRepository?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                var isUserLoggedIn: Boolean
                runBlocking { isUserLoggedIn = userRepository.isUserLoggedIn().first() }
                if (isUserLoggedIn) {
                    MainViewModel(userRepository) as T
                } else {
                    throw IllegalStateException("User is not logged in")
                }
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                var isUserLoggedIn: Boolean
                runBlocking { isUserLoggedIn = userRepository.isUserLoggedIn().first() }
                if (isUserLoggedIn) {
                    ProfileViewModel(profileRepository) as T
                } else {
                    throw IllegalStateException("User is not logged in")
                }
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(historyRepository!!) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userRepository, profileRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val instance: ViewModelFactory
                runBlocking {
                    val userRepository = Injection.provideUserRepository(application)
                    val profileRepository = Injection.provideProfileRepository(application)
                    val historyRepository = Injection.provideHistoryRepository(application)
                    instance = ViewModelFactory(userRepository, profileRepository, historyRepository)
                }
                INSTANCE = instance
                instance
            }
        }

        suspend fun updateHistoryRepository(context: Context) {
            val historyRepository = withContext(Dispatchers.IO) { Injection.provideHistoryRepository(context) }
            INSTANCE?.historyRepository = historyRepository
        }

    }
}
