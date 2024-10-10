// DocumentSnapshotExtensions.kt
package com.example.melapp.Backend

import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toEvento(): Evento? {
    return this.toObject(Evento::class.java)?.copy(id = this.id)
}