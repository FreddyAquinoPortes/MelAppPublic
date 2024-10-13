package com.example.melapp.Backend

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.melapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

// Función para centrar la cámara en la ubicación actual del usuario
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
                cameraPositionState.position = CameraPosition(userLocation, 15f, 0f, 0f)
            } else {
                Toast.makeText(context, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error obteniendo ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

// Función para agregar un FloatingActionButton que centra la cámara en la ubicación del usuario
@Composable
fun CenterCameraButton(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    context: Context
) {
    FloatingActionButton(
        onClick = {
            centerCameraOnUser(fusedLocationClient, cameraPositionState, context)
        },
        containerColor = Color(0xFF1A237E),
        contentColor = Color.White,
    ) {
        Icon(painterResource(R.drawable.ic_target), contentDescription = "Centrar en mi ubicación")
    }
}
