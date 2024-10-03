package com.example.melapp.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.melapp.R
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.google.maps.android.compose.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoState
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, eventoViewModel: EventoViewModel = viewModel()) {

    val eventoState by eventoViewModel.eventoState.collectAsState()

    LaunchedEffect(Unit) {
        eventoViewModel.obtenerTodosLosEventos()
    }

    BackHandler {
        // Bloquear el botón de retroceso
    }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Posición inicial de la cámara
    val initialPosition = LatLng(40.7128, -74.0060) // NYC
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(initialPosition, 12f, 0f, 0f)
    }

    // Manejar permisos de ubicación
    var myLocationEnabled by remember { mutableStateOf(false) }

    // Solicitar permisos fuera de LaunchedEffect
    if (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        myLocationEnabled = true
        // Lógica para mover la cámara a la ubicación actual
        centerCameraOnUser(fusedLocationClient, cameraPositionState, context)
    }

    Scaffold(
        topBar = {
            Column {
                SearchTopBar()
                CategoryBar() // Barra de categorías
            }
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { navController.navigate("profileScreen") },
                onPostEventClick = { navController.navigate("map") },
                onSettingsClick = { navController.navigate("settingsScreen") },
                onPublishClick = { navController.navigate("event_form") }
            )
        },
        floatingActionButton = {
            // Usamos un Box para manejar la posición del botón
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        // Llama a la función para centrar en la ubicación del usuario
                        centerCameraOnUser(fusedLocationClient, cameraPositionState, context)
                    },
                    containerColor = Color(0xFF1A237E),
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart) // Alinear el botón en la esquina inferior izquierda
                ) {
                    Icon(painterResource(R.drawable.ic_target), contentDescription = "Centrar en mi ubicación")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center // Omitir si ya no es necesario cambiar la posición
    ) {
        // Mapa con marcadores de eventos
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = myLocationEnabled)
        ) {
            // Agregar marcadores para cada evento
            when (eventoState) {
                is EventoState.Success -> {
                    val data = (eventoState as EventoState.Success).data
                    if (data is List<*>) {
                        data.filterIsInstance<Evento>().forEach { evento ->
                            Marker(
                                state = MarkerState(position = LatLng(evento.latitud, evento.longitud)),
                                title = evento.nombre,
                                snippet = evento.descripcion,
                                onClick = {
                                    // Manejar el clic en el marcador
                                    // Por ejemplo, navegar a una pantalla de detalles del evento
                                    navController.navigate("eventDetails/${evento.id}")
                                    true
                                }
                            )
                        }
                    }
                }
                is EventoState.Error -> {
                    // Opcional: Mostrar un mensaje de error en el mapa
                    // Puedes usar un Overlay o algún otro método para mostrar mensajes
                }
                else -> {
                    // Otros estados (Idle, Loading)
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SearchTopBar() {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically // Asegurar que los íconos estén alineados
            ) {
                // Search icon
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp) // Ajustar el tamaño del ícono
                )

                // Search text field
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                // Mic icon
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic), // Reemplaza con el drawable correcto
                    contentDescription = "Mic",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp) // Ajustar el tamaño del ícono
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
    val categories = remember { mutableStateListOf<String>() } // Estado mutable para almacenar los campos de cada documento

    // Obtener los campos de la colección event_category de Firestore
    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("event_category")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val data = document.data // Obtenemos todos los campos del documento como un mapa
                    // Iterar sobre cada campo del documento y agregar el valor a la lista de categorías
                    data.forEach { (key, value) ->
                        categories.add("$key: $value")
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejar error si es necesario
            }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(categories.size) { index ->
            CategoryItem(categories[index]) // Mostrar el valor del campo
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
