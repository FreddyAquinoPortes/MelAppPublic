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
    onHourSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val hours = (0..23).map { hour -> "$hour:00" }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedHour,
            onValueChange = {},
            label = { Text("Hora del evento") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Desplegar el menú
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

