package com.example.melapp.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.example.melapp.R
import com.example.melapp.ReusableComponents.NavigationBottomBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable

fun HelpScreen(navController: NavController) {
    var showContactInfo by remember { mutableStateOf(false) } // Controla si se muestra la información de contacto

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
                    .padding(paddingValues) // Evita solapamiento con top y bottom bar
                    .padding(16.dp) // Padding adicional para el contenido
            ) {
                item {
                    Text(text = "¿En qué te podemos ayudar?", fontSize = 22.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Opciones de ayuda
                    HelpOptionRow(
                        optionText = "Reportar un problema",
                        onClick = { navController.navigate("reportProblemScreen") }
                    )

                    HelpOptionRow(
                        optionText = "Contacto",
                        onClick = { showContactInfo = !showContactInfo } // Alternar la visibilidad de la información de contacto
                    )

                    Spacer(modifier = Modifier.height(24.dp)) // Espacio entre secciones
                }

                // Mostrar información de contacto si se presionó la opción
                if (showContactInfo) {
                    item {
                        Text(
                            text = "Contáctanos en: melapp@yopmail.com",
                            fontSize = 16.sp,
                            color = Color.Blue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Podemos ofrecerte asistencia para resolver cualquier problema relacionado con la app o responder tus preguntas.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(24.dp)) // Espacio antes de continuar con otras opciones
                    }
                }

                // Preguntas Frecuentes
                item {
                    Text(text = "Preguntas Frecuentes", fontSize = 20.sp, color = Color.Gray)
                }

                // Lista de FAQs
                items(faqList.size) { index ->
                    val (question, answer) = faqList[index]
                    var expanded by remember { mutableStateOf(false) }

                    FAQItem(
                        questionText = question,
                        answerText = answer,
                        expanded = expanded,
                        onClick = { expanded = !expanded }
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
fun FAQItem(questionText: String, answerText: String, expanded: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, // Centra verticalmente el icono y el texto
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_newspaper), // Icono de periódico
                contentDescription = "FAQ Icon",
                modifier = Modifier.size(24.dp), // Tamaño del ícono
                tint = Color.Gray // Color del ícono
            )
            Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el icono y el texto

            Text(
                text = questionText,
                fontSize = 16.sp,
                color = Color.Gray // Color para preguntas frecuentes
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre la pregunta y la respuesta
            Text(
                text = answerText,
                fontSize = 14.sp,
                color = Color.Black // Color para las respuestas
            )
        }
    }
}

// Lista de FAQs con preguntas y respuestas
val faqList = listOf(
    Pair("¿Cómo funciona la aplicación?", "La aplicación te permite buscar, explorar y guardar eventos de acuerdo a tus preferencias."),
    Pair("¿Puedo buscar eventos por categoría o ubicación específica?", "Sí, puedes filtrar eventos según categorías como música, teatro, deportes, etc., y también por ubicación."),
    Pair("¿Hay un calendario de mis eventos guardados en la app?", "Sí, hay un calendario que te permite ver todos los eventos que has guardado o marcado como favoritos."),
    Pair("¿Puedo filtrar los resultados por fecha, hora o precio del evento?", "Sí, puedes usar filtros para ajustar los resultados por fecha, hora o rango de precios."),
    Pair("¿Cómo puedo contactar con el organizador de un evento?", "En cada evento encontrarás un botón de contacto que te redirige a los datos de contacto del organizador."),
    Pair("¿Puedo recibir notificaciones sobre eventos cercanos o actualizaciones del calendario?", "Sí, puedes habilitar las notificaciones para recibir alertas sobre eventos cercanos o cambios en tus eventos guardados."),
    Pair("¿Puedo compartir los eventos que me gustan en redes sociales?", "Sí, cada evento tiene un botón para compartirlo en tus redes sociales favoritas."),
    Pair("¿Cómo puedo reportar problemas, sugerencias u ofrecer información adicional?", "En la sección de Ajustes encontrarás la opción de 'Reportar un problema' o 'Enviar sugerencias'.")
)


