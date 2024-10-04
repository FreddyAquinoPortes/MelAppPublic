// EventFormScreen.kt
package com.example.melapp.Screens

import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.Components.DatePicker
import com.example.melapp.Components.EventCategoryDropdown
import com.example.melapp.Components.HourPicker
import com.example.melapp.R
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EventFormScreen(navController: NavController, eventoViewModel: EventoViewModel = viewModel()) {
    // Estados del formulario
    var eventTitle by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var attendeeCount by remember { mutableStateOf("25") }
    var selectedDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var ticketUrl by remember { mutableStateOf("") }
    var eventCategory by remember { mutableStateOf("Concierto") }
    var cost by remember { mutableStateOf("0.00") }
    var selectedCurrency by remember { mutableStateOf("DOP") }
    var expanded by remember { mutableStateOf(false) }
    var latitud by remember { mutableStateOf(0.0) }
    var longitud by remember { mutableStateOf(0.0) }

    val eventoState by eventoViewModel.eventoState.collectAsState()

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: ""

    // Coordenadas de SelectLocationScreen
    val lat = navController.currentBackStackEntry?.savedStateHandle?.get<Double>("latitud")
    val lng = navController.currentBackStackEntry?.savedStateHandle?.get<Double>("longitud")

    val eventImage: String? = null
    val additionalImages: List<String> = listOf()

    LaunchedEffect(lat, lng) {
        lat?.let { latitud = it }
        lng?.let { longitud = it }
    }

    var showToast by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ReusableTopBar(
            screenTitle = "Publicar Evento",
            onBackClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ReusableTopBar(
                screenTitle = "Publicar Evento",
                onBackClick = { navController.popBackStack() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

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

                EventImagesSection(
                    eventImage = eventImage,
                    additionalImages = additionalImages,
                    onEventImageClick = {
                        // Lógica para seleccionar imagen principal
                    },
                    onAdditionalImagesClick = {
                        // Lógica para seleccionar imágenes adicionales
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_category), contentDescription = "Categoría")
                    Spacer(modifier = Modifier.width(8.dp))
                    EventCategoryDropdown(
                        selectedCategory = eventCategory,
                        onCategorySelected = { eventCategory = it }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        painterResource(R.drawable.ic_clock),
                        contentDescription = "Intervalo de hora del evento"
                    )
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
                            painterResource(R.drawable.ic_users),
                            contentDescription = "Asistentes"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                                painterResource(R.drawable.ic_money),
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

                OutlinedTextField(
                    value = ticketUrl,
                    onValueChange = { ticketUrl = it },
                    label = { Text("URL de boletos") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painterResource(R.drawable.ic_link),
                            contentDescription = "URL Boletos"
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri)
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                                // Guardar en Firestore
                                val db = FirebaseFirestore.getInstance()

                                val eventData = hashMapOf(
                                    "user_email" to (currentUser?.email ?: ""),
                                    "event_age" to "Todas las edades",
                                    "event_category" to eventCategory,
                                    "event_date" to selectedDate,
                                    "event_description" to eventDescription,
                                    "event_end_time" to endTime,
                                    "event_location" to "Lat: $latitud, Lng: $longitud",
                                    "event_name" to eventTitle,
                                    "event_number_of_attendees" to attendeeCount,
                                    "event_price_range" to "$cost $selectedCurrency",
                                    "event_rating" to "0",
                                    "event_start_time" to startTime,
                                    "event_status" to "pendiente",
                                    "event_title" to eventTitle,
                                    "event_url" to ticketUrl,
                                    "event_verification" to "pendiente"
                                )

                                db.collection("Event")
                                    .add(eventData)
                                    .addOnSuccessListener {
                                        showToast = true
                                    }
                                    .addOnFailureListener {
                                        //Toast.makeText(context, "Error al publicar el evento", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Publicar")
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
                "Formulario publicado correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }



    fun Double.format(digits: Int): String {
        return String.format(Locale.getDefault(), "%.${digits}f", this)
    }
}