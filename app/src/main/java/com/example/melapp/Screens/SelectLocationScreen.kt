package com.example.melapp.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.melapp.Backend.CenterCameraButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
@SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
fun SelectLocationScreen(navController: NavController, onLocationSelected: (Double, Double) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Posición inicial de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(40.7128, -74.0060), 12f, 0f, 0f)
    }

    var myLocationEnabled by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }  // Added this line back

    if (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        myLocationEnabled = true
        centerCameraOnUser(fusedLocationClient, cameraPositionState, context)
    } else {
        // Solicitar permisos si no están otorgados
        // Esto se debería manejar adecuadamente dependiendo de tu lógica de manejo de permisos
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
                                onLocationSelected(it.latitude, it.longitude)  // onLocationSelected parameter now passed
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

        floatingActionButton = {
            CenterCameraButton(
                fusedLocationClient = fusedLocationClient,
                cameraPositionState = cameraPositionState,
                context = context
            )
        },
        floatingActionButtonPosition = FabPosition.Start
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = myLocationEnabled),
            onMapClick = { latLng ->  // Added the onMapClick functionality
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
}
