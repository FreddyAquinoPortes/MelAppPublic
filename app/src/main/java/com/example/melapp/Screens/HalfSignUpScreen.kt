package com.example.melapp.Screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.melapp.Backend.PhoneVisualTransformation
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar



@Composable
fun HalfSignUpScreen(navController: NavController) {

    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email

    val viewModel = remember { HalfSignUpViewModel(context, email!!) }

    // Estado de navegación para verificar si debe redirigir al mapa
    val shouldNavigateToMap = remember { mutableStateOf(false) }

    // Verificar el account_state desde Firestore
    LaunchedEffect(email) {
        if (email != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(email)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val accountState = document.getLong("account_state")?.toInt()
                        if (accountState == 1) {
                            // Si account_state es 1, cambiar el estado para navegar
                            shouldNavigateToMap.value = true
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("FirestoreError", "Error al obtener account_state", e)
                }
        }
    }

    // Si shouldNavigateToMap es true, redirigir a la pantalla del mapa
    if (shouldNavigateToMap.value) {
        LaunchedEffect(Unit) {
            navController.navigate("map")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReusableTopBar("Completar Registro", onBackClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            PersonalInfoSection(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            RegisterButton(viewModel, navController)

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


@Composable
fun PersonalInfoSection(viewModel: HalfSignUpViewModel) {
    SectionTitle("Información Personal")
    Spacer(modifier = Modifier.height(8.dp))
    BirthDateFields(viewModel)
    Spacer(modifier = Modifier.height(8.dp))
    GenderDropdown(viewModel)
    Spacer(modifier = Modifier.height(16.dp))
    PhoneNumberFields(viewModel)
}

@Composable
fun BirthDateFields(viewModel: HalfSignUpViewModel) {
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
fun GenderDropdown(viewModel: HalfSignUpViewModel) {
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
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            enabled = false,
            isError = viewModel.shouldShowError && viewModel.genero == "Seleccionar género",
            trailingIcon = {
                if (viewModel.shouldShowError && viewModel.genero == "Seleccionar género") {
                    Icon(Icons.Default.Info, contentDescription = "Campo obligatorio", tint = Color.Red)
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
fun PhoneNumberFields(viewModel: HalfSignUpViewModel) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
        ) {
            TextField(
                value = viewModel.selectedCountry,
                onValueChange = { /* No editable */ },
                label = { Text("País") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.countryExpanded = !viewModel.countryExpanded },
                enabled = false,
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, "Expandir lista de países")
                }
            )

            DropdownMenu(
                expanded = viewModel.countryExpanded,
                onDismissRequest = { viewModel.countryExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                viewModel.countries.forEach { (countryName, phoneCode) ->
                    DropdownMenuItem(
                        text = { Text(countryName) },
                        onClick = {
                            viewModel.selectedCountry = countryName
                            viewModel.countryCode = phoneCode
                            viewModel.countryExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = viewModel.countryCode,
            onValueChange = { /* No editable */ },
            label = { Text("Código") },
            modifier = Modifier.weight(1f),
            enabled = false
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = viewModel.phoneNumber,
            onValueChange = viewModel::updatePhoneNumber,
            label = { Text("Número de Teléfono") },
            modifier = Modifier.weight(2f),
            isError = viewModel.shouldShowError && viewModel.phoneNumber.isEmpty(),
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


@Composable
fun RegisterButton(viewModel: HalfSignUpViewModel, navController: NavController) {
    Button(
        onClick = {
            viewModel.shouldShowError = true
            viewModel.updateUserData(navController)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Completar Registro")
    }
}

class HalfSignUpViewModel(private val context: Context,private val userEmail: String) {
    var birthDay by mutableStateOf("")
    var birthMonth by mutableStateOf("")
    var birthYear by mutableStateOf("")
    var genero by mutableStateOf("Seleccionar género")
    var expanded by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var countryCode by mutableStateOf("+1")
    var phoneNumber by mutableStateOf("")
    var phoneError by mutableStateOf<String?>(null)
    var shouldShowError by mutableStateOf(false)
    var selectedCountry by mutableStateOf("")
    var countries by mutableStateOf(listOf<Pair<String, String>>())
    var countryExpanded by mutableStateOf(false)
    var email: String = userEmail
        private set

    val genderOptions = listOf(
        "Mujer",
        "Hombre",
        "Intersexual",
        "No-binario",
        "Prefiero no decirlo",
        "Otro",
        "Seleccionar género"
    )

    init {
        fetchCountries()
    }

    private fun fetchCountries() {
        FirebaseFirestore.getInstance().collection("Country")
            .get()
            .addOnSuccessListener { result ->
                countries = result.map { document ->
                    val countryName = document.getString("country_name") ?: "Unknown"
                    val phoneCode = document.getString("phone_code") ?: "+0"
                    Pair(countryName, phoneCode)
                }.sortedBy { it.first } // Ordena alfabéticamente por nombre del país
            }
            .addOnFailureListener { exception ->
                errorMessage = "Error fetching countries: ${exception.message}"
            }
    }

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

    fun updateUserData(navController: NavController) {
        val day = birthDay.toIntOrNull()
        val month = birthMonth.toIntOrNull()
        val year = birthYear.toIntOrNull()

        errorMessage = when {
            day == null || month == null || year == null || genero == "Seleccionar género" || phoneNumber.isEmpty() -> {
                "Todos los campos deben estar llenos."
            }

            birthDateError != null -> birthDateError
            generoError != null -> generoError
            else -> null
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

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null && user.email != null) {
                val userEmail = user.email!!
                val usersRef = FirebaseFirestore.getInstance().collection("users")

                usersRef.whereEqualTo("email", userEmail).get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val userDoc = documents.documents[0]
                            val userData = hashMapOf(
                                "birth_date" to "$birthDay/$birthMonth/$birthYear",
                                "gender" to generoCodigo,
                                "Phone_number" to "${countryCode}${phoneNumber}",
                                "account_state" to 1
                            )

                            userDoc.reference.update(userData as Map<String, Any>)
                                .addOnSuccessListener {
                                    navController.navigate("map")
                                }
                                .addOnFailureListener { e ->
                                    errorMessage = "Error al actualizar los datos: ${e.message}"
                                }
                        } else {
                            errorMessage = "No se encontró el usuario con el correo $userEmail"
                        }
                    }
                    .addOnFailureListener { e ->
                        errorMessage = "Error al buscar el usuario: ${e.message}"
                    }
            } else {
                errorMessage = "Usuario no autenticado o sin correo electrónico"
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