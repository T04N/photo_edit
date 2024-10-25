//package com.example.photoeditorapp
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.view.MotionEvent
//import android.view.View
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.example.photoapp_maxmobile.R
//import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
//import ja.burhanrashid52.photoeditor.PhotoEditor
//import ja.burhanrashid52.photoeditor.PhotoEditorView
//import ja.burhanrashid52.photoeditor.PhotoFilter
//import ja.burhanrashid52.photoeditor.SaveSettings
//import ja.burhanrashid52.photoeditor.TextStyleBuilder
//import java.io.File
//import java.io.IOException
//import java.util.*//
//class PhotoEditorActivity : ComponentActivity() {
//
//    private lateinit var photoEditor: PhotoEditor
//    private lateinit var photoEditorView: PhotoEditorView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.photo_edit)
//
//        // Initialize PhotoEditorView
//        photoEditorView = findViewById(R.id.photoEditorView)
//
//        // Initialize PhotoEditor
//        photoEditor = PhotoEditor.Builder(this, photoEditorView).build()
//        photoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener {
//            override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
//                // Handle text change event
//            }
//
//            override fun onAddViewListener(viewType: ja.burhanrashid52.photoeditor.ViewType, numberOfAddedViews: Int) {
//                // Handle add view event
//            }
//
//            override fun onRemoveViewListener(viewType: ja.burhanrashid52.photoeditor.ViewType, numberOfAddedViews: Int) {
//                // Handle remove view event
//            }
//
//            override fun onStartViewChangeListener(viewType: ja.burhanrashid52.photoeditor.ViewType) {
//                // Handle start view change event
//            }
//
//            override fun onStopViewChangeListener(viewType: ja.burhanrashid52.photoeditor.ViewType) {
//                // Handle stop view change event
//            }
//
//            override fun onTouchSourceImage(event: MotionEvent) {
//                // Handle touch event on source image
//            }
//        })
//
//        // Set up buttons
//        findViewById<View>(R.id.btnAddText).setOnClickListener {
//            val textStyleBuilder = TextStyleBuilder()
//            textStyleBuilder.withTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
//            photoEditor.addText("Hello World", textStyleBuilder)
//        }
//
//        findViewById<View>(R.id.btnDraw).setOnClickListener {
//            photoEditor.setBrushDrawingMode(true)
//            photoEditor.brushColor = ContextCompat.getColor(this, android.R.color.holo_blue_light)
//        }
//
//        findViewById<View>(R.id.btnAddEmoji).setOnClickListener {
//            val emojis = arrayOf("\uD83D\uDE00", "\uD83D\uDE02", "\uD83D\uDE09", "\uD83D\uDE0D")
//            val randomEmoji = emojis[Random().nextInt(emojis.size)]
//            photoEditor.addEmoji(randomEmoji)
//        }
//
//        findViewById<View>(R.id.btnApplyFilter).setOnClickListener {
//            photoEditor.setFilterEffect(PhotoFilter.GRAY_SCALE)
//        }
//
//        findViewById<View>(R.id.btnSave).setOnClickListener {
//            saveImage()
//        }
//    }
//
//    private fun saveImage() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
//        } else {
//            val file = File(externalCacheDir, "edited_image_${System.currentTimeMillis()}.jpg")
//            try {
//                val saveSettings = SaveSettings.Builder().setClearViewsEnabled(true).setTransparencyEnabled(true).build()
//                photoEditor.saveAsFile(file.absolutePath, saveSettings, object : PhotoEditor.OnSaveListener {
//                    override fun onSuccess(imagePath: String) {
//                        val savedImageUri = Uri.fromFile(File(imagePath))
//                        Toast.makeText(this@PhotoEditorActivity, "Saved: $savedImageUri", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onFailure(exception: Exception) {
//                        Toast.makeText(this@PhotoEditorActivity, "Save Failed!", Toast.LENGTH_SHORT).show()
//                    }
//                })
//            } catch (e: IOException) {
//                e.printStackTrace()
//                Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
