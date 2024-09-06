package com.example.melapp.Screens



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.navigation.NavController
import com.example.melapp.Backend.ValidatedTextField
import com.google.firebase.auth.FirebaseAuth
import com.example.melapp.Backend.validateEmailAndPassword
import androidx.compose.runtime.LaunchedEffect
import java.time.LocalDate
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    // Variables de estado para los campos de entrada
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var birthMonth by remember { mutableStateOf("") }
    var birthDay by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("Assigned sex at birth: Female") }
    var expanded by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var telefono by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()

    // Validación de fecha
    val birthDateError by remember {
        derivedStateOf {
            val month = birthMonth.toIntOrNull()
            val day = birthDay.toIntOrNull()
            val year = birthYear.toIntOrNull()

            when {
                birthMonth.isEmpty() || birthDay.isEmpty() || birthYear.isEmpty() -> "Por favor completa todos los campos de la fecha"
                month == null || month !in 1..12 -> "Mes inválido"
                day == null || day !in 1..31 -> "Día inválido"
                year == null || year !in 1900..(currentYear - 10) -> "Año inválido"
                else -> {
                    val maxDaysInMonth = when (month) {
                        4, 6, 9, 11 -> 30 // Abril, Junio, Septiembre, Noviembre
                        2 -> if (isLeapYear(year)) 29 else 28 // Febrero
                        else -> 31
                    }
                    if (day > maxDaysInMonth) "Fecha inválida" else null
                }
            }
        }
    }

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

            if (birthDateError != null) {
                Text(
                    text = birthDateError!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
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

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null && errorMessage == "Correo inválido"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Contraseña
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null && errorMessage != "Correo inválido"
            )

            // Checkbox para mostrar la contraseña
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Checkbox(
                    checked = passwordVisible,
                    onCheckedChange = { passwordVisible = it }
                )
                Text(
                    text = "Mostrar contraseña",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de registro
            Button(
                onClick = {
                    errorMessage = validateEmailAndPassword(email, password)
                    if (errorMessage == null) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("traditional_login") // Navegar a la pantalla de Login
                                } else {
                                    errorMessage = task.exception?.message
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Registrarse")
            }

            // Mensaje de error
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

// Función auxiliar para validar años bisiestos
private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}
