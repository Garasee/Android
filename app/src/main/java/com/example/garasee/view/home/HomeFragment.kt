package com.example.garasee.view.home

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.garasee.R
import com.example.garasee.databinding.FragmentHomeBinding
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.view.main.MainActivity
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    private val isNewMap = mapOf(
    "New" to true,
    "Second" to false
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val conditionArray = resources.getStringArray(R.array.condition_array)
        val conditionAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, conditionArray)
        val tvHomeCondition = binding.tvHomeCondition
        tvHomeCondition.setAdapter(conditionAdapter)

        tvHomeCondition.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateCondition(tvHomeCondition, conditionArray)
            }
        }

        tvHomeCondition.setOnDismissListener {
            validateCondition(tvHomeCondition, conditionArray)
        }

        tvHomeCondition.setOnEditorActionListener { _, _, _ ->
            validateCondition(tvHomeCondition, conditionArray)
            false
        }

        val injectionArray = resources.getStringArray(R.array.injection_array)
        val injectionAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, injectionArray)
        val tvHomeInjection = binding.tvHomeInjection
        tvHomeInjection.setAdapter(injectionAdapter)

        tvHomeInjection.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateInjection(tvHomeInjection, injectionArray)
            }
        }

        tvHomeInjection.setOnDismissListener {
            validateInjection(tvHomeInjection, injectionArray)
        }

        tvHomeInjection.setOnEditorActionListener { _, _, _ ->
            validateInjection(tvHomeInjection, injectionArray)
            false
        }

        val factory = ViewModelFactory.getInstance(requireActivity().application as Application)
        viewModel = ViewModelProvider(this@HomeFragment, factory)[HomeViewModel::class.java]

        binding.progressBar.visibility = View.VISIBLE

        viewModel.fetchUser()

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameTextView.text = it.content.user.name
            }
            binding.progressBar.visibility = View.GONE
        }

        binding.submitButton.setOnClickListener {
            val brand = binding.edHomeBrand.text.toString()

            val enteredisNew = binding.tvHomeCondition.text.toString()
            val isNew = isNewMap[enteredisNew]


            val year = binding.edHomeYear.text.toString().toInt()
            val engineCapacity = binding.edHomeEngine.text.toString().toFloat()
            val peakPower = binding.edHomePeakpower.text.toString().toFloat()
            val peakTorque = binding.edHomePeaktorque.text.toString().toFloat()
            val injection = binding.tvHomeInjection.text.toString()
            val length = binding.edHomeLength.text.toString().toFloat()
            val width = binding.edHomeWidth.text.toString().toFloat()
            val wheelBase = binding.edHomeWheelbase.text.toString().toFloat()
            val doorAmount = binding.edHomeDooramount.text.toString().toInt()
            val seatCapacity = binding.edHomeSeatcapacity.text.toString().toInt()
            if (checkInput(brand, isNew!!, year, engineCapacity, peakPower, peakTorque, injection, length, width, wheelBase, doorAmount, seatCapacity)) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.postPredict(brand, isNew, year, engineCapacity, peakPower, peakTorque, injection, length, width, wheelBase, doorAmount, seatCapacity)
                viewModel.predict.observe(viewLifecycleOwner) { predict ->
                    predict?.let {
                        binding.progressBar.visibility = View.GONE
                        val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                        val formattedPrice = format.format(it.content.price.toString().toDouble())
                        showResultDialog(brand, it.content.isAcceptable, formattedPrice)

                    } ?: run {
                        binding.progressBar.visibility = View.GONE
                        showErrorDialog()
                    }
                }
            }
        }

    }

    private fun showResultDialog(brand: String, isAcceptable: Boolean, price: String) {
        val acceptableText = if (isAcceptable) "Acceptable" else "Not Acceptable"

        AlertDialog.Builder(requireContext())
            .setTitle("Prediction Result")
            .setMessage("Brand: $brand\nIs Acceptable: $acceptableText\nPrice: $price")
            .setIcon(R.drawable.baseline_directions_car_24)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                navigateToHistory()
            }
            .create()
            .show()
    }


    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("Failed to get prediction result. Please try again.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun checkInput(
        brand: String,
        isNew: Boolean?,
        year: Int?,
        engineCapacity: Float?,
        peakPower: Float?,
        peakTorque: Float?,
        injection: String,
        length: Float?,
        width: Float?,
        wheelBase: Float?,
        doorAmount: Int?,
        seatCapacity: Int?
    ): Boolean {

        var isValid = true

        if (brand.isEmpty()) {
            binding.edHomeBrand.error = getString(R.string.invalid_brand)
            isValid = false
        }

        if (isNew == null) {
            binding.tvHomeCondition.error = getString(R.string.invalid_condition)
            isValid = false
        }

        if (year == null || year <= 0) {
            binding.edHomeYear.error = getString(R.string.invalid_year)
            isValid = false
        }

        if (engineCapacity == null || engineCapacity <= 0) {
            binding.edHomeEngine.error = getString(R.string.invalid_engine)
            isValid = false
        }

        if (peakPower == null || peakPower <= 0) {
            binding.edHomePeakpower.error = getString(R.string.invalid_peakpower)
            isValid = false
        }

        if (peakTorque == null || peakTorque <= 0) {
            binding.edHomePeaktorque.error = getString(R.string.invalid_peaktorque)
            isValid = false
        }

        if (injection.isEmpty()) {
            binding.tvHomeInjection.error = getString(R.string.invalid_injection)
            isValid = false
        }

        if (length == null || length <= 0) {
            binding.edHomeLength.error = getString(R.string.invalid_length)
            isValid = false
        }

        if (width == null || width <= 0) {
            binding.edHomeWidth.error = getString(R.string.invalid_width)
            isValid = false
        }

        if (wheelBase == null || wheelBase <= 0) {
            binding.edHomeWheelbase.error = getString(R.string.invalid_wheelbase)
            isValid = false
        }

        if (doorAmount == null || doorAmount <= 0) {
            binding.edHomeDooramount.error = getString(R.string.invalid_dooramount)
            isValid = false
        }

        if (seatCapacity == null || seatCapacity <= 0) {
            binding.edHomeSeatcapacity.error = getString(R.string.invalid_seat)
            isValid = false
        }

        return isValid
    }


    private fun validateCondition(tvHomeCondition: AutoCompleteTextView, validOptions: Array<String>) {
        val enteredText = tvHomeCondition.text.toString()
        if (validOptions.contains(enteredText)) {
            Log.d("SelectedCondition", "Selected condition: $enteredText")
        } else {
            tvHomeCondition.text = null
            Log.d("InvalidCondition", "Entered condition is not valid: $enteredText")
            Toast.makeText(requireContext(), "Invalid condition. Please select a valid condition.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInjection(tvHomeInjection: AutoCompleteTextView, validOptions: Array<String>) {
        val enteredText = tvHomeInjection.text.toString()
        if (validOptions.contains(enteredText)) {
            Log.d("SelectedInjection", "Selected injection: $enteredText")
        } else {
            tvHomeInjection.text = null
            Log.d("InvalidInjection", "Entered injection is not valid: $enteredText")
            Toast.makeText(requireContext(), "Invalid injection. Please select a valid injection.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun navigateToHistory() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("fragment", "history")
        }
        startActivity(intent)
    }



}