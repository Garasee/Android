package com.example.garasee.view.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garasee.data.api.ContentItem
import com.example.garasee.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel (private val historyRepository: HistoryRepository?) : ViewModel() {

    private val _historyLiveData = MutableLiveData<List<ContentItem>>()
    val historyLiveData: LiveData<List<ContentItem>> get() = _historyLiveData

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            try {
                historyRepository?.let {
                    val content = it.getHistory()
                    _historyLiveData.postValue(content)
                } ?: run {
                }
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Error loading history", e)
            }
        }
    }
}