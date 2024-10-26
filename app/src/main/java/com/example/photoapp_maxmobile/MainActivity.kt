package com.example.photoapp_maxmobile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.photoapp_maxmobile.setup.FirebaseSetup
import com.google.firebase.FirebaseApp
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var btnCapture: Button
    private lateinit var btnFromGallery: Button
    private lateinit var btnEditPhoto: Button
    private lateinit var btnUpload: Button
    private lateinit var imgPreview: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_SELECT = 2
    private val REQUEST_IMAGE_EDIT = 3
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCapture = findViewById(R.id.btnCapture)
        btnFromGallery = findViewById(R.id.btnLoadFromGallery)
        btnEditPhoto = findViewById(R.id.btnEdit)
        btnUpload = findViewById(R.id.btnUpload)
        imgPreview = findViewById(R.id.imgPreview)

        FirebaseApp.initializeApp(this)
        FirebaseSetup.initialize();

        btnCapture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
            } else {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

        btnFromGallery.setOnClickListener {
            val selectFromGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_IMAGE_SELECT)
            } else {
                startActivityForResult(selectFromGallery, REQUEST_IMAGE_SELECT)
            }
        }

        btnEditPhoto.setOnClickListener {
            if (selectedImageUri != null) {
                val editIntent = Intent(this, PhotoEditActivity::class.java)
                editIntent.putExtra("imageUri", selectedImageUri)
                startActivityForResult(editIntent, REQUEST_IMAGE_EDIT)
            } else {
                Toast.makeText(this, "Vui lòng chọn hoặc chụp ảnh trước khi chỉnh sửa.", Toast.LENGTH_SHORT).show()
            }
        }

        btnUpload.setOnClickListener {
            if (selectedImageUri != null) {
                FirebaseSetup.uploadImageToFirebase(selectedImageUri!!, this)
            } else {
                Toast.makeText(this, "Vui lòng chọn hoặc chỉnh sửa ảnh trước khi tải lên.", Toast.LENGTH_SHORT).show()
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
}


