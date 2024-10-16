package com.example.melapp.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.R
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosFavoritosScreen(navController: NavController, eventoViewModel: EventoViewModel = viewModel()) {
    val eventoState by eventoViewModel.eventoState.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    val defaultLatLng = LatLng(0.0, 0.0)

    LaunchedEffect(Unit) {
        eventoViewModel.obtenerEventosFavoritosDelUsuario()
    }

    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = "Eventos Favoritos",
                onBackClick = { navController.popBackStack() }
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
                    val eventoDocuments = (eventoState as EventoState.SuccessList).data
                    if (eventoDocuments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No tienes eventos guardados como favoritos.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            items(eventoDocuments) { document ->
                                val evento = document.toObject(Evento::class.java)?.copy(id = document.id)
                                evento?.let {
                                    EventoListItem(
                                        evento = it,
                                        onViewDetails = {
                                            navController.navigate("eventDetails/${it.id}")
                                        },
                                        onMapClick = {
                                            val location = it.event_location?.let { loc -> parseLocation(loc) } ?: defaultLatLng
                                            eventoViewModel.updateSelectedEvent(it)
                                            eventoViewModel.updateCameraPosition(location)
                                            navController.navigate("map")
                                        },
                                        onDeleteFavorite = {
                                            showDeleteDialog = it.id
                                        }
                                    )
                                }
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
                    // Estado Idle u otro no manejado
                }
            }

            // Diálogo de confirmación para eliminar
            showDeleteDialog?.let { eventId ->
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = null },
                    title = { Text("Eliminar de Favoritos") },
                    text = { Text("¿Estás seguro de que quieres eliminar este evento de tus favoritos?") },
                    confirmButton = {
                        TextButton(onClick = {
                            eventoViewModel.eliminarEventoFavorito(eventId)
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
        }
    )
}

@Composable
fun EventoListItem(
    evento: Evento,
    onViewDetails: () -> Unit,
    onMapClick: () -> Unit,
    onDeleteFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = evento.event_thumbnail,
                contentDescription = "Event Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                val painterState = painter.state
                if (painterState is AsyncImagePainter.State.Loading || painterState is AsyncImagePainter.State.Error) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    SubcomposeAsyncImageContent()
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onViewDetails)
            ) {
                Text(text = evento.event_name ?: "", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = evento.event_description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ubicación: ${evento.event_location}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column {
                IconButton(onClick = onViewDetails) {
                    Icon(painter = painterResource(R.drawable.ic_info), contentDescription = "Ver Detalles")
                }
                IconButton(onClick = onMapClick) {
                    Icon(painter = painterResource(R.drawable.ic_earth), contentDescription = "Ver en el Mapa")
                }
                IconButton(onClick = onDeleteFavorite) {
                    Icon(painter = painterResource(R.drawable.ic_bin), contentDescription = "Eliminar de Favoritos")
                }
            }
        }
    }
}