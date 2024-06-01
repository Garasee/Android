package com.example.garasee.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Query("SELECT * from History")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM History WHERE timestamp = :timestamp")
    fun getNoteByTimestamp(timestamp: String): LiveData<Note?>

    @Query("DELETE FROM History WHERE timestamp = :timestamp")
    fun deleteByTimestamp(timestamp: String)

}