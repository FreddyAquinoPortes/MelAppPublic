package com.example.melapp.Screens

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegistrationSuccessScreen(navController: NavController, email: String?, userName: String?) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser

    var isEmailVerified by remember { mutableStateOf(user?.isEmailVerified ?: false) }
    var statusMessage by remember { mutableStateOf("Verificando el estado de verificación...") }

    LaunchedEffect(user) {
        if (user != null && !isEmailVerified) {
            // Envía el correo de verificación si no se ha enviado
            user.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    statusMessage = "Se ha enviado un correo de verificación a $email."
                } else {
                    statusMessage = "Error al enviar el correo de verificación: ${task.exception?.message}"
                }
            }
        }
    }

    LaunchedEffect(isEmailVerified) {
        if (!isEmailVerified) {
            // Verifica el estado del correo periódicamente
            while (!isEmailVerified) {
                user?.reload()?.addOnCompleteListener {
                    isEmailVerified = user.isEmailVerified
                }
                kotlinx.coroutines.delay(5000) // Espera 5 segundos antes de volver a verificar
            }

            // Si el correo está verificado, actualiza el estado de la cuenta
            if (isEmailVerified) {
                db.collection("users").document(user?.uid ?: "")
                    .update("account_status", 1)
                    .addOnSuccessListener {
                        statusMessage = "¡Correo verificado exitosamente!"
                    }
                    .addOnFailureListener {
                        statusMessage = "Error al actualizar el estado de la cuenta: ${it.message}"
                    }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Te has registrado correctamente, $userName!",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Por favor, revisa tu correo y confirma tu cuenta.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = statusMessage,
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))
        if (isEmailVerified) {
            Button(onClick = {
                navController.navigate("next_screen_route") // Cambia a la siguiente pantalla cuando el correo esté verificado
            }) {
                Text(text = "Continuar")
            }
        }
    }
}


