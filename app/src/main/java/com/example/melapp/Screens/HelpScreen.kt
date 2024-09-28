package com.example.melapp.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.example.melapp.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HelpScreen(navController: NavController) {
    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = "Ayuda",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { /* Navigate to Profile */ },
                onPostEventClick = { /* Navigate to Post Event */ },
                onSettingsClick = { /* Navigate to Settings */ }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Avoid overlap with top bar and bottom bar
                    .padding(16.dp) // Additional content padding
            ) {
                item {
                    Text(text = "¿En qué te podemos ayudar?", fontSize = 22.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Opciones de ayuda
                    HelpOptionRow(
                        optionText = "Reportar un problema",
                        onClick = { /* Navigate to Report a Problem screen */ }
                    )

                    HelpOptionRow(
                        optionText = "Estado de la cuenta",
                        onClick = { /* Navigate to Account Status screen */ }
                    )

                    HelpOptionRow(
                        optionText = "Servicio de ayuda",
                        onClick = { /* Navigate to Help Service screen */ }
                    )

                    HelpOptionRow(
                        optionText = "Ayuda sobre privacidad y seguridad",
                        onClick = { /* Navigate to Privacy and Security screen */ }
                    )

                    HelpOptionRow(
                        optionText = "Solicitudes de ayuda",
                        onClick = { /* Navigate to Help Requests screen */ }
                    )

                    Spacer(modifier = Modifier.height(24.dp)) // Espacio entre secciones
                }

                // Preguntas Frecuentes
                item {
                    Text(text = "Preguntas Frecuentes", fontSize = 20.sp, color = Color.Gray)
                }

                // Lista de FAQs
                items(faqQuestions.size) { index ->
                    FAQItem(
                        questionText = faqQuestions[index],
                        onClick = { /* Acción para la pregunta seleccionada */ }
                    )
                }

                // "Ver más" botón clickeable
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ver más",
                        fontSize = 18.sp,
                        color = Color(0xFF1A237E), // Morado oscuro
                        modifier = Modifier.clickable { /* Acción para cargar más FAQs */ }
                    )
                }
            }
        }
    )
}

@Composable
fun HelpOptionRow(optionText: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = optionText,
            fontSize = 18.sp,
            color = Color(0xFF1A237E) // Morado oscuro
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_circle_right),  // Icono de flecha
            contentDescription = "Arrow",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF1A237E) // Aplicamos el morado oscuro al ícono
        )
    }
}

@Composable
fun FAQItem(questionText: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically, // Centra verticalmente el icono y el texto
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_newspaper),  // Icono de periódico
            contentDescription = "FAQ Icon",
            modifier = Modifier.size(24.dp),  // Tamaño del ícono
            tint = Color.Gray  // Color del ícono
        )

        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el icono y el texto

        Text(
            text = questionText,
            fontSize = 16.sp,
            color = Color.Gray // Color para preguntas frecuentes
        )
    }
}

// Lista de preguntas frecuentes
val faqQuestions = listOf(
    "¿Cómo funciona la aplicación?",
    "¿Puedo buscar eventos por categoría o ubicación específica?",
    "¿Hay un calendario de mis eventos guardados en la app?",
    "¿Puedo filtrar los resultados por fecha, hora o precio del evento?",
    "¿Cómo puedo contactar con el organizador de un evento?",
    "¿Puedo recibir notificaciones sobre eventos cercanos o actualizaciones del calendario?",
    "¿Puedo compartir los eventos que me gustan en redes sociales?",
    "¿Cómo puedo reportar problemas, sugerencias u ofrecer información adicional?"
)
