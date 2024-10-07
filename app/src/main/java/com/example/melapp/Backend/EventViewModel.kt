package com.example.melapp.Backend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Mantener una sola declaración de EventoState
sealed class EventoState {
    object Idle : EventoState()
    object Loading : EventoState()
    data class SuccessList(val data: List<DocumentSnapshot>) : EventoState() // Para cuando obtienes una lista de eventos
    data class SuccessSingle(val data: DocumentSnapshot) : EventoState() // Para un solo evento
    data class Success(val evento: Evento) : EventoState() // Para cuando obtienes un evento convertido a tu clase Evento
    data class Error(val message: String) : EventoState()
}

data class Evento(
    val eventName: String = "",
    val eventDescription: String = "",
    val eventLocation: String = "",
    var eventThumbnail: String = "" // Nuevo campo para la URL de la miniatura
)

class EventoViewModel : ViewModel() {

    private val _eventoState = MutableStateFlow<EventoState>(EventoState.Idle)
    val eventoState: StateFlow<EventoState> = _eventoState

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Obtener un evento por ID
    fun obtenerEvento(eventoId: String) {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading
                Log.d("EventoViewModel", "Fetching event with ID: $eventoId")

                val documentSnapshot =
                    firestore.collection("Event").document(eventoId).get().await()

                if (documentSnapshot.exists()) {
                    Log.d("EventoViewModel", "Successfully fetched event: ${documentSnapshot.id}")
                    _eventoState.value = EventoState.SuccessSingle(documentSnapshot)
                } else {
                    Log.e("EventoViewModel", "Event not found")
                    _eventoState.value = EventoState.Error("Evento no encontrado")
                }
            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error fetching event: ${e.message}", e)
                _eventoState.value = EventoState.Error("Error al obtener el evento: ${e.message}")
            }
        }
    }

    // Obtener todos los eventos
    fun obtenerTodosLosEventos() {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading
                Log.d("EventoViewModel", "Starting to fetch all events from Firestore")

                val eventosCollection = firestore.collection("Event")
                val querySnapshot = eventosCollection.get().await()
                val documentos = querySnapshot.documents

                Log.d("EventoViewModel", "Successfully fetched ${documentos.size} events")
                _eventoState.value = EventoState.SuccessList(documentos)
            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error fetching events: ${e.message}", e)
                _eventoState.value = EventoState.Error("Error al obtener los eventos: ${e.message}")
            }
        }
    }

    fun obtenerEventoPorId(eventoId: String) {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading

                val documento = firestore.collection("Event").document(eventoId).get().await()
                val evento = documento.toObject(Evento::class.java)

                if (evento != null) {
                    if (evento.eventThumbnail.isNotEmpty()) {
                        try {
                            val storageRef = storage.reference.child(evento.eventThumbnail)
                            val downloadUrl = storageRef.downloadUrl.await()
                            evento.eventThumbnail = downloadUrl.toString()
                        } catch (e: Exception) {
                            Log.e("EventoViewModel", "Error obteniendo URL de imagen: ${e.message}")
                            // Si hay un error al obtener la URL, mantenemos la referencia original
                        }
                    }
                    _eventoState.value = EventoState.Success(evento)
                } else {
                    _eventoState.value = EventoState.Error("Evento no encontrado")
                }
            } catch (e: Exception) {
                _eventoState.value = EventoState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }

}

}









