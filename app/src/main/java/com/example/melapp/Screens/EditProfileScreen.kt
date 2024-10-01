package com.example.melapp.Screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun EditProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    // State to hold the user's current username and email
    var newUsername by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }

    // Fetch user data from Firestore based on email when the composable is first composed
    LaunchedEffect(email) {
        email?.let { userEmail ->
            Log.d("Firestore", "Fetching user data for email: $userEmail")

            db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.first()
                        newUsername = document.getString("user_name") ?: ""
                    } else {
                        Log.d("Firestore", "No user found with email: $userEmail")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching user data", exception)
                }
        } ?: run {
            Log.e("Firestore", "User email is null, could not fetch data")
        }
    }

    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = "Editar Perfil",
                onBackClick = { navController.popBackStack() } // Go back to profile screen
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Editar Perfil", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Text field to edit username
            OutlinedTextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("Nuevo Nombre de Usuario") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to save changes
            Button(onClick = {
                if (newUsername.isNotEmpty()) {
                    // Update username in Firestore
                    email?.let { userEmail ->
                        db.collection("users")
                            .whereEqualTo("email", userEmail)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    val documentId = documents.first().id
                                    db.collection("users").document(documentId)
                                        .update("user_name", newUsername)
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "Username successfully updated.")
                                            navController.popBackStack() // Navigate back after saving
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Error updating username", e)
                                        }
                                }
                            }
                    }
                } else {
                    Log.e("EditProfileScreen", "Username cannot be empty.")
                }
            }) {
                Text(text = "Guardar Cambios")
            }
        }
    }
}

