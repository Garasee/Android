package com.example.garasee.view.profile

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.garasee.databinding.FragmentProfileBinding
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.view.changepassword.ChangePasswordActivity
import com.example.garasee.view.editprofile.EditProfileActivity

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity().application as Application)
        viewModel = ViewModelProvider(this@ProfileFragment, factory)[ProfileViewModel::class.java]

        binding.progressBar.visibility = View.VISIBLE

        viewModel.fetchUser()

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvProfileName.text = it.content.user.name
                binding.tvProfilePhone.text = it.content.user.phone
                binding.tvProfileEmail.text = it.content.user.email
                binding.tvProfileCity.text = it.content.user.city.name
            }
            binding.progressBar.visibility = View.GONE
        }

        binding.changeprofile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("name", binding.tvProfileName.text.toString())
            intent.putExtra("phone", binding.tvProfilePhone.text.toString())
            intent.putExtra("city", binding.tvProfileCity.text.toString())
            startActivity(intent)
        }

        binding.changepass.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }
}

