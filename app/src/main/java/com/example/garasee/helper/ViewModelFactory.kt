package com.example.garasee.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.garasee.di.Injection
import com.example.garasee.repository.UserRepository
import com.example.garasee.view.login.LoginViewModel
import com.example.garasee.view.main.MainViewModel
import com.example.garasee.view.history.HistoryViewModel
import com.example.garasee.repository.NoteRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class ViewModelFactory private constructor(
    private val application: Application,
    private val userRepository: UserRepository,
    private val noteRepository: NoteRepository
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
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(application) as T
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
                    val noteRepository = Injection.provideNoteRepository(application)
                    instance = ViewModelFactory(application, userRepository, noteRepository)
                }
                INSTANCE = instance
                instance
            }
        }

    }
}
