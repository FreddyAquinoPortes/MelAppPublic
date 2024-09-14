package com.example.melapp.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.melapp.Backend.PhoneVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen() {
    // State for form fields
    var eventTitle by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var attendeeCount by remember { mutableStateOf(25) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Palacio de Los Deportes") }
    var eventCategory by remember { mutableStateOf("Concierto") }
    var ticketUrl by remember { mutableStateOf("") }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Handle image selection here
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Allows vertical scrolling for long forms
    ) {
        TopAppBar(
            title = { Text("Publicar Evento") },
            actions = {
                IconButton(onClick = { /* Navigate to my events */ }) {
                    Icon(Icons.Default.Info, contentDescription = "Mis eventos")
                }
            }
        )

        // Event Title Input
        OutlinedTextField(
            value = eventTitle,
            onValueChange = { eventTitle = it },
            label = { Text("Titulo del evento") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Event Title") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Event Category Dropdown
        EventCategoryDropdown(
            selectedCategory = eventCategory,
            onCategorySelected = { eventCategory = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Event Description Input
        OutlinedTextField(
            value = eventDescription,
            onValueChange = { eventDescription = it },
            label = { Text("Descripción del evento") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.List, contentDescription = "Event Description") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Photo Picker Button
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Pick Photo")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Agregar foto")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Location Picker
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Location")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Date and Time Pickers
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.DateRange, contentDescription = "Fecha")
            Spacer(modifier = Modifier.width(8.dp))
            DatePicker(selectedDate) { newDate ->
                selectedDate = newDate
            }
            Spacer(modifier = Modifier.width(8.dp))
            TimePicker(selectedTime) { newTime ->
                selectedTime = newTime
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Attendee Count Input
        OutlinedTextField(
            value = attendeeCount.toString(),
            onValueChange = { attendeeCount = it.toIntOrNull() ?: 25 },
            label = { Text("Cantidad de asistentes") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ticket URL Input
        OutlinedTextField(
            value = ticketUrl,
            onValueChange = { ticketUrl = it },
            label = { Text("URL de boletos") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = "Ticket URL") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Publish and Cancel Buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { /* Handle publish event */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
            ) {
                Text(text = "Publicar", color = Color.White)
            }
            Button(
                onClick = { /* Handle cancel */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(text = "Cancelar", color = Color.White)
            }
        }
    }
}

// DatePicker Component
@Composable
fun DatePicker(selectedDate: String, onDateSelected: (String) -> Unit) {
    // Date Picker implementation
}

// TimePicker Component
@Composable
fun TimePicker(selectedTime: String, onTimeSelected: (String) -> Unit) {
    // Time Picker implementation
}

// Dropdown for Event Category
@Composable
fun EventCategoryDropdown(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Concierto", "Deportes", "Teatro", "Exposición")

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            label = { Text("Categoría del evento") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow", Modifier.clickable { expanded = true })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category) },  // Newer versions of Compose require a 'text' parameter
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventFormScreenPreview() {
    MaterialTheme {
        EventFormScreen()
    }
}