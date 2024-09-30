package com.example.melapp.Screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.melapp.Backend.validateEmailAndPassword
import com.example.melapp.Backend.PhoneVisualTransformation
import com.example.melapp.ReusableComponents.ReusableTopBar
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val signUpViewModel = remember { SignUpViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReusableTopBar("Registrarse", onBackClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            PersonalInfoSection(signUpViewModel)
            Spacer(modifier = Modifier.height(60.dp))
            LoginInfoSection(signUpViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            RegisterButton(signUpViewModel)

            if (signUpViewModel.errorMessage != null) {
                Text(
                    text = signUpViewModel.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PersonalInfoSection(viewModel: SignUpViewModel) {
    SectionTitle("Información Personal")
    Spacer(modifier = Modifier.height(8.dp))
    CustomTextField(
        value = viewModel.nombres,
        onValueChange = { viewModel.nombres = it },
        label = "Nombres"
    )
    Spacer(modifier = Modifier.height(8.dp))
    CustomTextField(
        value = viewModel.apellidos,
        onValueChange = { viewModel.apellidos = it },
        label = "Apellidos"
    )
    Spacer(modifier = Modifier.height(30.dp))
    BirthDateFields(viewModel)
    Spacer(modifier = Modifier.height(8.dp))
    GenderDropdown(viewModel)
    Spacer(modifier = Modifier.height(16.dp))
    PhoneNumberFields(viewModel)
}

@Composable
fun LoginInfoSection(viewModel: SignUpViewModel) {
    SectionTitle("Inicio de Sesión")
    Spacer(modifier = Modifier.height(8.dp))
    CustomTextField(
        value = viewModel.userName,
        onValueChange = { viewModel.userName = it },
        label = "Nombre de Usuario"
    )
    Spacer(modifier = Modifier.height(8.dp))
    CustomTextField(
        value = viewModel.email,
        onValueChange = { viewModel.email = it },
        label = "Correo Electrónico",
        keyboardType = KeyboardType.Email
    )
    Spacer(modifier = Modifier.height(8.dp))
    PasswordField(viewModel)
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BirthDateFields(viewModel: SignUpViewModel) {
    Text(
        text = "Fecha de Nacimiento",
        fontSize = 18.sp,
        color = Color.Gray,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CustomTextField(
            value = viewModel.birthDay,
            onValueChange = { viewModel.birthDay = it },
            label = "Día",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        CustomTextField(
            value = viewModel.birthMonth,
            onValueChange = { viewModel.birthMonth = it },
            label = "Mes",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        CustomTextField(
            value = viewModel.birthYear,
            onValueChange = { viewModel.birthYear = it },
            label = "Año",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
    }
    if (viewModel.birthDateError != null) {
        Text(text = viewModel.birthDateError!!, color = Color.Red)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        isError = value.isEmpty(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = {
            if (value.isEmpty()) {
                Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(viewModel: SignUpViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        TextField(
            value = viewModel.genero,
            onValueChange = { viewModel.genero = it },
            label = { Text("Género") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.expanded = !viewModel.expanded },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, cursorColor = Color.Black),
            enabled = false,
            isError = viewModel.genero == "Seleccionar género",
            trailingIcon = {
                if (viewModel.genero == "Seleccionar género") {
                    Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
                }
            }
        )

        DropdownMenu(
            expanded = viewModel.expanded,
            onDismissRequest = { viewModel.expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            viewModel.genderOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        viewModel.genero = "Genero: $option"
                        viewModel.expanded = false
                    }
                )
            }
        }
    }
    if (viewModel.generoError != null) {
        Text(text = viewModel.generoError!!, color = Color.Red)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberFields(viewModel: SignUpViewModel) {
    Row(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = viewModel.countryCode,
            onValueChange = { /* No editable */ },
            label = { Text("Código de País") },
            modifier = Modifier.weight(1f),
            enabled = false
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = viewModel.phoneNumber,
            onValueChange = viewModel::updatePhoneNumber,
            label = { Text("Número de Teléfono") },
            modifier = Modifier.weight(3f),
            isError = viewModel.phoneError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            visualTransformation = PhoneVisualTransformation()
        )
    }
    if (viewModel.phoneError != null) {
        Text(
            text = viewModel.phoneError!!,
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(viewModel: SignUpViewModel) {
    TextField(
        value = viewModel.password,
        onValueChange = { viewModel.password = it },
        label = { Text("Contraseña") },
        visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth(),
        isError = viewModel.password.isEmpty(),
        trailingIcon = {
            if (viewModel.password.isEmpty()) {
                Icon(Icons.Default.Info, contentDescription = "Campo obligatorio")
            }
        }
    )
    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Checkbox(
            checked = viewModel.passwordVisible,
            onCheckedChange = { viewModel.passwordVisible = it }
        )
        Text(
            text = "Mostrar contraseña",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun RegisterButton(viewModel: SignUpViewModel) {
    Button(
        onClick = { viewModel.registerUser() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Registrarse")
    }
}

class SignUpViewModel {
    var nombres by mutableStateOf("")
    var apellidos by mutableStateOf("")
    var birthDay by mutableStateOf("")
    var birthMonth by mutableStateOf("")
    var birthYear by mutableStateOf("")
    var genero by mutableStateOf("Seleccionar género")
    var expanded by mutableStateOf(false)
    var userName by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var countryCode by mutableStateOf("+1")
    var phoneNumber by mutableStateOf("")
    var phoneError by mutableStateOf<String?>(null)
    var rol by mutableStateOf(0)

    val genderOptions = listOf("Mujer", "Hombre", "Intersexual", "No-binario", "Prefiero no decirlo", "Otro", "Seleccionar género")

    val birthDateError by derivedStateOf {
        val day = birthDay.toIntOrNull()
        val month = birthMonth.toIntOrNull()
        val year = birthYear.toIntOrNull()
        when {
            day == null || month == null || year == null -> "Por favor completa todos los campos de la fecha"
            !isValidDate(day, month, year) -> "Fecha inválida o no cumple con los requisitos"
            else -> null
        }
    }

    val generoError by derivedStateOf {
        if (genero == "Seleccionar género") "Por favor selecciona un género" else null
    }

    fun updatePhoneNumber(newNumber: String) {
        if (newNumber.length > 10) {
            phoneError = "El máximo de caracteres permitidos es 10"
        } else {
            phoneError = null
            phoneNumber = newNumber
        }
    }

    fun registerUser() {
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

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = hashMapOf(
                            "name" to nombres,
                            "lastname" to apellidos,
                            "birth_date" to "$day/$month/$year",
                            "gender" to generoCodigo,
                            "email" to email,
                            "user_name" to userName,
                            "Phone_number" to "${countryCode}${phoneNumber}",
                            "rol" to rol
                        )

                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            FirebaseFirestore.getInstance().collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener {
                                    // Éxito al guardar en Firestore
                                    errorMessage = null
                                    // Aquí puedes navegar a la siguiente pantalla o mostrar un mensaje de éxito
                                }
                                .addOnFailureListener { e ->
                                    // Error al guardar en Firestore
                                    errorMessage = "Error al guardar los datos del usuario: ${e.message}"
                                }
                        } else {
                            errorMessage = "Error al obtener el ID del usuario"
                        }
                    } else {
                        errorMessage = task.exception?.message ?: "Error desconocido al crear la cuenta"
                    }
                }
        }
    }

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

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}