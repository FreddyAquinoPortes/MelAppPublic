package com.example.melapp.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.melapp.R
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically



@Composable
fun CollapsibleSettingsOption(
    title: String,
    subOptions: List<String>,
    isSecondary: Boolean = false,
    isCheckbox: Boolean = false,
    selectedOption: MutableState<String?> = mutableStateOf(null),
    onOptionClick: (() -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isSecondary) 16.dp else 8.dp)
    ) {
        // Título del ajuste (por ejemplo, Idioma)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isExpanded = !isExpanded
                    onOptionClick?.invoke() // Ejecutar la acción cuando se hace clic
                }
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color(0xFFFFFFFF), // Color blanco para el título
                fontSize = 20.sp
            )
            Icon(
                painter = if (isExpanded) painterResource(R.drawable.ic_circle) else painterResource(R.drawable.ic_circle_down),
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = Color(0xFFFFFFFF) // Icono blanco
            )
        }

        // Subopciones desplegadas con animación
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(), // Animación de entrada (despliegue y desvanecimiento)
            exit = shrinkVertically() + fadeOut()  // Animación de salida (colapso y desvanecimiento)
        ) {
            Column {
                subOptions.forEach { option ->
                    Text(
                        text = option,
                        color = Color(0xFFB0BEC5), // Color gris claro para el texto de las opciones
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
                            .background(Color(0xFF424242)) // Fondo gris oscuro para las opciones
                            .padding(8.dp), // Padding para darle espacio al texto
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val selectedLanguage = remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo

    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = "Ajustes",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { navController.navigate("profileScreen") },
                onPostEventClick = { navController.navigate("map") },
                onPublishClick = { navController.navigate("all_event_screen") },
                onSettingsClick = { navController.navigate("settingsScreen") }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            CollapsibleSettingsOption(
                title = "Idioma",
                subOptions = listOf("Español", "Inglés"),
                isCheckbox = true,
                selectedOption = selectedLanguage
            )
            CollapsibleSettingsOption("Cuenta", listOf("Seguridad", "Accesibilidad"))
            CollapsibleSettingsOption("Seguridad", listOf("Cambiar contraseña", "Autenticación de dos factores"))
            CollapsibleSettingsOption("Accesibilidad", listOf("Texto grande", "Contraste alto"))
            CollapsibleSettingsOption("Notificaciones", listOf("Activar", "Sonido", "Vibración"))
            CollapsibleSettingsOption("Condiciones y Políticas", listOf("Términos de servicio", "Política de privacidad"))
            CollapsibleSettingsOption("Acerca de", listOf("Versión", "Licencias"))

            // Implementación del botón de Cerrar Sesión
            Button(
                onClick = {
                    showLogoutDialog = true // Mostrar el diálogo de confirmación
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(50.dp) // Bordes redondeados
            ) {
                Text(text = "Cerrar sesión", color = Color.White)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Ayuda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { navController.navigate("helpScreen") },
                    modifier = Modifier
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB39DDB))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_help),
                        contentDescription = "Ayuda",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Ayuda", color = Color.White)
                }
            }
        }

        // Diálogo de confirmación para cerrar sesión
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false }, // Cierra el diálogo al hacer clic fuera
                title = { Text(text = "Cerrar sesión") },
                text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            FirebaseAuth.getInstance().signOut() // Cerrar sesión
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                            showLogoutDialog = false // Cierra el diálogo
                        }
                    ) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false } // Cierra el diálogo sin cerrar sesión
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}
