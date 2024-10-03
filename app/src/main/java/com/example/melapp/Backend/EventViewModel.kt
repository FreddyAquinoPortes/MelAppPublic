// EventoViewModel.kt
package com.example.melapp.Backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EventoState {
    object Idle : EventoState()
    object Loading : EventoState()
    data class Success(val data: Any?) : EventoState()
    data class Error(val message: String) : EventoState()
}

class EventoViewModel(
    private val repository: EventoRepository = EventoRepository()
) : ViewModel() {

    private val _eventoState = MutableStateFlow<EventoState>(EventoState.Idle)
    val eventoState: StateFlow<EventoState> = _eventoState

    // Crear un evento
    fun crearEvento(evento: Evento) {
        viewModelScope.launch {
            _eventoState.value = EventoState.Loading
            val resultado = repository.crearEvento(evento)
            resultado.fold(
                onSuccess = { id ->
                    _eventoState.value = EventoState.Success(id)
                },
                onFailure = { throwable ->
                    _eventoState.value = EventoState.Error(throwable.localizedMessage ?: "Error al crear el evento")
                }
            )
        }
    }

    // Obtener un evento
    fun obtenerEvento(id: String) {
        viewModelScope.launch {
            _eventoState.value = EventoState.Loading
            val resultado = repository.obtenerEvento(id)
            resultado.fold(
                onSuccess = { evento ->
                    if (evento != null) {
                        _eventoState.value = EventoState.Success(evento)
                    } else {
                        _eventoState.value = EventoState.Error("Evento no encontrado")
                    }
                },
                onFailure = { throwable ->
                    _eventoState.value = EventoState.Error(throwable.localizedMessage ?: "Error al obtener el evento")
                }
            )
        }
    }

    // Actualizar un evento
    fun actualizarEvento(id: String, datosActualizados: Map<String, Any>) {
        viewModelScope.launch {
            _eventoState.value = EventoState.Loading
            val resultado = repository.actualizarEvento(id, datosActualizados)
            resultado.fold(
                onSuccess = {
                    _eventoState.value = EventoState.Success("Evento actualizado correctamente")
                },
                onFailure = { throwable ->
                    _eventoState.value = EventoState.Error(throwable.localizedMessage ?: "Error al actualizar el evento")
                }
            )
        }
    }

    // Eliminar un evento
    fun eliminarEvento(id: String) {
        viewModelScope.launch {
            _eventoState.value = EventoState.Loading
            val resultado = repository.eliminarEvento(id)
            resultado.fold(
                onSuccess = {
                    _eventoState.value = EventoState.Success("Evento eliminado correctamente")
                },
                onFailure = { throwable ->
                    _eventoState.value = EventoState.Error(throwable.localizedMessage ?: "Error al eliminar el evento")
                }
            )
        }
    }

    // Obtener todos los eventos
    fun obtenerTodosLosEventos() {
        viewModelScope.launch {
            _eventoState.value = EventoState.Loading
            val resultado = repository.obtenerTodosLosEventos()
            resultado.fold(
                onSuccess = { eventos ->
                    _eventoState.value = EventoState.Success(eventos)
                },
                onFailure = { throwable ->
                    _eventoState.value = EventoState.Error(throwable.localizedMessage ?: "Error al obtener los eventos")
                }
            )
        }
    }

    // Obtener eventos por usuario
    fun obtenerEventosPorUsuario(usuarioId: String) {
        viewModelScope.launch {
            _eventoState.value = EventoState.Loading
            val resultado = repository.obtenerEventosPorUsuario(usuarioId)
            resultado.fold(
                onSuccess = { eventos ->
                    _eventoState.value = EventoState.Success(eventos)
                },
                onFailure = { throwable ->
                    _eventoState.value = EventoState.Error(throwable.localizedMessage ?: "Error al obtener los eventos del usuario")
                }
            )
        }
    }
}
