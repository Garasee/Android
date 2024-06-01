package com.example.garasee.view.main

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.garasee.R
import com.example.garasee.database.Note
import com.example.garasee.repository.NoteRepository
import com.example.garasee.databinding.ActivityMainBinding
import com.example.garasee.di.Injection
import com.example.garasee.helper.ImageClassifierHelper
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.utils.Utils
import com.example.garasee.view.history.HistoryActivity
import com.example.garasee.view.result.ResultActivity
import com.example.garasee.view.welcome.WelcomeActivity
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var mNoteRepository: NoteRepository
    private lateinit var binding: ActivityMainBinding
    private lateinit var token: String

    private var currentImageUri: Uri? = null

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar4)
        toolbar.title = "Garasee"
        setSupportActionBar(binding.toolbar4)

        mNoteRepository = NoteRepository(application)

        binding.galleryButton.setOnClickListener { startGallery() }

        binding.cameraButton.setOnClickListener { startCamera() }

        binding.analyzeButton.setOnClickListener {analyzeImage()}

//        binding.analyzeButton.setOnClickListener {
//            currentImageUri?.let {
//                analyzeImage(it)
//            } ?: run {
//                showToast(getString(R.string.empty_image_warning))
//            }
//        }

        lifecycleScope.launch {
            val userPreference = Injection.provideUserPreference(applicationContext)
            token = userPreference.getToken().first() ?: ""

            if (token.isEmpty()) {
                navigateToWelcome()
                return@launch
            }

            val isLoggedIn = userPreference.getSession().first().isLogin

            if (isLoggedIn) {
                try {
                    val factory = ViewModelFactory.getInstance(application as Application)
                    viewModel = ViewModelProvider(this@MainActivity, factory)[MainViewModel::class.java]

                    viewModel.getSession().observe(this@MainActivity) { user ->
                        if (!user.isLogin) {
                            navigateToWelcome()
                        }
                    }

                } catch (e: IllegalStateException) {
                    Log.e("MainActivity", "IllegalStateException caught", e)
                    navigateToWelcome()
                }
            } else {
                navigateToWelcome()
            }
        }
    }

    private fun navigateToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.logout -> {
                lifecycleScope.launch {
                    viewModel.logout(token)
                    navigateToWelcome()
                }
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            startUCrop(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        val utils = Utils()
        currentImageUri = utils.getImageUri(this)
        currentImageUri?.let { launcherIntentCamera.launch(it) }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startUCrop(uri: Uri) {
        val options = UCrop.Options()

        val tempFile = File.createTempFile("cropped_image", ".jpg", cacheDir)

        UCrop.of(uri, Uri.fromFile(tempFile))
            .withOptions(options)
            .start(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                currentImageUri = it
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(data!!)
            error?.let {
                Log.e("UCrop Error", "Error: $error")
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(null)
            binding.previewImageView.setImageURI(it)
        }
    }

//    private fun showImage(uri: Uri) {
//        binding.previewImageView.setImageURI(null)
//        binding.previewImageView.setImageURI(uri)
//    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            val timestamp: String = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(Date())

            val imagePath = getPathFromUri(uri)
            Log.d("imagePath", imagePath)

            val cachePath = File(cacheDir, "cropped_image")
            if (cachePath.exists() && !cachePath.isDirectory) {
                cachePath.delete()
            }
            Log.d("cachePath", cachePath.toString())
            if (!cachePath.exists()) {
                cachePath.mkdir()
            }
            val uniqueImagePath = "$cachePath/$timestamp"

            val file = File(imagePath)
            file.copyTo(File(uniqueImagePath))

            val uniqueUri = Uri.fromFile(File(uniqueImagePath))

            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        runOnUiThread {
                            results?.let { it ->
                                val displayResult = if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                    val sortedCategories = it[0].categories.sortedByDescending { it?.score }
                                    val topCategory = sortedCategories.firstOrNull()
                                    topCategory?.let {
                                        "${it.label} " + NumberFormat.getPercentInstance().format(it.score).trim()
                                    } ?: "No results"
                                } else {
                                    "No results"
                                }

                                var actionCompleted = false

                                mNoteRepository.getNoteByTimestamp(timestamp).observe(this@MainActivity) { note ->
                                    if (!actionCompleted) {
                                        if (note == null) {
                                            val newNote = Note().apply {
                                                this.timestamp = timestamp
                                                this.imageUrl = uniqueUri.toString() // Simpan path file lokal ke basis data
                                                this.result = displayResult
                                            }
                                            mNoteRepository.insert(newNote)
                                        } else {
                                            showToast(getString(R.string.mNoteNull))
                                        }

                                        actionCompleted = true
                                    }
                                }
                                moveToResult(displayResult, timestamp)
                            }
                        }
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(uri, this)
        } ?: showToast(getString(R.string.no_image))

    }

    private fun getPathFromUri(uri: Uri): String {
        val filePath: String
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor == null) {
            filePath = uri.path.toString()
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    private fun moveToResult(displayResult: String, timestamp: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
            putExtra(ResultActivity.EXTRA_RESULT, displayResult)
            putExtra(ResultActivity.EXTRA_TIMESTAMP, timestamp)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
