package com.example.melapp.Screens



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import com.example.melapp.Backend.PhoneVisualTransformation
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // Variables de estado para los campos de entrada
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var birthDay by remember { mutableStateOf("") }
    var birthMonth by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("Seleccionar género") } // Default value
    var expanded by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var countryCode by remember { mutableStateOf("+1") }  // Código de país predefinido
    var phoneNumber by remember { mutableStateOf("") }     // Número de teléfono
    var phoneError by remember { mutableStateOf<String?>(null) }

    // Validación de fecha
    val birthDateError by remember {
        derivedStateOf {
            val day = birthDay.toIntOrNull()
            val month = birthMonth.toIntOrNull()
            val year = birthYear.toIntOrNull()
            when {
                day == null || month == null || year == null -> "Por favor completa todos los campos de la fecha"
                !isValidDate(day, month, year) -> "Fecha inválida o no cumple con los requisitos"
                else -> null
            }
        }
    }

    // Validación del género
    val generoError by remember {
        derivedStateOf {
            if (genero == "Seleccionar género") "Por favor selecciona un género" else null
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
            TextField(
                value = nombres,
                onValueChange = { nombres = it },
                label = { Text("Nombres") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombres.isEmpty(),
                trailingIcon = {
                    if (nombres.isEmpty()) {
                        Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Apellidos
            TextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                isError = apellidos.isEmpty(),
                trailingIcon = {
                    if (apellidos.isEmpty()) {
                        Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                    }
                }
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

            // Campos de Fecha de Nacimiento
            Row {
                TextField(
                    value = birthDay,
                    onValueChange = { birthDay = it },
                    label = { Text("Día") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    isError = birthDay.isEmpty(),
                    trailingIcon = {
                        if (birthDay.isEmpty()) {
                            Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = birthMonth,
                    onValueChange = { birthMonth = it },
                    label = { Text("Mes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    isError = birthMonth.isEmpty(),
                    trailingIcon = {
                        if (birthMonth.isEmpty()) {
                            Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = birthYear,
                    onValueChange = { birthYear = it },
                    label = { Text("Año") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    isError = birthYear.isEmpty(),
                    trailingIcon = {
                        if (birthYear.isEmpty()) {
                            Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                        }
                    }
                )
            }

            if (birthDateError != null) {
                Text(text = birthDateError!!, color = Color.Red)
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
                    label = { Text("Género") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, cursorColor = Color.Black),
                    enabled = false,
                    isError = genero == "Seleccionar género",
                    trailingIcon = {
                        if (genero == "Seleccionar género") {
                            Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                        }
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(text = { Text("Mujer") }, onClick = { genero = "Genero: Mujer"; expanded = false })
                    DropdownMenuItem(text = { Text("Hombre") }, onClick = { genero = "Genero: Hombre"; expanded = false })
                    DropdownMenuItem(text = { Text("Intersexual") }, onClick = { genero = "Genero: Intersexual"; expanded = false })
                    DropdownMenuItem(text = { Text("No-binario") }, onClick = { genero = "Genero: No-binario"; expanded = false })
                    DropdownMenuItem(text = { Text("Prefiero no decirlo") }, onClick = { genero = "Genero: Prefiero no decirlo"; expanded = false })
                    DropdownMenuItem(text = { Text("Otro") }, onClick = { genero = "Genero: Otro"; expanded = false })
                    DropdownMenuItem(text = { Text("Seleccionar género") }, onClick = { genero = "Seleccionar género"; expanded = false })
                }
            }

            if (generoError != null) {
                Text(text = generoError!!, color = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = countryCode,
                    onValueChange = { /* Dejar vacío para que no se modifique */ },
                    label = { Text("Código de País") },
                    modifier = Modifier.weight(1f),
                    enabled = true  // No se puede editar el código de país
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length > 10) {
                        phoneError = "El máximo de caracteres permitidos es 10"
                    } else {
                        phoneError = null
                        phoneNumber = it
                    }
                                    },
                    label = { Text("Número de Teléfono") },
                    modifier = Modifier.weight(3f),
                    isError = phoneError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    visualTransformation = PhoneVisualTransformation()  // Transformación visual personalizada
                )

            }
            if (phoneError != null) {
                Text(
                    text = phoneError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
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

            // Campo de Nombre de Usuario
            TextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Nombre de Usuario") },
                modifier = Modifier.fillMaxWidth(),
                isError = userName.isEmpty(),
                trailingIcon = {
                    if (userName.isEmpty()) {
                        Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Correo Electrónico
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = email.isEmpty(),
                trailingIcon = {
                    if (email.isEmpty()) {
                        Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                    }
                }
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
                isError = password.isEmpty(),
                trailingIcon = {
                    if (password.isEmpty()) {
                        Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                    }
                }
            )

            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
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
                    val day = birthDay.toIntOrNull()
                    val month = birthMonth.toIntOrNull()
                    val year = birthYear.toIntOrNull()

                    errorMessage = when {
                        nombres.isEmpty() || apellidos.isEmpty() || day == null || month == null || year == null || genero == "Seleccionar género" || userName.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                            "Todos los campos deben estar llenos."
                        }
                        birthDateError != null -> birthDateError
                        generoError != null -> generoError
                        else -> validateEmailAndPassword(email, password)
                    }

                    if (errorMessage == null) {
                        val generoCodigo = when (genero) {
                            "Genero: Mujer" -> 0
                            "Genero: Hombre" -> 1
                            "Genero: Intersexual" -> 2
                            "Genero: No-binario" -> 3
                            "Genero: Prefiero no decirlo" -> 4
                            else -> 5
                        }

                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = mapOf(
                                        "nombres" to nombres,
                                        "apellidos" to apellidos,
                                        "fecha_nacimiento" to "$day/$month/$year",
                                        "genero" to generoCodigo,
                                        "email" to email,

                                    )
                                    db.collection("users").add(user)
                                } else {
                                    errorMessage = task.exception?.message
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }

            // Mensaje de error
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

// Función auxiliar para validar la fecha de nacimiento
private fun isValidDate(day: Int, month: Int, year: Int): Boolean {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val minimumYear = currentYear - 10

    if (year > minimumYear) {
        return false
    }

    val daysInMonth = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> return false
    }

    return day in 1..daysInMonth && year in 1900..minimumYear
}

// Función auxiliar para validar años bisiestos
private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

