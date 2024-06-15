package com.example.garasee.view.history

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.garasee.databinding.FragmentHistoryBinding
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.repository.NoteRepository

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var noteRepository: NoteRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        noteRepository = NoteRepository(requireActivity().application)

        val factory = ViewModelFactory.getInstance(requireActivity().application as Application)
        viewModel = ViewModelProvider(this@HistoryFragment, factory)[HistoryViewModel::class.java]

        val adapter = HistoryAdapter(noteRepository)
        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotes.adapter = adapter

        viewModel.getAllNotes().observe(viewLifecycleOwner) { notes ->
            notes?.let {
                adapter.setListNotes(it)
            }

        }

    }
}