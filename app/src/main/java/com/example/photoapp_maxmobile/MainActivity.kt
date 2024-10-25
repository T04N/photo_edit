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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.photoapp_maxmobile.ui.theme.PhotoApp_MaxmobileTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private lateinit var btnCapture: Button
    private lateinit var btnFromGallery: Button
    private lateinit var btnEditPhoto: Button
    private lateinit var imgPreview: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_SELECT = 2
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        btnCapture = findViewById(R.id.btnCapture)
        btnFromGallery = findViewById(R.id.btnLoadFromGallery)
        btnEditPhoto = findViewById(R.id.btnEdit)
        imgPreview = findViewById(R.id.imgPreview)

        btnCapture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
            } else {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

        btnFromGallery.setOnClickListener {
            val selectFromGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_IMAGE_SELECT)
            } else {
                startActivityForResult(selectFromGallery, REQUEST_IMAGE_SELECT)
            }
        }

        btnEditPhoto.setOnClickListener {
            if (selectedImageUri != null) {
                val editIntent = Intent(this, PhotoEditActivity::class.java)
                editIntent.putExtra("imageUri", selectedImageUri)
                startActivity(editIntent)
            } else {
                Toast.makeText(this, "Vui lòng chọn hoặc chụp ảnh trước khi chỉnh sửa.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            val imageBitmap = extras?.get("data") as? Bitmap
            imageBitmap?.let {
                imgPreview.setImageBitmap(it)
                val uri = saveImageToCache(it)
                selectedImageUri = uri
            }
        }

        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            uri?.let {
                imgPreview.setImageURI(it)
                selectedImageUri = it // Lưu URI của ảnh đã chọn
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




