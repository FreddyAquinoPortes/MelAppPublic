package com.example.melapp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.melapp.Components.DatePicker
import com.example.melapp.Components.EventCategoryDropdown
import com.example.melapp.Components.HourPicker
import com.example.melapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(navController: NavController) {
    // Estado de los campos del formulario
    var eventTitle by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var attendeeCount by remember { mutableStateOf(25) }
    var selectedDate by remember { mutableStateOf("") } // Estado inicial vacío para la fecha
    var selectedHour by remember { mutableStateOf("") } // Estado para la hora seleccionada
    var location by remember { mutableStateOf("") }
    var eventCategory by remember { mutableStateOf("Concierto") }
    var ticketUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Scroll vertical para formulario largo
    ) {
        TopAppBar(
            title = { Text("Publicar Evento") },
            actions = {
                IconButton(onClick = { /* Acción de mis eventos */ }) {
                    Icon(Icons.Default.Info, contentDescription = "Mis eventos")
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Título del Evento
        OutlinedTextField(
            value = eventTitle,
            onValueChange = { eventTitle = it },
            label = { Text("Título del evento") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(painter = painterResource(R.drawable.ic_title), contentDescription = "Título") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Selector de categoría del evento
        EventCategoryDropdown(
            selectedCategory = eventCategory,
            onCategorySelected = { eventCategory = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Descripción del Evento
        OutlinedTextField(
            value = eventDescription,
            onValueChange = { eventDescription = it },
            label = { Text("Descripción del evento") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(painter = painterResource(R.drawable.ic_description), contentDescription = "Descripción") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para seleccionar la fecha del evento
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(painterResource(R.drawable.ic_calendar ), contentDescription = "Fecha")
            Spacer(modifier = Modifier.width(8.dp))
            DatePicker(selectedDate) { newDate ->
                selectedDate = newDate // Actualiza la fecha seleccionada
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para seleccionar la hora del evento
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Hora")
            Spacer(modifier = Modifier.width(8.dp))
            HourPicker(selectedHour) { newHour ->
                selectedHour = newHour // Actualiza la hora seleccionada
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cantidad de asistentes
        OutlinedTextField(
            value = attendeeCount.toString(),
            onValueChange = { newValue ->
                attendeeCount = newValue.toIntOrNull() ?: 25
            },
            label = { Text("Cantidad de asistentes") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // URL de boletos
        OutlinedTextField(
            value = ticketUrl,
            onValueChange = { ticketUrl = it },
            label = { Text("URL de boletos") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = "URL Boletos") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de Publicar y Cancelar
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { /* Acción para publicar el evento */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Publicar")
            }
            Button(
                onClick = { /* Acción para cancelar */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "Cancelar")
            }
        }
    }
}
