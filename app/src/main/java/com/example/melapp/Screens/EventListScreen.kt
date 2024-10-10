// EventListScreen.kt
package com.example.melapp.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.Backend.toEvento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(navController: NavController, eventoViewModel: EventoViewModel = viewModel()) {
    val eventoState by eventoViewModel.eventoState.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf<String?>(null) } // ID del evento a eliminar

    LaunchedEffect(Unit) {
        eventoViewModel.obtenerEventosDelUsuario()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Eventos") },
                actions = {
                    IconButton(onClick = { navController.navigate("event_form") }) {
                        Icon(Icons.Default.Add, contentDescription = "Crear Evento")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("event_form") }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Evento")
            }
        }
    ) { innerPadding ->
        when (eventoState) {
            is EventoState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is EventoState.SuccessList -> {
                val eventos = (eventoState as EventoState.SuccessList).data.mapNotNull { it.toEvento() }
                if (eventos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes eventos creados.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        items(eventos) { evento ->
                            EventoListItem(
                                evento = evento,
                                onEdit = {
                                    navController.navigate("event_form/${evento.id}")
                                },
                                onDelete = {
                                    showDeleteDialog = evento.id
                                },
                                onViewDetails = {
                                    navController.navigate("eventDetails/${evento.id}")
                                }
                            )
                        }
                    }
                }
            }
            is EventoState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (eventoState as EventoState.Error).message, color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                // Estado Idle o no manejado
            }
        }
    }

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { eventoId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Evento") },
            text = { Text("¿Estás seguro de que quieres eliminar este evento? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    eventoViewModel.eliminarEvento(eventoId)
                    showDeleteDialog = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Manejar estados de eliminación y actualización
    LaunchedEffect(eventoState) {
        when (val state = eventoState) {
            is EventoState.SuccessSingle -> {
                // Después de crear o actualizar un evento, refrescar la lista
                Toast.makeText(context, "Operación exitosa", Toast.LENGTH_SHORT).show()
                eventoViewModel.obtenerEventosDelUsuario()
            }
            is EventoState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> { /* No hacer nada */ }
        }
    }
}
