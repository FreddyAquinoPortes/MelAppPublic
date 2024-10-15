package com.example.melapp.Screens

import EventCardDescription
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.R
import com.example.melapp.ReusableComponents.CategoryBar
import com.example.melapp.ReusableComponents.EventFilterCard
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.example.melapp.ReusableComponents.SearchTopBar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
// Función para convertir el string de color a un objeto Color
fun parseColor(colorString: String): Color {
    return try {
        // Si el string empieza con "0x" o "#", los removemos
        val cleanColorString = colorString.removePrefix("0x").removePrefix("#")
        Color(android.graphics.Color.parseColor("#$cleanColorString"))
    } catch (e: Exception) {
        Log.e("ColorParsing", "Error parsing color: $colorString", e)
        Color.Red // Color por defecto si hay un error
    }
}


// Función para obtener los colores de las categorías
suspend fun getCategoryColors(): Map<String, Color> {
    return withContext(Dispatchers.IO) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val querySnapshot = firestore.collection("event_category").get().await()
            querySnapshot.documents.associate { document ->
                val categoryName = document.getString("category_name") ?: ""
                val colorCode = document.getString("category_color_code") ?: "FF0000" // Rojo por defecto si hay error
                categoryName to parseColor(colorCode)
            }
        } catch (e: Exception) {
            Log.e("MapScreen", "Error fetching category colors: ${e.message}", e)
            emptyMap()
        }
    }
}


// Función para convertir Color a HUE para BitmapDescriptorFactory
fun colorToHue(color: Color): Float {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(color.toArgb(), hsv)
    Log.d("MapScreen", "Hue for color ${color.toArgb()}: ${hsv[0]}") // Agregar logging
    return hsv[0]  // Devuelve solo el hue (tono)
}

@SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, eventoViewModel: EventoViewModel = viewModel()) {
    val context = LocalContext.current
    val eventoState by eventoViewModel.eventoState.collectAsState()
    var selectedEvent by remember { mutableStateOf<Evento?>(null) }
    var filteredEvents by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var categoryColors by remember { mutableStateOf<Map<String, Color>>(emptyMap()) }
    var appliedFilters by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var selectedCategories by remember { mutableStateOf<List<String>>(emptyList()) }
    var showFilterCard by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        eventoViewModel.obtenerTodosLosEventos()
        categoryColors = getCategoryColors()
    }

    // Efecto para filtrar eventos cuando cambian el estado de eventos o los filtros aplicados
    LaunchedEffect(eventoState, appliedFilters, selectedCategories) {
        filteredEvents = when (eventoState) {
            is EventoState.SuccessList -> {
                val allEvents = (eventoState as EventoState.SuccessList).data.mapNotNull {
                    it.toObject(Evento::class.java)?.copy(id = it.id)
                }
                allEvents.filter { evento ->
                    (selectedCategories.isEmpty() || evento.event_category in selectedCategories) &&
                            appliedFilters.all { (key, value) ->
                                when (key) {
                                    "event_age" -> value.isEmpty() || evento.event_age == value
                                    "event_date" -> value.isEmpty() || evento.event_date == value
                                    "event_price_min" -> value.isEmpty() || (evento.event_price_range?.toDoubleOrNull() ?: 0.0) >= value.toDoubleOrNull() ?: 0.0
                                    "event_price_max" -> value.isEmpty() || (evento.event_price_range?.toDoubleOrNull() ?: 0.0) <= value.toDoubleOrNull() ?: Double.MAX_VALUE
                                    else -> true
                                }
                            }
                }
            }
            else -> emptyList()
        }
    }

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
                    },
                    onFiltersApplied = { filters ->
                        appliedFilters = filters
                        showFilterCard = false
                        eventoViewModel.obtenerTodosLosEventos()
                    },
                    initialFilters = appliedFilters,
                    showToast = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
            filteredEvents.forEach { evento ->
                val location = parseLocation(evento.event_location ?: "")
                location?.let { latLng ->
                    val markerColor = categoryColors[evento.event_category] ?: Color.Red
                    Marker(
                        state = MarkerState(position = latLng),
                        title = evento.event_name ?: "Evento sin nombre",
                        snippet = evento.event_description ?: "Sin descripción",
                        icon = BitmapDescriptorFactory.defaultMarker(colorToHue(markerColor)),
                        onClick = {
                            selectedEvent = evento
                            true}
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
        if (showFilterCard) {
            EventFilterCard(
                onCloseClick = { showFilterCard = false },
                onApplyFilters = { filters ->
                    appliedFilters = filters
                    // No cerramos la tarjeta aquí
                    eventoViewModel.obtenerTodosLosEventos()
                },
                initialFilters = appliedFilters,
                showToast = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            )
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




