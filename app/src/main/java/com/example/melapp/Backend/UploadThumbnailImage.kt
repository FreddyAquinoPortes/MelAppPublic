package com.example.melapp.Backend

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

// Function to upload image to Firebase Storage and return the storage path
fun uploadThumbnailImage(
    uri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    val thumbnailRef = storage.child("event_thumbnail_images/${UUID.randomUUID()}")
    thumbnailRef.putFile(uri)
        .addOnSuccessListener {
            // Get the storage path instead of the download URL
            val storagePath = thumbnailRef.path
            onSuccess(storagePath)
        }
        .addOnFailureListener {
            onFailure()
        }
}
