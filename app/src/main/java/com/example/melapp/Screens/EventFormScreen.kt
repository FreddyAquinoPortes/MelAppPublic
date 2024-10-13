// EventFormScreen.kt
package com.example.melapp.Screens

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.melapp.Backend.EventImagesSection
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.Backend.toEvento
import com.example.melapp.Backend.uploadThumbnailImage
import com.example.melapp.Components.DatePicker
import com.example.melapp.Components.EventCategoryDropdown
import com.example.melapp.Components.HourPicker
import com.example.melapp.R
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Double.format(digits: Int): String {
    return String.format(Locale.getDefault(), "%.${digits}f", this)
}

fun validateForm(
    eventTitle: String,
    eventDescription: String,
    selectedDate: String,
    startTime: String,
    endTime: String,
    attendeeCount: String,
    ticketUrl: String,
    latitud: Double,
    longitud: Double,
    cost: String
): Boolean {
    return eventTitle.isNotBlank() && eventDescription.isNotBlank() && selectedDate.isNotBlank() &&
            startTime.isNotBlank() && endTime.isNotBlank() && attendeeCount.isNotBlank() && latitud != 0.0 &&
            longitud != 0.0 && cost.isNotBlank()
}

fun parseLatLng(locationString: String): Pair<Double, Double>? {
    val regex = """Lat:\s*([+-]?\d+(\.\d+)?),\s*Lng:\s*([+-]?\d+(\.\d+)?)""".toRegex()
    val matchResult = regex.find(locationString)
    return matchResult?.let {
        val lat = it.groupValues[1].toDoubleOrNull()
        val lng = it.groupValues[3].toDoubleOrNull()
        if (lat != null && lng != null) {
            Pair(lat, lng)
        } else {
            null
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EventFormScreen(
    navController: NavController,
    eventoId: String? = null,
    eventoViewModel: EventoViewModel = viewModel()
) {

    // Recolectar el estado del ViewModel
    val eventoState by eventoViewModel.eventoState.collectAsState()

    // Estados del formulario
    var eventTitle by rememberSaveable { mutableStateOf("") }
    var eventDescription by rememberSaveable { mutableStateOf("") }
    var attendeeCount by rememberSaveable { mutableStateOf("25") }
    var selectedDate by rememberSaveable { mutableStateOf("") }
    var startTime by rememberSaveable { mutableStateOf("") }
    var endTime by rememberSaveable { mutableStateOf("") }
    var ticketUrl by rememberSaveable { mutableStateOf("") }
    var eventCategory by rememberSaveable { mutableStateOf("Concierto") }
    var cost by rememberSaveable { mutableStateOf("0.00") }
    var selectedCurrency by rememberSaveable { mutableStateOf("DOP") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var latitud by rememberSaveable { mutableStateOf(0.0) }
    var longitud by rememberSaveable { mutableStateOf(0.0) }
    val currentDateTime = rememberSaveable { mutableStateOf(LocalDateTime.now()) }


    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: ""

    // Coordenadas de SelectLocationScreen
    val lat = navController.currentBackStackEntry?.savedStateHandle?.get<Double>("latitud")
    val lng = navController.currentBackStackEntry?.savedStateHandle?.get<Double>("longitud")

    val eventImage: String? = null
    val additionalImages: List<String> = listOf()

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formattedDateTime = currentDateTime.value.format(formatter)

    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var eventImageUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedAditionalImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(lat, lng) {
        lat?.let { latitud = it }
        lng?.let { longitud = it }
    }

    var showToast by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = if (eventoId != null) "Actualizar Evento" else "Publicar Evento",
                onBackClick = { navController.popBackStack() },
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { navController.navigate("profileScreen") },
                onPostEventClick = { navController.navigate("map") },
                onSettingsClick = { navController.navigate("settingsScreen") },
                onPublishClick = { navController.navigate("event_list") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = eventTitle,
                onValueChange = { eventTitle = it },
                label = { Text("Título del evento") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_title),
                        contentDescription = "Título"
                    )
                }
            )
            // Image picker launcher
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    uri?.let {
                        selectedImageUri = it
                        // Upload image to Firebase and get the URL
                        uploadThumbnailImage(uri, { url ->
                            eventImageUrl = url // Set the image URL to display the thumbnail
                        }, {
                            // Handle upload failure

                        })
                    }
                }

            // Estado para manejar las imágenes adicionales
            val additionalImages = remember { mutableStateOf(listOf<Uri>()) }

            val additionalImagesLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
                    uris?.let {
                        additionalImages.value =
                            additionalImages.value + uris // Agregar las imágenes seleccionadas a la lista
                    }
                }

            Spacer(modifier = Modifier.height(60.dp))

            // Llamar a EventImagesSection correctamente
            EventImagesSection(
                eventImage = eventImageUrl,         // URL de la imagen seleccionada para el evento
                additionalImageUri = selectedAditionalImageUri,//selectedAditionalImageUri, // Pasar la URI de la imagen seleccionada aquí
                selectedImageUri = selectedImageUri,  // URI de la miniatura seleccionada
                onEventImageClick = {
                    // Lógica para seleccionar la imagen del evento (lanzar picker)
                    launcher.launch("image/*")
                },
                onAdditionalImagesClick = {
                    // Lógica para seleccionar imágenes adicionales (lanzar picker)
                    additionalImagesLauncher.launch("image/*")
                }
            )



            Spacer(modifier = Modifier.height(8.dp))

            // Categoría del Evento
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_category), contentDescription = "Categoría")
                Spacer(modifier = Modifier.width(8.dp))
                EventCategoryDropdown(
                    selectedCategory = eventCategory,
                    onCategorySelected = { eventCategory = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción del Evento
            OutlinedTextField(
                value = eventDescription,
                onValueChange = { eventDescription = it },
                label = { Text("Descripción del evento") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_description),
                        contentDescription = "Descripción"
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha del Evento
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(painterResource(R.drawable.ic_calendar), contentDescription = "Fecha")
                Spacer(modifier = Modifier.width(8.dp))
                DatePicker(selectedDate) { newDate -> selectedDate = newDate }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hora de Inicio y Fin
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    HourPicker(
                        selectedHour = startTime,
                        onHourSelected = { startTime = it },
                        label = "Hora de inicio",
                        is24HourFormat = true
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    HourPicker(
                        selectedHour = endTime,
                        onHourSelected = { endTime = it },
                        label = "Hora de fin",
                        is24HourFormat = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ubicación del Evento
            OutlinedTextField(
                value = if (latitud != 0.0 && longitud != 0.0) {
                    "Lat: ${latitud.format(4)}, Lng: ${longitud.format(4)}"
                } else {
                    "Selecciona la ubicación"
                },
                onValueChange = {},
                label = { Text("Ubicación") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("selectLocation")
                    },
                readOnly = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = "Ubicación"
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cantidad de Asistentes
            OutlinedTextField(
                value = attendeeCount,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        attendeeCount = newValue
                    }
                },
                label = { Text("Cantidad de asistentes") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_users),
                        contentDescription = "Asistentes"
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Costo y Moneda
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Costo") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_money),
                            contentDescription = "Costo"
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(0.5f)) {
                    OutlinedTextField(
                        value = selectedCurrency,
                        onValueChange = {},
                        label = { Text("Moneda") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Expandir"
                            )
                        },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("DOP", "USD").forEach { currency ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedCurrency = currency
                                    expanded = false
                                },
                                text = { Text(currency) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // URL de Boletos
            OutlinedTextField(
                value = ticketUrl,
                onValueChange = { ticketUrl = it },
                label = { Text("URL de boletos") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_link),
                        contentDescription = "URL Boletos"
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri)
            )

            Spacer(modifier = Modifier.height(16.dp))
            val context = LocalContext.current

            // Botones de Publicar y Cancelar
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val isValid = validateForm(
                            eventTitle, eventDescription, selectedDate, startTime, endTime,
                            attendeeCount, ticketUrl, latitud, longitud, cost
                        )
                        if (isValid) {
                            // Construir el rango de precio
                            val eventPriceRange = "$cost $selectedCurrency"

                            val evento = Evento(
                                id = eventoId,
                                user_email = currentUser?.email ?: "",
                                event_age = "Todas las edades",
                                event_category = eventCategory,
                                event_date = selectedDate,
                                event_description = eventDescription,
                                event_end_time = endTime,
                                event_location = "Lat: $latitud, Lng: $longitud",
                                event_name = eventTitle,
                                event_number_of_attendees = attendeeCount,
                                event_price_range = eventPriceRange,
                                event_rating = "0",
                                event_start_time = startTime,
                                event_status = "pendiente",
                                event_title = eventTitle,
                                event_url = ticketUrl,
                                event_verification = "pendiente",
                                event_post_date = formattedDateTime,
                                event_thumbnail = eventImageUrl
                            )

                            // Nuevo, para que elija entre crear y modificar
                            if (eventoId == null) {
                                // Crear un nuevo evento
                                eventoViewModel.crearEvento(evento)
                            } else {
                                // Actualizar un evento existente
                                eventoViewModel.actualizarEvento(evento)
                            }

                            // Navegar de vuelta después de publicar o actualizar
                            navController.popBackStack()
                            showToast = true
                        } else {
                            Toast.makeText(
                                context,
                                "Por favor, completa todos los campos correctamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (eventoId == null) "Publicar" else "Actualizar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }

    if (showToast) {
        Toast.makeText(
            LocalContext.current,
            if (eventoId == null) "Evento publicado correctamente" else "Evento actualizado correctamente",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Prellenar los campos si se está actualizando un evento
    LaunchedEffect(eventoId) {
        if (eventoId != null) {
            eventoViewModel.obtenerEvento(eventoId)
        }
    }

    // Manejar los datos del evento obtenido para actualización
    val eventoData =
        if (eventoState is EventoState.SuccessSingle && (eventoState as EventoState.SuccessSingle).data.exists()) {
            (eventoState as EventoState.SuccessSingle).data.toEvento()
        } else {
            null
        }

    LaunchedEffect(eventoData) {
        eventoData?.let { evento ->
            eventTitle = evento.event_name ?: "" // Proporciona un valor por defecto
            eventDescription = evento.event_description ?: ""
            attendeeCount =
                evento.event_number_of_attendees?.toString() ?: "0" // Asigna "0" si es null
            selectedDate = evento.event_date ?: ""
            startTime = evento.event_start_time ?: ""
            endTime = evento.event_end_time ?: ""
            ticketUrl = evento.event_url ?: ""
            eventCategory = evento.event_category ?: ""
            eventImageUrl = evento.event_thumbnail ?: ""

            // Parse eventPriceRange to get cost and selectedCurrency
            val priceParts = evento.event_price_range?.split(" ")
                ?: listOf("") // Proporciona una lista vacía si es null
            if (priceParts.size >= 2) {
                cost = priceParts[0]
                selectedCurrency = priceParts[1]
            }

            // Parse location
            val locationPair = parseLatLng(evento.event_location ?: "")
            if (locationPair != null) {
                latitud = locationPair.first
                longitud = locationPair.second
            }
        }
    }
}