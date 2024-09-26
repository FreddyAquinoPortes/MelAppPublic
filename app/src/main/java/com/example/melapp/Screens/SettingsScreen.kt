package com.example.melapp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.melapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Ajustes", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF1A237E) // Morado oscuro
                )
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { /* Lógica para ir al perfil */ },
                onPostEventClick = { /* Lógica para crear un evento */ },
                onSettingsClick = { navController.navigate("settingsScreen")}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Sample settings layout
            SettingsOptionItem("Idioma", listOf("Español", "Ingles"))
            SettingsOptionItem("Cuenta", listOf("Seguridad", "Accesibilidad"))
            SettingsOptionItem("Condiciones y Políticas", listOf("Notificaciones", "Activar"))
            SettingsOptionItem("Acerca de", listOf("Salir"))
        }
    }
}

@Composable
fun SettingsOptionItem(title: String, subOptions: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, color = Color(0xFF1A237E), fontSize = 20.sp) // Custom font size
            Icon(
                painter = painterResource(id = R.drawable.ic_circle_down), // Replace with actual drawable
                contentDescription = "Expand",
                tint = Color(0xFF1A237E)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        subOptions.forEach { option ->
            ClickableText(text = AnnotatedString(option), onClick = { /* Do action */ })
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
