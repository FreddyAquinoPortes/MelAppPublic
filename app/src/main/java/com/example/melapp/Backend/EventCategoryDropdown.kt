package com.example.melapp.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var categories by remember { mutableStateOf(listOf<String>()) }

    // Cargar las categorías desde Firestore
    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val categoryList = mutableListOf<String>()

        try {
            val snapshot = firestore.collection("event_category").get().await()
            for (document in snapshot.documents) {
                val categoryName = document.getString("category_name")
                if (categoryName != null) {
                    categoryList.add(categoryName)
                }
            }
            // Ordenar las categorías alfabéticamente antes de actualizar el estado
            categories = categoryList.sorted()
        } catch (e: Exception) {
            // Manejar el error si es necesario
            e.printStackTrace()
        }
    }

    // ExposedDropdownMenuBox para mostrar el menú desplegable
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría del evento") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        // Menú desplegable con las categorías cargadas desde Firestore
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}
