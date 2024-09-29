package com.example.melapp.ReusableComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.melapp.R

@Composable
fun NavigationBottomBar(
    onProfileClick: () -> Unit = {},
    onPostEventClick: () -> Unit = {},
    onPublishClick: () -> Unit = {}, // Añadimos un nuevo callback para el botón de publicar
    onSettingsClick: () -> Unit = {}
) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .height(125.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón de Perfil
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = "User Profile",
                        tint = Color.Gray
                    )
                }
                Text(
                    text = "Perfil",
                    color = Color.Gray
                )
            }

            // Botón de Publicar (Nuevo)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onPublishClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus), // Aquí usas tu ícono `ic_plus`
                        contentDescription = "Publicar",
                        tint = Color.Gray
                    )
                }
                Text(
                    text = "Publicar",
                    color = Color.Gray
                )
            }

            // Botón de Eventos
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onPostEventClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_earth),
                        contentDescription = "Post Event",
                        tint = Color.Gray
                    )
                }
                Text(
                    text = "Eventos",
                    color = Color.Gray
                )
            }

            // Botón de Ajustes
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Settings",
                        tint = Color.Gray
                    )
                }
                Text(
                    text = "Ajustes",
                    color = Color.Gray
                )
            }
        }
    }
}
