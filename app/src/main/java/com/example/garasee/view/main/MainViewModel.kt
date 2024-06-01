package com.example.garasee.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.garasee.data.pref.UserModel
import com.example.garasee.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
//    storyRepository: StoryRepository?
) : ViewModel() {

//    val storiesLiveData: LiveData<PagingData<ListStoryItem>> = storyRepository?.getStories()?.cachedIn(viewModelScope) ?: MutableLiveData()

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout(token: String) {
        viewModelScope.launch {
            userRepository.logout(token)
        }
    }
}