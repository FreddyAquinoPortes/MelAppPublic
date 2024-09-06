package com.example.melapp.Screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.melapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun PasswordRecoveryScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
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

            // Título "Recuperar Contraseña"
            Text(
                text = "Recuperar Contraseña",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 40.dp)
            )

            // Descripción
            Text(
                text = "Ingrese su correo para recuperar su contraseña",
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

            Spacer(modifier = Modifier.height(40.dp))

            // Botón "Enviar"
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (email.isEmpty()) {
                            errorMessage = "El campo de correo no puede estar vacío."
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            errorMessage = "Formato de correo incorrecto."
                        } else {
                            errorMessage = null
                            auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        successMessage = "Correo de recuperación enviado."
                                    } else {
                                        errorMessage = "Error al enviar el correo de recuperación."
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
                    text = "Enviar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // Mensajes de error o éxito
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (successMessage != null) {
                Text(
                    text = successMessage!!,
                    color = Color.Green,
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
