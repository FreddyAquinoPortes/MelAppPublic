package com.example.melapp.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.melapp.R
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.melapp.utils.handleLocationPermissionAndMoveCamera
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    BackHandler {
        // No hacemos nada aquí para bloquear el botón de retroceso
    }
    val context = LocalContext.current

    // Initialize the camera position
    val initialPosition = LatLng(40.7128, -74.0060) // NYC
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(initialPosition, 12f, 0f, 0f)
    }

    // Handle permissions for the location
    var myLocationEnabled by remember { mutableStateOf(false) }
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    ) {
        myLocationEnabled = true
    }

    // Move camera to current location if permissions are granted
    handleLocationPermissionAndMoveCamera(
        context = context,
        cameraPositionState = cameraPositionState
    )

    Scaffold(
        topBar = {
            Column {
                SearchTopBar()
                CategoryBar() // Agregamos la barra de categorías aquí
            }
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { /* Lógica para ir al perfil */ },
                onPostEventClick = { navController.navigate("event_form") },
                onSettingsClick = { navController.navigate("settingsScreen")}
            )
        }
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = myLocationEnabled)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SearchTopBar() {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp)),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Search icon
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray,
                    modifier = Modifier.height(56.dp)
                )

                // Search text field
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                // Mic icon
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic), // Replace with actual drawable
                    contentDescription = "Mic",
                    tint = Color.Gray,
                    modifier = Modifier.height(56.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        scrollBehavior = null // if no scroll behavior is needed
    )
}

@Composable
fun CategoryBar() {
    val categories = listOf("Conciertos", "Deportes", "Culturales", "Infantiles", "Arte", "Cine", "Religiosos")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(categories.size) { index ->
            CategoryItem(categories[index])
        }
    }
}

@Composable
fun CategoryItem(category: String) {
    val selected = remember { mutableStateOf(false) }

    Button(
        onClick = { selected.value = !selected.value },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected.value) Color(0xFF1A237E) else Color.White, // containerColor en vez de backgroundColor
            contentColor = if (selected.value) Color.White else Color.Black
        ),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (selected.value) Color(0xFF1A237E) else Color.LightGray)
    ) {
        Text(category)
    }
}

// Reusable NavigationBottomBar Component
@Composable
fun NavigationBottomBar(
    onProfileClick: () -> Unit = {},
    onPostEventClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0)) // Aquí estableces el color de fondo con Modifier.background
            .height(100.dp) // Ajusta la altura si es necesario
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Ajusta el padding horizontal si es necesario
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón de Perfil
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = "User Profile",
                        tint = Color.Gray
                    )
                }
                Text(
                    text = "Perfil",
                    color = Color.Gray
                )
            }

            // Botón de Eventos
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onPostEventClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_earth),
                        contentDescription = "Post Event",
                        tint = Color.Gray
                    )
                }
                Text(
                    text = "Eventos",
                    color = Color.Gray
                )
            }

            // Botón de Ajustes
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Settings",
                        tint = Color.Gray
                    )
                }
                Text(
                    text = "Ajustes",
                    color = Color.Gray
                )
            }
        }
    }
}
