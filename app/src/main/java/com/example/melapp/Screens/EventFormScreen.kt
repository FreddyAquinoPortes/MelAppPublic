package com.example.melapp.Screens

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(navController: NavController) {
    // Estados del formulario
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

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())){
        ReusableTopBar(screenTitle = "Publicar Evento", onBackClick = {  val result = navController.popBackStack()
            if (!result) {
                navController.navigate("map") // Navega a la pantalla deseada si no puede hacer pop
            } })
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {



        Spacer(modifier = Modifier.height(100.dp))

        // Título del Evento
        OutlinedTextField(
            value = eventTitle,
            onValueChange = { eventTitle = it },
            label = { Text("Título del evento") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(painter = painterResource(R.drawable.ic_title), contentDescription = "Título") }
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
            leadingIcon = { Icon(painterResource(R.drawable.ic_description), contentDescription = "Descripción") }
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
            Icon(painterResource(R.drawable.ic_clock), contentDescription = "Intervalo de hora del evento")
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

        // Cantidad de asistentes
        OutlinedTextField(
            value = attendeeCount.toString(),
            onValueChange = { newValue -> attendeeCount = newValue.toIntOrNull() ?: 25 },
            label = { Text("Cantidad de asistentes") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(painterResource(R.drawable.ic_users), contentDescription = "Asistentes") }
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
                leadingIcon = { Icon(painterResource(R.drawable.ic_money), contentDescription = "Costo") },
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
            leadingIcon = { Icon(painter = painterResource(R.drawable.ic_link), contentDescription = "URL Boletos") }
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

