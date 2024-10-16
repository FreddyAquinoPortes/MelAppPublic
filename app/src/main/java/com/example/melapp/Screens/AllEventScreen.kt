// AllEventListScreen.kt
package com.example.melapp.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.Backend.toEvento
import com.example.melapp.ReusableComponents.EventCardDescription2
import com.example.melapp.ReusableComponents.ReusableTopBar


@Composable
fun AllEventScreen(navController: NavController, eventoViewModel: EventoViewModel = viewModel()) {
    val eventoState by eventoViewModel.eventoState.collectAsState()

    // Llamar a obtenerTodosLosEventos() una vez que la pantalla se cargue
    LaunchedEffect(Unit) {
        eventoViewModel.obtenerTodosLosEventos()
    }

    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = "Eventos",
                onBackClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            when (eventoState) {
                is EventoState.Loading -> {
                    // Mostrar un indicador de carga
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
                    // Convertir los documentos a objetos Evento
                    val eventos = (eventoState as EventoState.SuccessList).data.mapNotNull { it.toEvento() }

                    if (eventos.isEmpty()) {
                        // Si no hay eventos, mostrar un mensaje
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay eventos disponibles.")
                        }
                    } else {
                        // Mostrar la lista de eventos
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            items(eventos) { evento ->
                                val context = LocalContext.current
                                EventCardDescription2(
                                    evento = evento,
                                    modifier = Modifier.clickable {
                                        // Navegar a la pantalla de detalles del evento al hacer clic
                                        navController.navigate("eventDetails/${evento.id}")
                                    },
                                    onSaveClick = { isSaved ->
                                        // Lógica que deseas ejecutar cuando el estado de guardado cambia
                                        // Puedes manejar aquí la respuesta según si el evento fue guardado o eliminado
                                        if (isSaved) {
                                            Toast.makeText(context, "Evento guardado en favoritos", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Evento eliminado de favoritos", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is EventoState.Error -> {
                    // Mostrar un mensaje de error
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
                    // Manejar estados no conocidos
                }
            }
        }
    )
}



