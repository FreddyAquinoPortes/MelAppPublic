package com.example.melapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.R
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.google.firebase.firestore.DocumentSnapshot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetailsScreen(navController: NavController, eventoId: String, eventoViewModel: EventoViewModel = viewModel()) {
    val eventoState by eventoViewModel.eventoState.collectAsState()

    LaunchedEffect(eventoId) {
        eventoViewModel.obtenerEvento(eventoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { navController.navigate("profileScreen") },
                onPostEventClick = { navController.navigate("map") },
                onSettingsClick = { navController.navigate("settingsScreen") },
                onPublishClick = { navController.navigate("event_form") }
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
                is EventoState.SuccessSingle -> {
                    val documento = (eventoState as EventoState.SuccessSingle).data
                    DisplayEventDetails(documento)
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
    )
}

@Composable
fun DisplayEventDetails(documento: DocumentSnapshot) {
    // Mapea las claves a nombres amigables
    val fieldMappings = mapOf(
        "event_title" to "Título del Evento",
        "event_description" to "Descripción",
        "event_date" to "Fecha",
        "event_start_time" to "Hora de inicio",
        "event_end_time" to "Hora de fin",
        "event_location" to "Ubicación",
        "event_number_of_attendees" to "Número de asistentes",
        "event_price_range" to "Rango de precios",
        "event_category" to "Categoría",
        "event_thumbnail" to "Imagen del Evento",  // Este campo es especial porque será tratado como una imagen
        "user_email" to "Correo electrónico",
        "event_post_date" to "Fecha de publicación",
        "event_age" to "Edad permitida",
        "event_status" to "Estado",
        "event_url" to "Link",
        "event_rating" to "Calificación",
        "event_verification" to "Verificación",
        "event_name" to "Nombre del Evento"
    )

    // Carga el URL de la miniatura
    val imageUrl = documento.getString("event_thumbnail")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Mostrar la imagen del evento si está disponible
        imageUrl?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Imagen del evento",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Mostrar los detalles del evento con nombres amigables
        documento.data?.forEach { (key, value) ->
            val fieldName = fieldMappings[key] ?: key  // Si no hay un nombre amigable, usa la clave original
            if (key != "event_thumbnail") {  // Evita mostrar el link de la imagen como texto
                Text(
                    text = "$fieldName: $value",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
