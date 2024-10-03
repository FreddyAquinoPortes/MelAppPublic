package com.example.melapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.melapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun TradicionalLoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Fondo decorativo (rectángulos)
        Image(
            painter = painterResource(id = R.drawable.rectangle_1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(150.dp, 150.dp)
                .offset(x = (-45).dp, y = (-80).dp)
        )

        Image(
            painter = painterResource(id = R.drawable.rectangle_5),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(150.dp, 150.dp)
                .offset(x = (-85).dp, y = 600.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Título "Iniciar"
            Text(
                text = "Iniciar",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 40.dp)
            )

            // Descripción
            Text(
                text = "Por favor ingrese su correo y contraseña para iniciar sesión",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier
                    .padding(vertical = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo "Correo"
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = errorMessage != null && email.isEmpty()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo "Contraseña"
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(),
                isError = errorMessage != null && password.isEmpty()
            )

            // CheckBox para mostrar/ocultar la contraseña
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = passwordVisible,
                    onCheckedChange = { passwordVisible = it }
                )
                Text(
                    text = "Mostrar contraseña",
                    fontSize = 14.sp,
                    color = Color(0xFF575757)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Texto "Olvido la contraseña"
            Text(
                text = "Olvidó la contraseña",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF575757),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clickable {
                        navController.navigate("passwordRecovery") // Navega a la pantalla de recuperación
                    }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botón "Iniciar"
            Button(
                onClick = {
                    coroutineScope.launch {
                        when {
                            email.isEmpty() -> errorMessage = "El campo de correo no puede estar vacío."
                            password.isEmpty() -> errorMessage = "El campo de contraseña no puede estar vacío."
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errorMessage = "Formato de correo incorrecto."
                            else -> {
                                errorMessage = null
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val userId = auth.currentUser?.uid
                                            if (userId != null) {
                                                firestore.collection("users").document(userId).get()
                                                    .addOnSuccessListener { document ->
                                                        val accountState = document.getLong("account_state")
                                                        if (accountState == 1L) {
                                                            navController.navigate("map") // Navegar a la pantalla principal
                                                        } else if (accountState == 0L) {
                                                            navController.navigate("registration_success") // Redirigir a RegistrationSuccessScreen
                                                        } else {
                                                            errorMessage = "Estado de la cuenta no válido."
                                                        }
                                                    }
                                                    .addOnFailureListener {
                                                        errorMessage = "No se pudo obtener el estado de la cuenta."
                                                    }
                                            }
                                        } else {
                                            errorMessage = "Credenciales incorrectas."
                                        }
                                    }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFA6CE39))
            ) {
                Text(
                    text = "Iniciar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Texto "No tiene una cuenta? Registrarse"
            TextButton(
                onClick = { navController.navigate("register") }, // Navega a la pantalla de registro
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "¿No tienes una cuenta? Registrarse", color = Color(0xFF24146C))
            }
        }
    }
}
