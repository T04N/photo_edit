package com.example.photoapp_maxmobile.setup

import android.content.Context
import android.net.Uri
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
        storageReference = storage.reference
    }

    fun uploadImageToFirebase(fileUri: Uri, context: Context) {
        val fileName = "images/${UUID.randomUUID()}.jpg"
        val imageRef = storageReference.child(fileName)

        val uploadTask = imageRef.putFile(fileUri)
        uploadTask.addOnSuccessListener {
            Toast.makeText(context, "Ảnh đã được tải lên thành công.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Đã có lỗi xảy ra khi tải lên ảnh.", Toast.LENGTH_SHORT).show()
        }
    }
}
