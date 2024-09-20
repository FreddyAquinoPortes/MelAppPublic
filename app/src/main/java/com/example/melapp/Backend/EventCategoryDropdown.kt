package com.example.melapp.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Concierto", "Conferencia", "Reunión", "Taller", "Feria")

    // Utilizamos ExposedDropdownMenuBox para un control más sencillo del Dropdown
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded // Cambia el estado del menú desplegable
        }
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true, // Evita que el usuario escriba manualmente
            label = { Text("Categoría del evento") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(), // Permite que el menú aparezca correctamente
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        // Menú desplegable
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category) // Actualiza la categoría seleccionada
                        expanded = false // Cierra el menú después de seleccionar
                    }
                )
            }
        }
    }
}
