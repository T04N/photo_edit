package com.example.photoapp_maxmobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import java.io.File
import java.io.IOException
import java.util.*

class PhotoEditActivity : AppCompatActivity() {

    private lateinit var photoEditor: PhotoEditor
    private lateinit var photoEditorView: PhotoEditorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_edit)

        // Initialize PhotoEditorView
        photoEditorView = findViewById(R.id.photoEditorView)

        // Get the image passed from MainActivity
        val imageUri = intent.getParcelableExtra<Uri>("imageUri")
        imageUri?.let {
            photoEditorView.source.setImageURI(it)
            photoEditorView.source.adjustViewBounds = true
            photoEditorView.source.scaleType = ImageView.ScaleType.FIT_CENTER
        }

        // Initialize PhotoEditor
        photoEditor = PhotoEditor.Builder(this, photoEditorView).build()
        photoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener {
            override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {}
            override fun onAddViewListener(viewType: ja.burhanrashid52.photoeditor.ViewType, numberOfAddedViews: Int) {}
            override fun onRemoveViewListener(viewType: ja.burhanrashid52.photoeditor.ViewType, numberOfAddedViews: Int) {}
            override fun onStartViewChangeListener(viewType: ja.burhanrashid52.photoeditor.ViewType) {}
            override fun onStopViewChangeListener(viewType: ja.burhanrashid52.photoeditor.ViewType) {}
            override fun onTouchSourceImage(event: MotionEvent) {}
        })

        // Set up buttons
        findViewById<View>(R.id.btnAddText).setOnClickListener {
            val textStyleBuilder = TextStyleBuilder()
            textStyleBuilder.withTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            photoEditor.addText("TEXT EXAMPLE ", textStyleBuilder)
        }

        findViewById<View>(R.id.btnDraw).setOnClickListener {
            photoEditor.setBrushDrawingMode(true)
            photoEditor.brushColor = ContextCompat.getColor(this, android.R.color.holo_blue_light)
        }

        findViewById<View>(R.id.btnAddEmoji).setOnClickListener {
            val emojis = arrayOf("\uD83D\uDE00", "\uD83D\uDE02", "\uD83D\uDE09", "\uD83D\uDE0D")
            val randomEmoji = emojis[Random().nextInt(emojis.size)]
            photoEditor.addEmoji(randomEmoji)
        }

        findViewById<View>(R.id.btnSave).setOnClickListener {
            saveImage()
        }

        // Set up filter task bar
        val filterLayout: LinearLayout = findViewById(R.id.filterLayout)
        val filters = PhotoFilter.values()
        filters.forEach { filter ->
            val filterContainer = LinearLayout(this)
            filterContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            filterContainer.orientation = LinearLayout.VERTICAL
            filterContainer.gravity = Gravity.CENTER

            val filterButton = ImageButton(this)
            filterButton.layoutParams = LinearLayout.LayoutParams(200, 200)
            filterButton.setImageResource(R.drawable.ic_new_filter)
            filterButton.setOnClickListener {
                photoEditor.setFilterEffect(filter)
            }

            val filterName = TextView(this)
            filterName.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            filterName.text = filter.name
            filterName.textSize = 12f
            filterName.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            filterName.gravity = Gravity.CENTER

            filterContainer.addView(filterButton)
            filterContainer.addView(filterName)
            filterLayout.addView(filterContainer)
        }
    }

    private fun saveImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            val file = File(externalCacheDir, "edited_image_${System.currentTimeMillis()}.jpg")
            try {
                val saveSettings = SaveSettings.Builder().setClearViewsEnabled(true).setTransparencyEnabled(true).build()
                photoEditor.saveAsFile(file.absolutePath, saveSettings, object : PhotoEditor.OnSaveListener {
                    override fun onSuccess(imagePath: String) {
                        val savedImageUri = Uri.fromFile(File(imagePath))
                        Toast.makeText(this@PhotoEditActivity, "Saved: $savedImageUri", Toast.LENGTH_SHORT).show()

                        val resultIntent = Intent().apply {
                            putExtra("editedImageUri", savedImageUri)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }

                    override fun onFailure(exception: Exception) {
                        Toast.makeText(this@PhotoEditActivity, "Save Failed!", Toast.LENGTH_SHORT).show()
                    }
                })
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
