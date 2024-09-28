package com.example.melapp.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.melapp.ReusableComponents.ReusableTopBar

@Composable
fun CollapsibleSettingsOption(
    title: String,
    subOptions: List<String>,
    isSecondary: Boolean = false,
    isCheckbox: Boolean = false,
    selectedOption: MutableState<String?> = mutableStateOf(null) // Track selected option
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isSecondary) 16.dp else 8.dp) // More padding for secondary sections
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
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
        if (isExpanded) {
            subOptions.forEach { option ->
                if (isCheckbox) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
                            .clickable {
                                // Deselect the previously selected option and select the new one
                                selectedOption.value = option
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedOption.value == option,
                            onCheckedChange = {
                                selectedOption.value = option
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF1A237E)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option)
                    }
                } else {
                    ClickableText(
                        text = AnnotatedString(option),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                        onClick = { /* Handle option click */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // More space between sub-options
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val selectedLanguage = remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = "Ajustes", // Pass the screen title here
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { /* Navigate to profile */ },
                onPostEventClick = { /* Navigate to create an event */ },
                onSettingsClick = { navController.navigate("settingsScreen") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Make the content scrollable
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
            CollapsibleSettingsOption("Salir", emptyList())

            Spacer(modifier = Modifier.weight(1f))

            // Help Button on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { /* Handle Help action */ },
                    modifier = Modifier
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB39DDB)) // Light purple
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_help), // Replace with your icon
                        contentDescription = "Ayuda",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Ayuda", color = Color.White)
                }
            }
        }
    }
}
