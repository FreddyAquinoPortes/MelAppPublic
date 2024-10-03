package com.example.melapp.Screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.melapp.Components.DatePicker
import com.example.melapp.Components.EventCategoryDropdown
import com.example.melapp.Components.HourPicker
import com.example.melapp.R
import com.example.melapp.ReusableComponents.ReusableTopBar
import coil.compose.rememberAsyncImagePainter


@Composable
fun EventFormScreen(navController: NavController) {
    // Form states
    var eventTitle by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var attendeeCount by remember { mutableStateOf(25) }
    var selectedDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var ticketUrl by remember { mutableStateOf("") }
    var eventCategory by remember { mutableStateOf("Concierto") }
    var cost by remember { mutableStateOf("0.00") }
    var selectedCurrency by remember { mutableStateOf("DOP") }
    var expanded by remember { mutableStateOf(false) }
    var eventImage by remember { mutableStateOf<Uri?>(null) }
    var additionalImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ReusableTopBar(screenTitle = "Publicar Evento", onBackClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Event Title
            OutlinedTextField(
                value = eventTitle,
                onValueChange = { eventTitle = it },
                label = { Text("Título del evento") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(painterResource(R.drawable.ic_title), contentDescription = "Título") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Main Event Image
            Text("Agregar Imagen Principal del Evento", style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable {
                        // Implement logic to select image from gallery or camera
                    },
                contentAlignment = Alignment.Center
            ) {
                if (eventImage != null) {
                    Image(
                        painter = rememberAsyncImagePainter(eventImage),
                        contentDescription = "Imagen del evento",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_images),
                        contentDescription = "Agregar imagen",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Additional Images
            Text("Agregar Imágenes Adicionales del Evento", style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable {
                        // Implement logic to select multiple images
                    },
                contentAlignment = Alignment.Center
            ) {
                if (additionalImages.isNotEmpty()) {
                    // Show a grid or list of selected images
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_images),
                        contentDescription = "Agregar imágenes",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Event Category
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.ic_category), contentDescription = "Categoría")
                Spacer(modifier = Modifier.width(8.dp))
                EventCategoryDropdown(
                    selectedCategory = eventCategory,
                    onCategorySelected = { eventCategory = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Event Description
            OutlinedTextField(
                value = eventDescription,
                onValueChange = { eventDescription = it },
                label = { Text("Descripción del evento") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(painter = painterResource(R.drawable.ic_description), contentDescription = "Descripción") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Event Date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(painter = painterResource(R.drawable.ic_calendar), contentDescription = "Fecha")
                Spacer(modifier = Modifier.width(8.dp))
                DatePicker(selectedDate) { newDate -> selectedDate = newDate }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Start and End Time
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(painter = painterResource(R.drawable.ic_clock), contentDescription = "Intervalo de hora del evento")
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

            // Attendee Count
            OutlinedTextField(
                value = attendeeCount.toString(),
                onValueChange = { newValue -> attendeeCount = newValue.toIntOrNull() ?: 25 },
                label = { Text("Cantidad de asistentes") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(painter = painterResource(R.drawable.ic_users), contentDescription = "Asistentes") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cost and Currency
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Costo") },
                    leadingIcon = { Icon(painter = painterResource(R.drawable.ic_money), contentDescription = "Costo") },
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

            // Ticket URL
            OutlinedTextField(
                value = ticketUrl,
                onValueChange = { ticketUrl = it },
                label = { Text("URL de boletos") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(painter = painterResource(R.drawable.ic_link), contentDescription = "URL Boletos") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Publish and Cancel Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Action to publish the event */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Publicar")
                }
                Button(
                    onClick = { /* Action to cancel */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = "Cancelar")
                }
            }
        }
    }
}
