package com.example.melapp.Screens

import EventCardDescription
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.R
import com.example.melapp.ReusableComponents.CategoryBar
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.example.melapp.ReusableComponents.SearchTopBar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

fun parseLocation(locationString: String): LatLng? {
    val regex = """Lat:\s*([+-]?\d+(\.\d+)?),\s*Lng:\s*([+-]?\d+(\.\d+)?)""".toRegex()
    val matchResult = regex.find(locationString)
    return matchResult?.let {
        val lat = it.groupValues[1].toDoubleOrNull()
        val lng = it.groupValues[3].toDoubleOrNull()
        if (lat != null && lng != null) {
            println("Successfully parsed location: Lat $lat, Lng $lng")
            LatLng(lat, lng)
        } else {
            println("Failed to parse location: $locationString")
            null
        }
    }
}

@SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, eventoViewModel: EventoViewModel = viewModel()) {
    val eventoState by eventoViewModel.eventoState.collectAsState()
    var selectedEvent by remember { mutableStateOf<Evento?>(null) }
    var selectedCategories by remember { mutableStateOf<List<String>>(emptyList()) }
    var filteredEvents by remember { mutableStateOf<List<Evento>>(emptyList()) }

    LaunchedEffect(Unit) {
        eventoViewModel.obtenerTodosLosEventos()
    }

    // Efecto para filtrar eventos cuando cambian las categorías seleccionadas o el estado de eventos
    LaunchedEffect(eventoState, selectedCategories) {
        filteredEvents = when (eventoState) {
            is EventoState.SuccessList -> {
                val allEvents = (eventoState as EventoState.SuccessList).data.mapNotNull {
                    it.toObject(Evento::class.java)?.copy(id = it.id)
                }
                if (selectedCategories.isEmpty()) {
                    allEvents
                } else {
                    allEvents.filter { it.event_category in selectedCategories }
                }
            }
            else -> emptyList()
        }
    }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(40.7128, -74.0060), 12f, 0f, 0f)
    }

    var myLocationEnabled by remember { mutableStateOf(false) }

    if (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        myLocationEnabled = true
        centerCameraOnUser(fusedLocationClient, cameraPositionState, context)
    }

    Scaffold(
        topBar = {
            Column {
                SearchTopBar(
                    eventoViewModel = eventoViewModel,
                    onEventSelected = { evento ->
                        selectedEvent = evento
                        evento.event_location?.let { location ->
                            parseLocation(location)?.let { latLng ->
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                            }
                        }
                    }
                )
                CategoryBar(
                    onCategoriesSelected = { categories ->
                        selectedCategories = categories
                    }
                )
            }
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { navController.navigate("profileScreen") },
                onPostEventClick = { navController.navigate("map") },
                onSettingsClick = { navController.navigate("settingsScreen") },
                onPublishClick = { navController.navigate("event_list") }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        centerCameraOnUser(fusedLocationClient, cameraPositionState, context)
                    },
                    containerColor = Color(0xFF1A237E),
                    contentColor = Color.White,
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Icon(painterResource(R.drawable.ic_target), contentDescription = "Centrar en mi ubicación")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = myLocationEnabled)
        ) {
            // Usar filteredEvents en lugar de eventoState directamente
            filteredEvents.forEach { evento ->
                val location = parseLocation(evento.event_location ?: "")
                location?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = evento.event_name ?: "Evento sin nombre",
                        snippet = evento.event_description ?: "Sin descripción",
                        onClick = {
                            selectedEvent = evento
                            true
                        }
                    )
                }
            }
        }

        selectedEvent?.let { evento ->
            Box(modifier = Modifier.fillMaxSize()) {
                EventCardDescription(
                    evento = evento,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 150.dp, end = 16.dp, start = 86.dp)
                )
            }
        }
    }
}
// Función regular (no @Composable) para centrar la cámara en la ubicación real del usuario
@SuppressLint("MissingPermission")
fun centerCameraOnUser(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    context: Context
) {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                val userLocation = LatLng(location.latitude, location.longitude)
                // Actualizar la posición de la cámara
                cameraPositionState.position = CameraPosition(userLocation, 15f, 0f, 0f)
            } else {
                Toast.makeText(context, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error obteniendo ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}




