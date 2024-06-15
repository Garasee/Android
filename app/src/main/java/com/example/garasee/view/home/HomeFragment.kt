package com.example.garasee.view.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.garasee.R
import com.example.garasee.database.Note
import com.example.garasee.repository.NoteRepository
import com.example.garasee.databinding.FragmentHomeBinding
import com.example.garasee.helper.ImageClassifierHelper
import com.example.garasee.utils.Utils
import com.example.garasee.view.result.ResultActivity
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var mNoteRepository: NoteRepository
    private lateinit var binding: FragmentHomeBinding

    private var currentImageUri: Uri? = null

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
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

        mNoteRepository = NoteRepository(requireActivity().application)

        binding.galleryButton.setOnClickListener { startGallery() }

        binding.cameraButton.setOnClickListener { startCamera() }

        binding.analyzeButton.setOnClickListener { analyzeImage() }


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
        currentImageUri = utils.getImageUri(requireContext())
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

        val tempFile = File.createTempFile("cropped_image", ".jpg", requireActivity().cacheDir)

        UCrop.of(uri, Uri.fromFile(tempFile))
            .withOptions(options)
            .start(requireContext(), this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == AppCompatActivity.RESULT_OK) {
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

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            val timestamp: String = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(Date())

            val imagePath = getPathFromUri(uri)
            Log.d("imagePath", imagePath)

            val cachePath = File(requireActivity().cacheDir, "cropped_image")
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
                context = requireContext(),
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        requireActivity().runOnUiThread {
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

                                mNoteRepository.getNoteByTimestamp(timestamp).observe(viewLifecycleOwner) { note ->
                                    if (!actionCompleted) {
                                        if (note == null) {
                                            val newNote = Note().apply {
                                                this.timestamp = timestamp
                                                this.imageUrl = uniqueUri.toString()
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
            imageClassifierHelper.classifyStaticImage(uri, requireContext())
        } ?: showToast(getString(R.string.no_image))

    }

    private fun getPathFromUri(uri: Uri): String {
        val filePath: String
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
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
        val intent = Intent(requireActivity(), ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
            putExtra(ResultActivity.EXTRA_RESULT, displayResult)
            putExtra(ResultActivity.EXTRA_TIMESTAMP, timestamp)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


}
