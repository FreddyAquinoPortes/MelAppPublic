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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.Components.DatePicker
import com.example.melapp.Components.EventCategoryDropdown
import com.example.melapp.Components.HourPicker
import com.example.melapp.R
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(navController: NavController, eventoViewModel: EventoViewModel = viewModel()) {
    // Estados del formulario
    var eventTitle by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var attendeeCount by remember { mutableStateOf("25") } // Cambio a String para facilitar la conversión
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

    // Obtener el UID del usuario autenticado
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: ""

    // Obtener las coordenadas seleccionadas de SelectLocationScreen
    val lat = navController.currentBackStackEntry?.savedStateHandle?.get<Double>("latitud")
    val lng = navController.currentBackStackEntry?.savedStateHandle?.get<Double>("longitud")

    val eventImage: String? = null // Aquí puedes cargar la imagen de la galería o la cámara
    val additionalImages: List<String> = listOf() // Lista de imágenes adicionales

    LaunchedEffect(lat, lng) {
        lat?.let { latitud = it }
        lng?.let { longitud = it }
    }

    // Variable de estado para controlar el Toast
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
            Spacer(modifier = Modifier.height(16.dp))

            // Título del Evento
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
                        painterResource(R.drawable.ic_description),
                        contentDescription = "Descripción"
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha del evento
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

            // Hora de inicio y fin
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    painterResource(R.drawable.ic_clock),
                    contentDescription = "Intervalo de hora del evento"
                )
                // Campo de hora de inicio
                Box(modifier = Modifier.weight(1f)) {
                    HourPicker(
                        selectedHour = startTime,
                        onHourSelected = { startTime = it },
                        label = "Hora de inicio",
                        is24HourFormat = true
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Campo de hora de fin
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

            // Ubicación del evento
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
                        navController.navigate("selectLocation") // Navegar a SelectLocationScreen
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

            // Cantidad de asistentes
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

            // Campo de costo y selección de moneda en la misma fila
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

                // Dropdown para seleccionar moneda
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

            // URL de boletos
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

            // Botones de Publicar y Cancelar
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        // Validar los campos antes de publicar
                        if (validateForm(
                                eventTitle,
                                eventDescription,
                                selectedDate,
                                startTime,
                                endTime,
                                attendeeCount,
                                cost,
                                selectedCurrency,
                                ticketUrl,
                                latitud,
                                longitud
                            )
                        ) {
                            // Crear el objeto Evento
                            val evento = Evento(
                                nombre = eventTitle,
                                descripcion = eventDescription,
                                fecha = convertDateToTimestamp(selectedDate),
                                latitud = latitud,
                                longitud = longitud,
                                creadorId = userId
                            )

                            // Llamar a la función del ViewModel para crear el evento
                            eventoViewModel.crearEvento(evento)
                        } else {
                            // Mostrar un mensaje de error si la validación falla
                            showToast = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Publicar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancelar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Manejar los estados del ViewModel
            when (eventoState) {
                is EventoState.Loading -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is EventoState.Success -> {
                    val id = (eventoState as EventoState.Success).data as? String
                    if (id != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Evento creado con ID: $id",
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigate("map") },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Volver al Mapa")
                        }
                    }
                }

                is EventoState.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (eventoState as EventoState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    // Otros estados
                }
            }

            // Mostrar el Toast si es necesario
            if (showToast) {
                val context = LocalContext.current
                LaunchedEffect(showToast) {
                    Toast.makeText(
                        context,
                        "Por favor, completa todos los campos correctamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    showToast = false
                }
            }
        }
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

// Función para validar los campos del formulario
private fun validateForm(
    title: String,
    description: String,
    date: String,
    startTime: String,
    endTime: String,
    attendeeCount: String,
    cost: String,
    currency: String,
    ticketUrl: String,
    lat: Double,
    lng: Double
): Boolean {
    return title.isNotBlank() &&
            description.isNotBlank() &&
            date.isNotBlank() &&
            startTime.isNotBlank() &&
            endTime.isNotBlank() &&
            attendeeCount.isNotBlank() &&
            cost.isNotBlank() &&
            currency.isNotBlank() &&
            ticketUrl.isNotBlank() &&
            lat != 0.0 && lng != 0.0
}

// Función para convertir la fecha en formato String a timestamp Long
private fun convertDateToTimestamp(date: String): Long {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val parsedDate = sdf.parse(date)
        parsedDate?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}
