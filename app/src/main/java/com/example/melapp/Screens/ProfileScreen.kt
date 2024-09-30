package com.example.melapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.melapp.R
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.example.melapp.ReusableComponents.ReusableTopBar

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = "Perfil",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { /* Lógica para ir a la pantalla de perfil */ },
                onPostEventClick = { navController.navigate("map") },
                onPublishClick = { navController.navigate("event_form") },
                onSettingsClick = { navController.navigate("settingsScreen") }
            )
        }
    ) { paddingValues ->
        // Cambié a LazyColumn para incluir encabezados y contenido
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                // Foto de perfil y botón de editar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_user), // Reemplaza con el recurso de la imagen del perfil
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Botones en fila (Editar Perfil y Ver Eventos Guardados)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Button(onClick = { /* Lógica para editar el perfil */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_pencil),
                                    contentDescription = "Icono de editar"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Editar Perfil")
                            }

                            Button(onClick = { /* Lógica para ver eventos guardados */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_bookmark),
                                    contentDescription = "Icono de eventos guardados"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Ver Eventos Guardados")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Título de la sección de eventos publicados
                Text(
                    text = "Eventos Publicados",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.titleLarge // Usamos el nuevo estilo titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Cuadrícula de eventos publicados
            items(10) { index -> // Simula 10 eventos, puedes reemplazarlo con tu lista de eventos
                EventCard(
                    eventName = "Evento $index",
                    location = "@Ubicación $index",
                    imageRes = R.drawable.ic_category // Reemplaza con la imagen de tu evento
                )
            }
        }
    }
}

@Composable
fun EventCard(eventName: String, location: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp) // Usamos CardDefaults para la elevación
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = eventName,
                style = MaterialTheme.typography.bodyLarge, // Nuevo estilo de texto
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium, // Nuevo estilo de texto
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
