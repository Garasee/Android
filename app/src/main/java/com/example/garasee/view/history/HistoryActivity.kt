package com.example.garasee.view.history

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.garasee.R
import com.example.garasee.databinding.ActivityHistoryBinding
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.repository.NoteRepository

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var noteRepository: NoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar4)
        toolbar.title = "History"

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inisialisasi NoteRepository
        noteRepository = NoteRepository(application)

        val factory = ViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        val adapter = HistoryAdapter(noteRepository)
        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.adapter = adapter

        viewModel.getAllNotes().observe(this) { notes ->
            notes?.let {
                adapter.setListNotes(it)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
