package com.example.garasee.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.garasee.data.api.ApiService
import com.example.garasee.data.pref.UserPreference
import com.example.garasee.database.Note
import com.example.garasee.database.NoteDao
import com.example.garasee.database.NoteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NoteRepository(application: Application) {
    private val mNotesDao: NoteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = NoteRoomDatabase.getDatabase(application)
        mNotesDao = db.noteDao()
    }

    companion object {
        @Volatile
        private var instance: NoteRepository? = null
        fun getInstance(
            application: Application
        ): NoteRepository =
            instance ?: synchronized(this) {
                instance ?: NoteRepository(application)
            }.also { instance = it }
    }

    fun getAllNotes(): LiveData<List<Note>> = mNotesDao.getAllNotes()

    fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }

    fun deleteByTimestamp(timestamp: String) {
        executorService.execute { mNotesDao.deleteByTimestamp(timestamp) }
    }

    fun getNoteByTimestamp(timestamp: String): LiveData<Note?> {
        return mNotesDao.getNoteByTimestamp(timestamp)
    }

}