package com.example.melapp.Backend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class EventoState {
    object Idle : EventoState()
    object Loading : EventoState()
    data class SuccessList(val data: List<DocumentSnapshot>) : EventoState()
    data class SuccessSingle(val data: DocumentSnapshot) : EventoState()
    data class Error(val message: String) : EventoState()
}

class EventoViewModel : ViewModel() {

    private val _eventoState = MutableStateFlow<EventoState>(EventoState.Idle)
    val eventoState: StateFlow<EventoState> = _eventoState

    private val firestore = FirebaseFirestore.getInstance()

    fun obtenerEvento(eventoId: String) {
        viewModelScope.launch {
            try {
                _eventoState.value = EventoState.Loading
                Log.d("EventoViewModel", "Fetching event with ID: $eventoId")

                val documentSnapshot = firestore.collection("Event").document(eventoId).get().await()

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
}






