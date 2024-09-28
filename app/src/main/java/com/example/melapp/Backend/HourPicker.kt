package com.example.melapp.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun HourPicker(
    selectedHour: String,
    onHourSelected: (String) -> Unit,
    label: String,
    is24HourFormat: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    // Lógica para manejar el formato de 24 y 12 horas con intervalos de 30 minutos
    val hours = if (is24HourFormat) {
        (0..23).flatMap { hour ->
            listOf("%02d:00".format(hour), "%02d:30".format(hour))
        }
    } else {
        (1..11).flatMap { hour ->
            listOf(
                "$hour:00 AM", "$hour:30 AM",
                "$hour:00 PM", "$hour:30 PM"
            )
        } + listOf("12:00 AM", "12:30 AM", "12:00 PM", "12:30 PM")
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedHour,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            readOnly = true
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            hours.forEach { hour ->
                DropdownMenuItem(
                    text = { Text(hour) },
                    onClick = {
                        onHourSelected(hour)
                        expanded = false
                    }
                )
            }
        }
    }
}
