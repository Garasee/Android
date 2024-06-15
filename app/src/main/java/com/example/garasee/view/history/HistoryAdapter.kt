package com.example.garasee.view.history

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.garasee.database.Note
import com.example.garasee.databinding.ItemNoteBinding
import com.example.garasee.helper.NoteDiffCallback
import com.example.garasee.repository.NoteRepository
import com.example.garasee.view.result.ResultActivity
import java.util.Locale

class HistoryAdapter(private val mNoteRepository: NoteRepository) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val listNotes = ArrayList<Note>()

    fun setListNotes(listNotes: List<Note>) {
        val diffCallback = NoteDiffCallback(this.listNotes, listNotes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listNotes.clear()
        this.listNotes.addAll(listNotes)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val note = listNotes[position]
        holder.bind(note)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, note.imageUrl)
            intent.putExtra(ResultActivity.EXTRA_RESULT, note.result)
            intent.putExtra(ResultActivity.EXTRA_TIMESTAMP, note.timestamp)
            intent.putExtra(ResultActivity.EXTRA_FROM_HISTORY_ACTIVITY, true)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    inner class HistoryViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            with(binding) {
                val timestampFormat = SimpleDateFormat("dd/MM/yyyy-HH:mm:ss", Locale.getDefault())
                try {
                    val date = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).parse(note.timestamp)
                    val formattedTimestamp = timestampFormat.format(date)
                    tvTimestamp.text = formattedTimestamp
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                tvResult.text = note.result
                Glide.with(binding.root.context)
                    .load(note.imageUrl)
                    .into(binding.imageView)

                btnDelete.setOnClickListener {
                    val context = itemView.context
                    AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin menghapus item ini?")
                        .setPositiveButton("Ya") { _, _ ->
                            mNoteRepository.deleteByTimestamp(note.timestamp!!)
                            Toast.makeText(context, "Item dihapus", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            }
        }
    }

}
