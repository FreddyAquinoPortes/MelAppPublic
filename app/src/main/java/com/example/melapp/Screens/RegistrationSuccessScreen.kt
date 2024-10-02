package com.example.melapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.melapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun RegistrationSuccessScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    val email = user?.email ?: "Correo no disponible"

    var userName by remember { mutableStateOf("Usuario") } // Variable para guardar el nombre de usuario
    var isEmailVerified by remember { mutableStateOf(user?.isEmailVerified ?: false) }
    var statusMessage by remember { mutableStateOf("Verificando el estado de verificación...") }
    var resendEmailMessage by remember { mutableStateOf("") }

    // Efecto para buscar el nombre de usuario en Firestore
    LaunchedEffect(user) {
        user?.let {
            try {
                val snapshot = db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .await()

                if (snapshot.documents.isNotEmpty()) {
                    val userData = snapshot.documents[0]
                    userName = userData.getString("user_name") ?: "Usuario"
                }
            } catch (e: Exception) {
                statusMessage = "Error al obtener los datos del usuario: ${e.message}"
            }
        }

        // Verificar si el correo está verificado periódicamente
        while (!isEmailVerified && user != null) {
            user.reload().addOnCompleteListener {
                isEmailVerified = user.isEmailVerified
            }
            kotlinx.coroutines.delay(5000) // Espera 5 segundos antes de volver a verificar
        }

        if (isEmailVerified) {
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
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Por favor, revisa tu correo ($email) y confirma tu cuenta.",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isEmailVerified) {
            // Muestra el ícono verde cuando el correo está verificado
            Image(
                painter = painterResource(id = R.drawable.ic_user_check), // Asegúrate de tener el icono en drawable
                contentDescription = "Correo Verificado",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Green // Texto verde para indicar éxito
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate("map") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Continuar")
            }
        } else {
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
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
        }
    }
}
