package com.example.garasee.view.history

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.garasee.databinding.FragmentHistoryBinding
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.data.api.ContentItem
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var viewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {

            ViewModelFactory.updateHistoryRepository(requireActivity().application as Application)
            val factory = ViewModelFactory.getInstance(requireActivity().application as Application)
            viewModel = ViewModelProvider(this@HistoryFragment, factory)[HistoryViewModel::class.java]

            viewModel.historyLiveData.observe(viewLifecycleOwner) { contentItem ->
                binding.progressBar.visibility = View.VISIBLE

                contentItem?.let {
                    val content = it.map { contentItem ->
                        ContentItem(
                            createdAt = contentItem.createdAt,
                            isAcceptable = contentItem.isAcceptable,
                            price = contentItem.price,
                            brand = contentItem.brand
                        )
                    }

                    if (content.isEmpty()) {
                        binding.notfound.visibility = View.VISIBLE
                        binding.rvNotes.visibility = View.GONE
                    } else {
                        binding.notfound.visibility = View.GONE
                        binding.rvNotes.visibility = View.VISIBLE
                        binding.rvNotes.adapter = HistoryAdapter(content)
                        binding.rvNotes.adapter?.notifyDataSetChanged()
                    }
                } ?: run {
                    showToast("No stories available")
                    binding.notfound.visibility = View.VISIBLE
                    binding.rvNotes.visibility = View.GONE
                }

                binding.progressBar.visibility = View.GONE
            }

        }

        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotes.setHasFixedSize(true)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}