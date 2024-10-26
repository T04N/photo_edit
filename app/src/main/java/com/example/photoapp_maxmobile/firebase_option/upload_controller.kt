package com.example.photoapp_maxmobile.setup

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.UUID

object FirebaseSetup {
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    fun initialize() {
        storage = Firebase.storage
        // Sử dụng URL cung cấp để tạo reference
        storageReference = storage.getReferenceFromUrl("gs://photoedit-a2c17.appspot.com")
    }

    fun uploadImageToFirebase(fileUri: Uri, context: Context) {
        if (!::storageReference.isInitialized) {
            Toast.makeText(context, "Firebase chưa được khởi tạo. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "images/${UUID.randomUUID()}.jpg"
        val imageRef = storageReference.child(fileName) // Tạo reference con cho file

        val uploadTask = imageRef.putFile(fileUri)
        uploadTask.addOnSuccessListener {
            Toast.makeText(context, "Ảnh đã được tải lên thành công.", Toast.LENGTH_SHORT).show()
            Log.d("FirebaseUpload", "Upload thành công: $fileName")
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Đã có lỗi xảy ra khi tải lên ảnh: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.e("FirebaseUpload", "Upload thất bại: $fileName", exception)
        }

    }
}
