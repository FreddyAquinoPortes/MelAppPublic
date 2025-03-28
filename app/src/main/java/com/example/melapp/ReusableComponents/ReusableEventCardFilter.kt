package com.example.melapp.ReusableComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.melapp.Components.DatePicker
import com.example.melapp.R
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFilterCard(
    onCloseClick: () -> Unit,
    onApplyFilters: (Map<String, String>) -> Unit,
    initialFilters: Map<String, String>,
    showToast: (String) -> Unit
) {
    var ageFilter by remember { mutableStateOf(initialFilters["event_age"] ?: "") }
    var dateFilter by remember { mutableStateOf(initialFilters["event_date"] ?: "") }
    var minPriceFilter by remember { mutableStateOf(initialFilters["event_price_min"] ?: "") }
    var maxPriceFilter by remember { mutableStateOf(initialFilters["event_price_max"] ?: "") }
    var ageOptions by remember { mutableStateOf(listOf<String>()) }
    var isAgeDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Event_Age").get()
            .addOnSuccessListener { documents ->
                ageOptions = documents.mapNotNull { it.getString("event_age_name") }
            }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filtrar Eventos", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onCloseClick) {
                    Icon(painter = painterResource(id = R.drawable.ic_closedcross), contentDescription = "Cerrar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = isAgeDropdownExpanded,
                onExpandedChange = { isAgeDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = ageFilter,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Edad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isAgeDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isAgeDropdownExpanded,
                    onDismissRequest = { isAgeDropdownExpanded = false }
                ) {
                    ageOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                ageFilter = option
                                isAgeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            DatePicker(
                selectedDate = dateFilter,
                onDateSelected = { dateFilter = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Rango de Precio", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = minPriceFilter,
                    onValueChange = { minPriceFilter = it.filter { it.isDigit() } },
                    label = { Text("Precio mínimo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = maxPriceFilter,
                    onValueChange = { maxPriceFilter = it.filter { it.isDigit() } },
                    label = { Text("Precio máximo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        ageFilter = ""
                        dateFilter = ""
                        minPriceFilter = ""
                        maxPriceFilter = ""
                        showToast("Filtros limpiados")
                    }
                ) {
                    Text("Limpiar")
                }
                Button(
                    onClick = {
                        val filters = mapOf(
                            "event_age" to ageFilter,
                            "event_date" to dateFilter,
                            "event_price_min" to formatPriceForBackend(minPriceFilter),
                            "event_price_max" to formatPriceForBackend(maxPriceFilter)
                        ).filter { it.value.isNotEmpty() }

                        onApplyFilters(filters)
                        showToast("Filtros aplicados correctamente")
                    }
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}

// Las funciones auxiliares permanecen iguales
fun extractNumericPrice(price: String?): Int {
    return price?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
}

// Función actualizada para comparar precios
fun isPriceInRange(dbPrice: String?, minPrice: String, maxPrice: String): Boolean {
    val dbNumericPrice = extractNumericPrice(dbPrice)
    val minNumericPrice = extractNumericPrice(minPrice)
    val maxNumericPrice = if (maxPrice.isEmpty()) Int.MAX_VALUE else extractNumericPrice(maxPrice)

    return dbNumericPrice in minNumericPrice..maxNumericPrice
}

// Función para formatear el precio para el backend
fun formatPriceForBackend(price: String): String {
    val numericPrice = price.replace(Regex("[^0-9]"), "")
    return if (numericPrice.isNotEmpty()) {
        if (price.uppercase().endsWith("DOP")) price else "$numericPrice DOP"
    } else ""
}

