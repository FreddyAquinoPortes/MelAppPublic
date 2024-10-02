package com.example.melapp.Screens

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

@Composable
fun CollapsibleSettingsOption(
    title: String,
    subOptions: List<String>,
    isSecondary: Boolean = false,
    isCheckbox: Boolean = false,
    selectedOption: MutableState<String?> = mutableStateOf(null),
    onOptionClick: (() -> Unit)? = null // Nuevo parámetro para manejar clics
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isSecondary) 16.dp else 8.dp)
    ) {
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
                color = Color(0xFF1A237E),
                fontSize = 20.sp
            )
            Icon(
                painter = if (isExpanded) painterResource(R.drawable.ic_circle) else painterResource(R.drawable.ic_circle_down),
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = Color(0xFF1A237E)
            )
        }

        if (isExpanded && subOptions.isNotEmpty()) {
            subOptions.forEach { option ->
                ClickableText(
                    text = AnnotatedString(option),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                    onClick = {
                        // Manejar clics en cada subopción si es necesario
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                onPublishClick = { navController.navigate("event_form") },
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

            // Implementación de Cerrar Sesión con confirmación
            CollapsibleSettingsOption(
                title = "Cerrar sesión",
                subOptions = emptyList(),
                onOptionClick = {
                    showLogoutDialog = true // Mostrar el diálogo de confirmación
                }
            )

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
