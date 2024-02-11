package com.example.screenshotr

import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {

    private lateinit var previewImageView: ImageView
    private lateinit var photosRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        previewImageView = findViewById(R.id.previewImageView)
        photosRecyclerView = findViewById(R.id.photosRecyclerView)

        loadImagesFromGallery()

    }

    private fun loadImagesFromGallery() {
        val projection = arrayOf(
            MediaStore.Images.Media._ID, MediaStore.Images.Media.RELATIVE_PATH
        )

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use { imageCursor ->
            val columnIndexId = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val columnRelativePath =
                imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)

            val imageIds = mutableListOf<Long>()
            while (imageCursor.moveToNext()) {
                val imageId = imageCursor.getLong(columnIndexId)
                val path = imageCursor.getString(columnRelativePath)
                if (isScreenshot(path)) {
                    imageIds.add(imageId)
                }
            }

            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            photosRecyclerView.layoutManager = layoutManager
            photosRecyclerView.adapter = PhotoAdapter(imageIds) { selectedImageId ->
                previewImageView.setImageURI(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                        .appendPath(selectedImageId.toString()).build()
                )
            }

            photosRecyclerView.post {
                val centerChild =
                    layoutManager.findViewByPosition(layoutManager.findFirstVisibleItemPosition() + layoutManager.childCount / 2)

                val centerPosition =
                    centerChild?.let { photosRecyclerView.getChildAdapterPosition(it) }
                        ?: RecyclerView.NO_POSITION

                if (centerPosition != RecyclerView.NO_POSITION && centerPosition < imageIds.size) {
                    val centerImageId = imageIds[centerPosition]
                    (photosRecyclerView.adapter as? PhotoAdapter)?.setCenterPosition(centerPosition)
                    previewImageView.setImageURI(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                            .appendPath(centerImageId.toString()).build()
                    )
                }
            }

            photosRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val centerPosition = (firstVisibleItemPosition + lastVisibleItemPosition) / 2

                    (photosRecyclerView.adapter as? PhotoAdapter)?.setCenterPosition(centerPosition)

                    if (centerPosition >= 0 && centerPosition < imageIds.size) {
                        val centerImageId = imageIds[centerPosition]
                        previewImageView.setImageURI(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                                .appendPath(centerImageId.toString()).build()
                        )
                    }
                }
            })
        }
    }

    private fun isScreenshot(imagePath: String?): Boolean {
        if (imagePath != null) {
            return imagePath.contains("screenshots", ignoreCase = true) ||
                    imagePath.contains("screenshot", ignoreCase = true)
        }
        return false
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT <= 32) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
            }
        }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT <= 32) {
            startExternalStoragePermissionRequest()
        } else {
            startReadMediaImagesPermissionRequest()
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadImagesFromGallery()
        } else {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_LONG).show()
        }
    }

    private fun startExternalStoragePermissionRequest() {
        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun startReadMediaImagesPermissionRequest() {
        requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
    }
}