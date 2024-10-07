package com.example.melapp.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import com.example.melapp.R

@Composable
fun HourPicker(
    selectedHour: String,
    onHourSelected: (String) -> Unit,
    label: String,
    is24HourFormat: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

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

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedHour,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { if (it.isFocused) expanded = true },
            readOnly = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = "Clock icon"
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Toggle dropdown",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
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