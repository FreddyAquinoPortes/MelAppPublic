// EventoViewModel.kt
package com.example.melapp.Backend

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
    val user_email: String? = null,
    val event_location: String? = null,
    val event_post_date: String? = null,
    val event_description: String? = null,
    val event_title: String? = null,
    val event_category: String? = null,
    val event_url: String? = null,
    val event_end_time: String? = null,
    val event_number_of_attendees: String? = null,
    val event_verification: String? = null,
    val event_date: String? = null,
    val event_name: String? = null,
    val event_start_time: String? = null,
    val event_price_range: String? = null,
    val event_age: String? = null,
    val event_status: String? = null,
    var event_thumbnail: String? = null,
    val id: String? = null, // Agrega este campo para almacenar el ID del evento
    val event_rating: String? = null

)

class EventoViewModel : ViewModel() {

    private val _eventoState = MutableStateFlow<EventoState>(EventoState.Idle)
    val eventoState: StateFlow<EventoState> = _eventoState
    private val _selectedEvent = MutableStateFlow<Evento?>(null)
    val selectedEvent: StateFlow<Evento?> = _selectedEvent

    fun updateSelectedEvent(evento: Evento) {
        _selectedEvent.value = evento
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _cameraPosition = mutableStateOf<CameraPosition?>(null)
    val cameraPosition: State<CameraPosition?> = _cameraPosition
    fun updateCameraPosition(latLng: LatLng) {
        _cameraPosition.value = CameraPosition.fromLatLngZoom(latLng, 15f)
    }
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

    // Obtener todos los eventos del usuario actual
    fun obtenerEventosDelUsuario() {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading
                Log.d("EventoViewModel", "Fetching events for user")

                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _eventoState.value = EventoState.Error("Usuario no autenticado")
                    return@launch
                }

                val userEmail = currentUser.email ?: ""

                val querySnapshot = firestore.collection("Event")
                    .whereEqualTo("user_email", userEmail)
                    .get()
                    .await()

                val documentos = querySnapshot.documents

                Log.d("EventoViewModel", "Successfully fetched ${documentos.size} events for user")
                _eventoState.value = EventoState.SuccessList(documentos)
            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error fetching user events: ${e.message}", e)
                _eventoState.value = EventoState.Error("Error al obtener los eventos: ${e.message}")
            }
        }
    }

    fun obtenerEventoPorId(eventId: String, onEventLoaded: (Evento) -> Unit) {
        firestore.collection("eventos").document(eventId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Convert the document to Evento
                    val evento = document.toObject(Evento::class.java)

                    // Check if evento is not null before notifying the caller
                    if (evento != null) {
                        onEventLoaded(evento) // Notify the caller with the loaded event
                    } else {
                        Log.e("EventoViewModel", "El evento es null")
                    }
                } else {
                    Log.e("EventoViewModel", "No existe el documento")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EventoViewModel", "Error obteniendo evento: ${exception.message}")
            }
    }



    // Crear un nuevo evento
    fun crearEvento(evento: Evento) {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading
                Log.d("EventoViewModel", "Creating new event")

                val eventData = hashMapOf(
                    "user_email" to (auth.currentUser?.email ?: ""),
                    "event_age" to evento.event_age,
                    "event_category" to evento.event_category,
                    "event_date" to evento.event_date,
                    "event_description" to evento.event_description,
                    "event_end_time" to evento.event_end_time,
                    "event_location" to evento.event_location,
                    "event_name" to evento.event_name,
                    "event_number_of_attendees" to evento.event_number_of_attendees,
                    "event_price_range" to evento.event_price_range,
                    "event_rating" to evento.event_rating,
                    "event_start_time" to evento.event_start_time,
                    "event_status" to evento.event_status,
                    "event_title" to evento.event_title,
                    "event_url" to evento.event_url,
                    "event_verification" to evento.event_verification,
                    "event_post_date" to evento.event_post_date,
                    "event_thumbnail" to (evento.event_thumbnail ?: "")
                )

                // Usar la función de suspensión add().await()
                val documentReference = firestore.collection("Event")
                    .add(eventData)
                    .await()

                Log.d("EventoViewModel", "Event created with ID: ${documentReference.id}")

                // Obtener el documento creado
                //val createdDocument = firestore.collection("Event").document(documentReference.id).get().await()
                val createdDocument = documentReference.get().await()
                _eventoState.value = EventoState.SuccessSingle(createdDocument)

            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error in crearEvento: ${e.message}", e)
                _eventoState.value = EventoState.Error("Error al crear el evento: ${e.message}")
            }
        }
    }

    // Actualizar un evento existente
    fun actualizarEvento(evento: Evento) {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading
                Log.d("EventoViewModel", "Updating event with ID: ${evento.id}")

                val eventData = hashMapOf(
                    "user_email" to (auth.currentUser?.email ?: ""),
                    "event_age" to evento.event_age,
                    "event_category" to evento.event_category,
                    "event_date" to evento.event_date,
                    "event_description" to evento.event_description,
                    "event_end_time" to evento.event_end_time,
                    "event_location" to evento.event_location,
                    "event_name" to evento.event_name,
                    "event_number_of_attendees" to evento.event_number_of_attendees,
                    "event_price_range" to evento.event_price_range,
                    "event_rating" to evento.event_rating,
                    "event_start_time" to evento.event_start_time,
                    "event_status" to evento.event_status,
                    "event_title" to evento.event_title,
                    "event_url" to evento.event_url,
                    "event_verification" to evento.event_verification,
                    "event_post_date" to evento.event_post_date,
                    "event_thumbnail" to (evento.event_thumbnail ?: "")
                )

                // Usar la función de suspensión update().await()
                firestore.collection("Event").document(evento.id!!)
                    .update(eventData as Map<String, Any>)
                    .await()

                Log.d("EventoViewModel", "Event updated successfully")

                // Obtener el documento actualizado
                val updatedDocument = firestore.collection("Event").document(evento.id).get().await()
                _eventoState.value = EventoState.SuccessSingle(updatedDocument)

            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error in actualizarEvento: ${e.message}", e)
                _eventoState.value = EventoState.Error("Error al actualizar el evento: ${e.message}")
            }
        }
    }
    suspend fun searchEvents(query: String): List<Evento> {
        return withContext(Dispatchers.IO) {
            try {
                // Obtener todos los eventos de Firebase
                val eventosCollection = firestore.collection("Event")
                val querySnapshot = eventosCollection.get().await()

                // Convertir los documentos a objetos Evento
                val eventos = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Evento::class.java)?.copy(id = document.id)
                }

                // Filtrar los eventos que coincidan con la consulta (insensible a mayúsculas/minúsculas)
                val filteredEvents = eventos.filter { evento ->
                    val queryLower = query.lowercase() // Convertir la consulta a minúsculas
                    val eventNameLower = evento.event_name?.lowercase() ?: "" // Convertir el nombre del evento a minúsculas

                    // Comprobar si el nombre del evento contiene todas las palabras de la consulta
                    queryLower.split(" ").all { queryPart ->
                        eventNameLower.contains(queryPart)
                    }
                }

                filteredEvents
            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error searching events: ${e.message}", e)
                emptyList()
            }
        }
    }


    // Eliminar un evento por ID
    fun eliminarEvento(eventoId: String) {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading
                Log.d("EventoViewModel", "Deleting event with ID: $eventoId")

                // Usar la función de suspensión delete().await()
                firestore.collection("Event").document(eventoId)
                    .delete()
                    .await()

                Log.d("EventoViewModel", "Event deleted successfully")

                // Refrescar la lista de eventos del usuario
                obtenerEventosDelUsuario()

            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error in eliminarEvento: ${e.message}", e)
                _eventoState.value = EventoState.Error("Error al eliminar el evento: ${e.message}")
            }
        }
    }
    fun obtenerEventosFavoritosDelUsuario() {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading
                Log.d("EventoViewModel", "Fetching favorite events for user")

                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _eventoState.value = EventoState.Error("Usuario no autenticado")
                    return@launch
                }

                val userEmail = currentUser.email ?: ""

                // Obtener todos los documentos donde el email del usuario coincida
                val querySnapshot = firestore.collection("event_saved")
                    .whereEqualTo("user_email", userEmail)
                    .get()
                    .await()

                val favoriteEventIds = querySnapshot.documents.mapNotNull { it.getString("id_event") }

                // Obtenemos los DocumentSnapshot de los eventos favoritos
                val eventoDocuments = favoriteEventIds.mapNotNull { eventId ->
                    firestore.collection("Event").document(eventId).get().await()
                }

                Log.d("EventoViewModel", "Successfully fetched ${eventoDocuments.size} favorite events")
                _eventoState.value = EventoState.SuccessList(eventoDocuments)

            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error fetching favorite events: ${e.message}", e)
                _eventoState.value = EventoState.Error("Error al obtener los eventos favoritos: ${e.message}")
            }
        }
    }
    fun eliminarEventoFavorito(eventoId: String) {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading
                Log.d("EventoViewModel", "Deleting favorite event with ID: $eventoId")

                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _eventoState.value = EventoState.Error("Usuario no autenticado")
                    return@launch
                }

                val userEmail = currentUser.email ?: ""

                // Buscar el documento que coincida con el id del evento y el email del usuario
                val querySnapshot = firestore.collection("event_saved")
                    .whereEqualTo("id_event", eventoId)
                    .whereEqualTo("user_email", userEmail)
                    .get()
                    .await()

                if (querySnapshot.documents.isNotEmpty()) {
                    // Debería haber solo un documento que coincida
                    val document = querySnapshot.documents.first()
                    document.reference.delete().await()

                    Log.d("EventoViewModel", "Favorite event deleted successfully")

                    // Refrescar la lista de eventos favoritos
                    obtenerEventosFavoritosDelUsuario()
                } else {
                    _eventoState.value = EventoState.Error("No se encontró el evento favorito")
                }

            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error in eliminarEventoFavorito: ${e.message}", e)
                _eventoState.value = EventoState.Error("Error al eliminar el evento favorito: ${e.message}")
            }
        }
    }
}