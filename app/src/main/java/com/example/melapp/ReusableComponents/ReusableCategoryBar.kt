package com.example.melapp.ReusableComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*


@Composable
fun CategoryBar(onCategoriesSelected: (List<String>) -> Unit) {
    var categories by remember { mutableStateOf(listOf<String>()) }
    var selectedCategories by remember { mutableStateOf(setOf("Todas")) }

    // Obtener las categorías de Firestore
    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("event_category")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedCategories = documents.mapNotNull { it.getString("category_name") }
                categories = listOf("Todas") + fetchedCategories.sorted()
            }
            .addOnFailureListener { exception ->
                // Manejar error si es necesario
            }
    }

    // Efecto para notificar cambios en las categorías seleccionadas
    LaunchedEffect(selectedCategories) {
        if ("Todas" in selectedCategories) {
            onCategoriesSelected(emptyList()) // Mostrar todos los eventos
        } else {
            onCategoriesSelected(selectedCategories.toList())
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                isSelected = category in selectedCategories,
                onSelected = { selected ->
                    selectedCategories = when {
                        category == "Todas" && selected -> setOf("Todas")
                        category == "Todas" && !selected -> emptySet()
                        selected -> selectedCategories + category - "Todas"
                        else -> selectedCategories - category
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: String,
    isSelected: Boolean,
    onSelected: (Boolean) -> Unit
) {
    Button(
        onClick = { onSelected(!isSelected) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF1A237E) else Color.White,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF1A237E) else Color.LightGray)
    ) {
        Text(category)
    }
}