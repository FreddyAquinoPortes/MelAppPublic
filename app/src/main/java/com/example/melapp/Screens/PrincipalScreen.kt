// MapScreen.kt
package com.example.melapp.Screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.melapp.utils.handleLocationPermissionAndMoveCamera
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapScreen() {
    val context = LocalContext.current

    // Define the initial camera position (e.g., New York City coordinates)
    val initialPosition = CameraPosition(LatLng(40.7128, -74.0060), 12f, 0f, 0f)

    // Create the CameraPositionState
    val cameraPositionState = rememberCameraPositionState {
        position = initialPosition
    }

    // Llamar a la función para manejar los permisos de ubicación y mover la cámara
    handleLocationPermissionAndMoveCamera(
        context = context,
        cameraPositionState = cameraPositionState
    )

    // Mostrar el mapa
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    )
}

