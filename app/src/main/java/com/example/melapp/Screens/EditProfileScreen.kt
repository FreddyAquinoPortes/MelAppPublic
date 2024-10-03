package com.example.melapp.Screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun EditProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    // State to hold the user's current username, email, and profile image URL
    var newUsername by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var profileImageUrl by remember { mutableStateOf("") } // URL de la imagen de perfil

    // Lanzador para seleccionar imagen
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Subir la imagen a Firebase Storage cuando se selecciona
            uploadImageToFirebaseStorage(it) { downloadUrl ->
                profileImageUrl = downloadUrl
            }
        }
    }

    // Fetch user data from Firestore based on email when the composable is first composed
    LaunchedEffect(email) {
        email?.let { userEmail ->
            db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.first()
                        newUsername = document.getString("user_name") ?: ""
                        profileImageUrl = document.getString("profile_image") ?: ""
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching user data", exception)
                }
        }
    }

    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = "Editar Perfil",
                onBackClick = { navController.popBackStack() }
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

            // Mostrar imagen de perfil
            if (profileImageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUrl),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier.size(128.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para seleccionar una nueva imagen de perfil
            Button(onClick = {
                launcher.launch("image/*") // Lanza el selector de imágenes
            }) {
                Text("Seleccionar Imagen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para editar nombre de usuario
            OutlinedTextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("Nuevo Nombre de Usuario") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para guardar cambios
            Button(onClick = {
                if (newUsername.isNotEmpty()) {
                    // Actualizar nombre de usuario y URL de imagen en Firestore
                    email?.let { userEmail ->
                        db.collection("users")
                            .whereEqualTo("email", userEmail)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    val documentId = documents.first().id
                                    db.collection("users").document(documentId)
                                        .update(mapOf(
                                            "user_name" to newUsername,
                                            "profile_image" to profileImageUrl
                                        ))
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "Perfil actualizado correctamente.")
                                            navController.popBackStack()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Error actualizando perfil", e)
                                        }
                                }
                            }
                    }
                } else {
                    Log.e("EditProfileScreen", "El nombre de usuario no puede estar vacío.")
                }
            }) {
                Text(text = "Guardar Cambios")
            }
        }
    }
}

// Función para subir la imagen a Firebase Storage
fun uploadImageToFirebaseStorage(imageUri: Uri, onSuccess: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val profileImagesRef = storageRef.child("profile_images/${user?.uid}.jpg")

    val uploadTask = profileImagesRef.putFile(imageUri)

    uploadTask.addOnSuccessListener {
        profileImagesRef.downloadUrl.addOnSuccessListener { uri ->
            onSuccess(uri.toString()) // Devolver la URL de descarga
        }
    }.addOnFailureListener {
        Log.e("FirebaseStorage", "Error al subir la imagen", it)
    }
}



