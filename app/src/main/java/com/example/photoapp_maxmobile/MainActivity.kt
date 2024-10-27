package com.example.photoapp_maxmobile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.app.AlertDialog
import com.example.photoapp_maxmobile.setup.FirebaseSetup
import com.google.firebase.FirebaseApp
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private lateinit var btnCapture: Button
    private lateinit var btnFromGallery: Button
    private lateinit var btnEditPhoto: Button
    private lateinit var btnShowImageList: Button
    private lateinit var btnUpload: Button
    private lateinit var imgPreview: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_SELECT = 2
    private val REQUEST_IMAGE_EDIT = 3
    private var selectedImageUri: Uri? = null

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            showDialog("Quyền truy cập máy ảnh đã bị từ chối.")
        }
    }

    private val requestGalleryPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val selectFromGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(selectFromGallery, REQUEST_IMAGE_SELECT)
        } else {
            showDialog("Quyền truy cập thư viện đã bị từ chối.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        FirebaseSetup.initialize()

        btnCapture = findViewById(R.id.btnCapture)
        btnFromGallery = findViewById(R.id.btnLoadFromGallery)
        btnEditPhoto = findViewById(R.id.btnEdit)
        btnUpload = findViewById(R.id.btnUpload)
        imgPreview = findViewById(R.id.imgPreview)
        btnShowImageList = findViewById(R.id.btnShowImageList)

        btnCapture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            } else {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

        btnFromGallery.setOnClickListener {
            val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestGalleryPermission.launch(permission)
            } else {
                val selectFromGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(selectFromGallery, REQUEST_IMAGE_SELECT)
            }
        }

        btnEditPhoto.setOnClickListener {
            if (selectedImageUri != null) {
                val editIntent = Intent(this, PhotoEditActivity::class.java)
                editIntent.putExtra("imageUri", selectedImageUri)
                startActivityForResult(editIntent, REQUEST_IMAGE_EDIT)
            } else {
                showDialog("Vui lòng chọn hoặc chụp ảnh trước khi chỉnh sửa.")
            }
        }

        btnShowImageList.setOnClickListener {
            val showImagesIntent = Intent(this, imagegridactivityActivity::class.java)
            startActivity(showImagesIntent)
        }

        btnUpload.setOnClickListener {
            if (selectedImageUri != null) {
                FirebaseSetup.uploadImageToFirebase(selectedImageUri!!, this)
            } else {
                showDialog("Vui lòng chọn hoặc chỉnh sửa ảnh trước khi tải lên.")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == Activity.RESULT_OK) {
                val extras = data?.extras
                val imageBitmap = extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    imgPreview.setImageBitmap(it)
                    val uri = saveImageToCache(it)
                    selectedImageUri = uri
                }
            }
            REQUEST_IMAGE_SELECT -> if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                uri?.let {
                    imgPreview.setImageURI(it)
                    selectedImageUri = it
                }
            }
            REQUEST_IMAGE_EDIT -> if (resultCode == Activity.RESULT_OK) {
                val editedImageUri = data?.getParcelableExtra<Uri>("editedImageUri")
                editedImageUri?.let {
                    imgPreview.setImageURI(it)
                    selectedImageUri = it
                }
            }
        }
    }

    private fun saveImageToCache(bitmap: Bitmap): Uri {
        val file = File(externalCacheDir, "captured_image_${System.currentTimeMillis()}.jpg")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        return Uri.fromFile(file)
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
