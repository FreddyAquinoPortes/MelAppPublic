package com.example.melapp.Screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.melapp.Backend.ValidatedTextField
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    // Variables de estado para los campos de entrada
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var birthMonth by remember { mutableStateOf("") }
    var birthDay by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("Assigned sex at birth: Female") }
    var expanded by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var telefono by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Fondo superior
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        ) {
            drawRoundRect(
                color = Color(0xFF24146C),
                cornerRadius = CornerRadius(10f, 100f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(45.dp))
            // Título
            Text(
                text = "Registrarse",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Sección Información Personal
            Text(
                text = "Información Personal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Nombres
            ValidatedTextField(
                value = nombres,
                onValueChange = { nombres = it },
                label = "Nombres"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Apellidos
            ValidatedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = "Apellidos"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Título para Fecha de Nacimiento
            Text(
                text = "Fecha de Nacimiento",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Campo de Fecha de Nacimiento
            Row(modifier = Modifier.fillMaxWidth()) {
                ValidatedTextField(
                    value = birthMonth,
                    onValueChange = { birthMonth = it },
                    label = "Mes",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                ValidatedTextField(
                    value = birthDay,
                    onValueChange = { birthDay = it },
                    label = "Día",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                ValidatedTextField(
                    value = birthYear,
                    onValueChange = { birthYear = it },
                    label = "Año",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Género con Dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                TextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Género", fontSize = 16.sp, color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, cursorColor = Color.Black),
                    enabled = false
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("Mujer") },
                        onClick = {
                            genero = "Genero: Mujer"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hombre") },
                        onClick = {
                            genero = "Genero: Hombre"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Intersexual") },
                        onClick = {
                            genero = "Genero: Intersexual"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("No-binario") },
                        onClick = {
                            genero = "Genero: No-binario"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Prefiero no decirlo") },
                        onClick = {
                            genero = "Genero: Prefiero no decirlo"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Otro") },
                        onClick = {
                            genero = "Genero: Otro"
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Sección Inicio de Sesión
            Text(
                text = "Inicio de Sesión",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Nombre de Usuario para Login
            ValidatedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = "Nombre de Usuario para Login"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Correo Electrónico
            ValidatedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = "Correo Electrónico"
            )

            Spacer(modifier = Modifier.height(8.dp))


            // Campo de Contraseña
            ValidatedTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña"
            )


            Spacer(modifier = Modifier.height(8.dp))

            // Checkbox para mostrar la contraseña
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Checkbox(
                    checked = passwordVisible,
                    onCheckedChange = { passwordVisible = it }
                )
                Text(text = "Mostrar contraseña")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección Ubicación
            Text(
                text = "Ubicación",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Sector
            ValidatedTextField(
                value = sector,
                onValueChange = { sector = it },
                label = "Sector"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Ciudad
            ValidatedTextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = "Ciudad"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Municipio
            ValidatedTextField(
                value = municipio,
                onValueChange = { municipio = it },
                label = "Municipio"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Registrarse
            Button(
                onClick = { navController.navigate("traditional_login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Registrarse")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Login
            TextButton(
                onClick = { navController.navigate("traditional_login") }, // Navega a la pantalla de login tradicional
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "¿Ya tienes una cuenta? Inicia sesión", color = Color(0xFF24146C))
            }
        }
    }
}