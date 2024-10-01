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
fun RegistrationSuccessScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    val email = user?.email ?: "Correo no disponible"
    val userName = user?.displayName ?: "Usuario" // Puedes obtener el nombre de usuario desde Firestore si está guardado

    var isEmailVerified by remember { mutableStateOf(user?.isEmailVerified ?: false) }
    var statusMessage by remember { mutableStateOf("Verificando el estado de verificación...") }
    var resendEmailMessage by remember { mutableStateOf("") }

    // Efecto para verificar si el correo está verificado periódicamente
    LaunchedEffect(user) {
        while (!isEmailVerified && user != null) {
            user.reload().addOnCompleteListener {
                isEmailVerified = user.isEmailVerified
            }
            kotlinx.coroutines.delay(5000) // Espera 5 segundos antes de volver a verificar
        }

        if (isEmailVerified) {
            // Si el correo está verificado, actualiza el estado de la cuenta en Firestore
            user?.uid?.let { userId ->
                db.collection("users").document(userId)
                    .update("account_state", 1)
                    .addOnSuccessListener {
                        statusMessage = "¡Correo verificado exitosamente!"
                    }
                    .addOnFailureListener { e ->
                        statusMessage = "Error al actualizar el estado de la cuenta: ${e.message}"
                    }
            }
        }
    }

    // Función para reenviar el correo de verificación
    fun resendVerificationEmail() {
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                resendEmailMessage = "Correo de verificación reenviado a $email."
            } else {
                resendEmailMessage = "Error al reenviar el correo de verificación: ${task.exception?.message}"
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

        if (!isEmailVerified) {
            Text(
                text = "Tu correo no ha sido verificado.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { resendVerificationEmail() }) {
                Text(text = "Reenviar correo de verificación")
            }
            if (resendEmailMessage.isNotEmpty()) {
                Text(
                    text = resendEmailMessage,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            Button(onClick = {
                navController.navigate("next_screen_route") // Cambia a la siguiente pantalla si el correo está verificado
            }) {
                Text(text = "Continuar")
            }
        }
    }
}
