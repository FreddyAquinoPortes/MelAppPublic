package com.example.melapp.ReusableComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.melapp.Backend.Evento
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    eventoViewModel: EventoViewModel = viewModel(),
    onEventSelected: (Evento) -> Unit,
    onFiltersApplied: (Map<String, String>) -> Unit,
    initialFilters: Map<String, String> = emptyMap(),
    showToast: (String) -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var showFilterCard by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )

                    TextField(
                        value = searchText,
                        onValueChange = { newText ->
                            searchText = newText
                            isSearching = newText.text.isNotEmpty()
                            coroutineScope.launch {
                                searchResults = eventoViewModel.searchEvents(newText.text)
                            }
                        },
                        placeholder = { Text("Buscar...") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = "Filter",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showFilterCard = true }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            scrollBehavior = null
        )

        if (isSearching) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .background(Color.White)
            ) {
                items(searchResults) { evento ->
                    Text(
                        text = evento.event_name ?: "Evento sin nombre",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEventSelected(evento)
                                isSearching = false
                                searchText = TextFieldValue("")
                            }
                            .padding(16.dp)
                    )
                }
            }
        }

        if (showFilterCard) {
            EventFilterCard(
                onCloseClick = { showFilterCard = false },
                onApplyFilters = { filters ->
                    onFiltersApplied(filters)
                    showFilterCard = false
                },
                initialFilters = initialFilters,
                showToast = showToast
            )
        }
    }
}