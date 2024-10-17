package com.example.melapp.Screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.Backend.getAddressFromLatLng
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.example.melapp.ReusableComponents.ReusableTopBar
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
            ReusableTopBar(
                screenTitle = "Detalles del Evento",
                onBackClick = { navController.popBackStack() }
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
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                is EventoState.SuccessSingle -> {
                    val documento = (eventoState as EventoState.SuccessSingle).data
                    val context = LocalContext.current
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .background(Color.White)
                    ) {
                        DisplayEventDetails(documento, context)
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
    )
}
@Composable
fun DisplayEventDetails(documento: DocumentSnapshot, context: Context) {
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
        "event_thumbnail" to "Imagen del Evento",
        "user_email" to "Correo electrónico",
        "event_post_date" to "Fecha de publicación",
        "event_age" to "Edad permitida",
        "event_status" to "Estado",
        "event_url" to "Link",
        "event_rating" to "Calificación",
        "event_verification" to "Verificación",
        "event_name" to "Nombre del Evento"
    )

    // Obtener latitud y longitud del documento
    val locationString = documento.getString("event_location") ?: ""
    val latLng = parseLatLng(locationString)
    var address = "Ubicación no disponible"
    latLng?.let {
        address = getAddressFromLatLng(context, it.first, it.second)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)  // Cambiado de Color(0xFF1C1C1C) a blanco
    ) {
        // Imagen destacada del evento con sombras y bordes redondeados
        val imageUrl = documento.getString("event_thumbnail")
        imageUrl?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shadow(8.dp)
            ) {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = "Imagen del evento",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de información general del evento
        InfoSection(title = "Información General") {
            GeneralInfo(documento, fieldMappings)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de detalles adicionales del evento
        InfoSection(title = "Detalles del Evento") {
            EventDetails(documento, fieldMappings, address)
        }
    }
}

// Sección con título y contenido
@Composable
fun InfoSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color(0xFF2E2E2E)  // Gris más oscuro para títulos
            ),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF918BAE)  // Color de fondo de la tarjeta
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

// Información general del evento
@Composable
fun GeneralInfo(documento: DocumentSnapshot, fieldMappings: Map<String, String>) {
    val eventName = documento.getString("event_title") ?: "Sin título"
    val eventCategory = documento.getString("event_category") ?: "Sin categoría"
    val eventDate = documento.getString("event_date") ?: "Sin fecha"

    Column {
        EventDetailRow(label = fieldMappings["event_title"] ?: "Título", value = eventName)
        EventDetailRow(label = fieldMappings["event_category"] ?: "Categoría", value = eventCategory)
        EventDetailRow(label = fieldMappings["event_date"] ?: "Fecha", value = eventDate)
    }
}

// Detalles adicionales del evento, incluyendo la ubicación convertida
@Composable
fun EventDetails(documento: DocumentSnapshot, fieldMappings: Map<String, String>, address: String) {
    val startTime = documento.getString("event_start_time") ?: "Sin hora de inicio"
    val endTime = documento.getString("event_end_time") ?: "Sin hora de fin"
    val attendees = documento.getString("event_number_of_attendees") ?: "N/A"
    val priceRange = documento.getString("event_price_range") ?: "Sin precio"

    Column {
        EventDetailRow(label = fieldMappings["event_start_time"] ?: "Hora de inicio", value = startTime)
        EventDetailRow(label = fieldMappings["event_end_time"] ?: "Hora de fin", value = endTime)
        EventDetailRow(label = fieldMappings["event_location"] ?: "Ubicación", value = address)  // Mostrar dirección en vez de lat/lng
        EventDetailRow(label = fieldMappings["event_number_of_attendees"] ?: "Asistentes", value = attendees)
        EventDetailRow(label = fieldMappings["event_price_range"] ?: "Precio", value = priceRange)
    }
}

// Fila de detalle con etiqueta y valor
@Composable
fun EventDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White  // Texto blanco para mejor contraste sobre el fondo morado
            ),
            fontWeight = FontWeight.Bold  // Hacerlo negrita para mayor legibilidad
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White  // Texto blanco para mejor contraste
            )
        )
    }
}