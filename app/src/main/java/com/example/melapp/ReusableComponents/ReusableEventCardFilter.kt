package com.example.melapp.ReusableComponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.melapp.R
import com.example.melapp.Components.DatePicker
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFilterCard(
    onCloseClick: () -> Unit,
    onApplyFilters: (Map<String, String>) -> Unit,
    initialFilters: Map<String, String> = emptyMap()
) {
    var ageFilter by remember { mutableStateOf(initialFilters["event_age"] ?: "") }
    var dateFilter by remember { mutableStateOf(initialFilters["event_date"] ?: "") }
    var minPriceFilter by remember { mutableStateOf(initialFilters["event_price_min"] ?: "") }
    var maxPriceFilter by remember { mutableStateOf(initialFilters["event_price_max"] ?: "") }
    var ageOptions by remember { mutableStateOf(listOf<String>()) }
    var isAgeDropdownExpanded by remember { mutableStateOf(false) }

    // Fetch age options from Firebase
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
                onExpandedChange = { isAgeDropdownExpanded = !isAgeDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = ageFilter,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Edad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isAgeDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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
                    onValueChange = { minPriceFilter = it },
                    label = { Text("Precio mínimo") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = maxPriceFilter,
                    onValueChange = { maxPriceFilter = it },
                    label = { Text("Precio máximo") },
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
                    }
                ) {
                    Text("Limpiar")
                }
                Button(
                    onClick = {
                        val filters = mapOf(
                            "event_age" to ageFilter,
                            "event_date" to dateFilter,
                            "event_price_min" to minPriceFilter,
                            "event_price_max" to maxPriceFilter
                        ).filter { it.value.isNotEmpty() }
                        onApplyFilters(filters)
                        onCloseClick()
                    }
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}