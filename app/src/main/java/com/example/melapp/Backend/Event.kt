// Event.kt
package com.example.melapp.Backend

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Evento(
    val id: String = "", // ID del evento en Realtime Database
    val nombre: String = "",
    val descripcion: String = "",
    val fecha: Long = 0L, // Fecha en formato timestamp (Unix Epoch)
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val creadorId: String = "" // UID del usuario que crea el evento
)
