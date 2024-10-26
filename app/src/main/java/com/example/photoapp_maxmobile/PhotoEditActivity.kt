package com.example.photoapp_maxmobile

import android.Manifest
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
            override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
                // Handle text change event
            }

            override fun onAddViewListener(viewType: ja.burhanrashid52.photoeditor.ViewType, numberOfAddedViews: Int) {
                // Handle add view event
            }

            override fun onRemoveViewListener(viewType: ja.burhanrashid52.photoeditor.ViewType, numberOfAddedViews: Int) {
                // Handle remove view event
            }

            override fun onStartViewChangeListener(viewType: ja.burhanrashid52.photoeditor.ViewType) {
                // Handle start view change event
            }

            override fun onStopViewChangeListener(viewType: ja.burhanrashid52.photoeditor.ViewType) {
                // Handle stop view change event
            }

            override fun onTouchSourceImage(event: MotionEvent) {
                // Handle touch event on source image
            }
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
            // Create a container for each filter button and label
            val filterContainer = LinearLayout(this)
            filterContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            filterContainer.orientation = LinearLayout.VERTICAL
            filterContainer.gravity = Gravity.CENTER

            // Create ImageButton for filter
            val filterButton = ImageButton(this)
            filterButton.layoutParams = LinearLayout.LayoutParams(200, 200)
            filterButton.setImageResource(R.drawable.ic_new_filter) // Replace with appropriate filter icon
            filterButton.setOnClickListener {
                photoEditor.setFilterEffect(filter)
            }

            // Create TextView for filter name
            val filterName = TextView(this)
            filterName.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            filterName.text = filter.name
            filterName.textSize = 12f
            filterName.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            filterName.gravity = Gravity.CENTER

            // Add ImageButton and TextView to container
            filterContainer.addView(filterButton)
            filterContainer.addView(filterName)

            // Add container to filter layout
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
