// SelectLocationScreen.kt
package com.example.melapp.Screens

import androidx.compose.material.icons.filled.ArrowBack
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocationScreen(navController: NavController, onLocationSelected: (Double, Double) -> Unit) {
    // Posición inicial del mapa
    val initialPosition = LatLng(40.7128, -74.0060) // NYC
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(initialPosition, 12f, 0f, 0f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Ubicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            selectedPosition?.let {
                                onLocationSelected(it.latitude, it.longitude)
                                navController.popBackStack()
                            }
                        },
                        enabled = selectedPosition != null
                    ) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "Seleccionar")
                    }
                }
            )
        },
        content = { innerPadding ->
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedPosition = latLng
                }
            ) {
                selectedPosition?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Ubicación Seleccionada"
                    )
                }
            }
        }
    )
}
