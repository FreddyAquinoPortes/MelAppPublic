// MapScreen.kt
package com.example.melapp.Screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.melapp.utils.handleLocationPermissionAndMoveCamera
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties

@Composable
fun MapScreen() {
    val context = LocalContext.current

    // Define the initial camera position (e.g., New York City coordinates)
    val initialPosition = CameraPosition(LatLng(40.7128, -74.0060), 12f, 0f, 0f)

    // Create the CameraPositionState
    val cameraPositionState = rememberCameraPositionState {
        position = initialPosition
    }

    // Variable to control if the My Location layer is enabled
    var myLocationEnabled by remember { mutableStateOf(false) }

    // Check if the app has location permissions
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    ) {
        myLocationEnabled = true
    }

    // Call the function to handle location permissions and move the camera
    handleLocationPermissionAndMoveCamera(
        context = context,
        cameraPositionState = cameraPositionState
    )

    // Display the map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = myLocationEnabled)
    )
}

