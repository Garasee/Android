package com.example.garasee.helper

import androidx.recyclerview.widget.DiffUtil
import com.example.garasee.database.Note

class NoteDiffCallback(private val oldNoteList: List<Note>, private val newNoteList: List<Note>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldNoteList.size
    override fun getNewListSize(): Int = newNoteList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNoteList[oldItemPosition].timestamp == newNoteList[newItemPosition].timestamp
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldNoteList[oldItemPosition]
        val newNote = newNoteList[newItemPosition]
        return oldNote.imageUrl == newNote.imageUrl && oldNote.result == newNote.result
    }
}