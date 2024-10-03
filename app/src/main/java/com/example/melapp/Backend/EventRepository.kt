// EventoRepository.kt
package com.example.melapp.Backend

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.example.melapp.Backend.Evento
import kotlinx.coroutines.tasks.await

class EventoRepository {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val eventosRef: DatabaseReference = db.getReference("eventos")

    // Crear un nuevo evento
    suspend fun crearEvento(evento: Evento): Result<String> {
        return try {
            val nuevoEventoRef = eventosRef.push()
            nuevoEventoRef.setValue(evento).await()
            Result.success(nuevoEventoRef.key ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener un evento por ID
    suspend fun obtenerEvento(id: String): Result<Evento?> {
        return try {
            val snapshot = eventosRef.child(id).get().await()
            if (snapshot.exists()) {
                val evento = snapshot.getValue(Evento::class.java)
                Result.success(evento)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar un evento
    suspend fun actualizarEvento(id: String, datosActualizados: Map<String, Any>): Result<Void?> {
        return try {
            eventosRef.child(id).updateChildren(datosActualizados).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar un evento
    suspend fun eliminarEvento(id: String): Result<Void?> {
        return try {
            eventosRef.child(id).removeValue().await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todos los eventos
    suspend fun obtenerTodosLosEventos(): Result<List<Evento>> {
        return try {
            val snapshot = eventosRef.get().await()
            val eventos = snapshot.children.mapNotNull { it.getValue(Evento::class.java) }
            Result.success(eventos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener eventos por usuario
    suspend fun obtenerEventosPorUsuario(usuarioId: String): Result<List<Evento>> {
        return try {
            val snapshot = eventosRef.orderByChild("creadorId").equalTo(usuarioId).get().await()
            val eventos = snapshot.children.mapNotNull { it.getValue(Evento::class.java) }
            Result.success(eventos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

