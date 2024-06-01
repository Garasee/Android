package com.example.garasee.view.result

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.garasee.R
import com.example.garasee.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar4)
        toolbar.title = "Result"

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        val classificationResult = intent.getStringExtra(EXTRA_RESULT)

        imageUri?.let {
            binding.resultImage.setImageURI(it)
        }

        binding.resultText.text = "Result = $classificationResult"

    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_TIMESTAMP = "extra_timestamp"
        const val EXTRA_FROM_HISTORY_ACTIVITY = "extra_from_history_activity"

    }

}