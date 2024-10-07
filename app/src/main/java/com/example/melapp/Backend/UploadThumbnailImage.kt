package com.example.melapp.Backend

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

// Function to upload image to Firebase Storage and return the public download URL
fun uploadThumbnailImage(
    uri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    val thumbnailRef = storage.child("event_thumbnail_images/${UUID.randomUUID()}")

    // Sube el archivo al storage
    thumbnailRef.putFile(uri)
        .addOnSuccessListener {
            // Obtener la URL pública de descarga
            thumbnailRef.downloadUrl
                .addOnSuccessListener { downloadUrl ->
                    // Aquí retornamos el enlace público
                    onSuccess(downloadUrl.toString())
                }
                .addOnFailureListener {
                    // Si falla al obtener la URL de descarga
                    onFailure()
                }
        }
        .addOnFailureListener {
            // Si falla al subir la imagen
            onFailure()
        }
}

