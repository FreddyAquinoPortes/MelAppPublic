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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.items

data class Category(
    val name: String,
    val colorCode: String
)
@Composable
fun CategoryBar(onCategoriesSelected: (List<String>) -> Unit) {
    var categories by remember { mutableStateOf(listOf<Category>()) }
    var selectedCategories by remember { mutableStateOf(setOf("Todas")) }

    // Obtener las categorías y colores de Firestore
    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("event_category")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedCategories = documents.map { doc ->
                    Category(
                        name = doc.getString("category_name") ?: "",
                        colorCode = doc.getString("category_color_code") ?: "0xFF1A237E"
                    )
                }.sortedBy { it.name }
                categories = listOf(Category("Todas", "0xFF1A237E")) + fetchedCategories
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
                isSelected = category.name in selectedCategories,
                onSelected = { selected ->
                    selectedCategories = when {
                        category.name == "Todas" && selected -> setOf("Todas")
                        category.name == "Todas" && !selected -> emptySet()
                        selected -> selectedCategories + category.name - "Todas"
                        else -> selectedCategories - category.name
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onSelected: (Boolean) -> Unit
) {
    val backgroundColor = if (isSelected) {
        parseColor(category.colorCode)
    } else {
        Color.White
    }

    Button(
        onClick = { onSelected(!isSelected) },
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSelected) backgroundColor else Color.LightGray)
    ) {
        Text(category.name)
    }
}

fun parseColor(colorString: String): Color {
    return try {
        val colorLong = colorString.removePrefix("0x").toLong(16)
        Color(colorLong)
    } catch (e: NumberFormatException) {
        // Si el color no es válido, devolvemos un color por defecto
        Color(0xFF1A237E) // El color azul oscuro original
    }
}