// AllEventListScreen.kt
package com.example.melapp.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.Backend.toEvento
import com.example.melapp.ReusableComponents.CategoryBar
import com.example.melapp.ReusableComponents.EventCardDescription2
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.example.melapp.ReusableComponents.ReusableTopBar


@Composable
fun AllEventScreen(navController: NavController, eventoViewModel: EventoViewModel = viewModel()) {
    val eventoState by eventoViewModel.eventoState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }  // Estado para almacenar el texto de búsqueda
    var filteredEvents by remember { mutableStateOf(listOf<Evento>()) }  // Estado para almacenar los eventos filtrados
    var selectedCategories by remember { mutableStateOf(emptyList<String>()) }  // Estado para almacenar las categorías seleccionadas

    // Llamar a obtenerTodosLosEventos() una vez que la pantalla se cargue
    LaunchedEffect(Unit) {
        eventoViewModel.obtenerTodosLosEventos()
    }

    // Filtrar los eventos cada vez que el estado de la búsqueda o las categorías cambie
    LaunchedEffect(searchQuery, selectedCategories, eventoState) {
        if (eventoState is EventoState.SuccessList) {
            val eventos = (eventoState as EventoState.SuccessList).data.mapNotNull { it.toEvento() }
            filteredEvents = eventos.filter { evento ->
                val matchesSearchQuery = evento.event_name?.contains(searchQuery, ignoreCase = true) == true
                val matchesCategory = selectedCategories.isEmpty() || evento.event_category in selectedCategories
                matchesSearchQuery && matchesCategory
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                ReusableTopBar(
                    screenTitle = "Eventos",
                    onBackClick = { navController.popBackStack() }
                )
                // Barra de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar eventos...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Barra de categorías
                CategoryBar(
                    onCategoriesSelected = { selectedCategories = it }
                )
            }
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { navController.navigate("profileScreen") },
                onPostEventClick = { navController.navigate("map") },
                onPublishClick = { navController.navigate("all_event_screen") },
                onSettingsClick = { navController.navigate("settingsScreen") }
            )
        },
        content = { innerPadding ->
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
                    if (filteredEvents.isEmpty()) {
                        // Si no hay eventos filtrados, mostrar un mensaje
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontraron eventos.")
                        }
                    } else {
                        // Mostrar la lista de eventos filtrados
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            items(filteredEvents) { evento ->
                                val context = LocalContext.current
                                EventCardDescription2(
                                    evento = evento,
                                    modifier = Modifier.clickable {
                                        // Navegar a la pantalla de detalles del evento al hacer clic
                                        navController.navigate("eventDetails/${evento.id}")
                                    },
                                    onSaveClick = { isSaved ->
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
