package com.example.melapp.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.melapp.Backend.EventoState
import com.example.melapp.Backend.EventoViewModel
import com.example.melapp.R
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.google.firebase.firestore.DocumentSnapshot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetailsScreen(navController: NavController, eventoId: String, eventoViewModel: EventoViewModel = viewModel()) {
    val eventoState by eventoViewModel.eventoState.collectAsState()

    LaunchedEffect(eventoId) {
        eventoViewModel.obtenerEvento(eventoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { navController.navigate("profileScreen") },
                onPostEventClick = { navController.navigate("map") },
                onSettingsClick = { navController.navigate("settingsScreen") },
                onPublishClick = { navController.navigate("event_form") }
            )
        },
        content = { innerPadding ->
            when (eventoState) {
                is EventoState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is EventoState.SuccessSingle -> {
                    val documento = (eventoState as EventoState.SuccessSingle).data
                    DisplayEventDetails(documento)
                }
                is EventoState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = (eventoState as EventoState.Error).message, color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {
                    // Estado Idle o no manejado
                }
            }
        }
    )
}

@Composable
fun DisplayEventDetails(documento: DocumentSnapshot) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        documento.data?.forEach { (key, value) ->
            Text(text = "$key: $value", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}