package com.example.garasee.view.history

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.garasee.database.Note
import com.example.garasee.repository.NoteRepository
import com.example.garasee.repository.UserRepository

class HistoryViewModel (application: Application) : ViewModel() {

    private val mNoteRepository: NoteRepository = NoteRepository(application)

    fun getAllNotes(): LiveData<List<Note>> = mNoteRepository.getAllNotes()
}