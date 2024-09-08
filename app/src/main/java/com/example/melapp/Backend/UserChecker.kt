package com.example.melapp.Backend

import com.google.firebase.firestore.FirebaseFirestore

fun checkUsernameExists(db: FirebaseFirestore, username: String, onResult: (Boolean) -> Unit) {
    db.collection("users").whereEqualTo("userName", username).get()
        .addOnSuccessListener { result ->
            onResult(!result.isEmpty)  // Retorna verdadero si el usuario ya existe
        }
        .addOnFailureListener { onResult(false) }
}