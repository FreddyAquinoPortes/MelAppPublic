package com.example.melapp.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.melapp.R
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.example.melapp.ReusableComponents.ReusableTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    // State to hold user_name, email, and profile image URL
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var profileImageUrl by remember { mutableStateOf("") }

    // Fetch user data from Firestore
    LaunchedEffect(email) {
        email?.let { userEmail ->
            Log.d("Firestore", "Fetching user data for email: $userEmail")

            db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.first()
                        Log.d("Firestore", "DocumentSnapshot data: ${document.data}")

                        username = document.getString("user_name") ?: ""
                        profileImageUrl = document.getString("profile_image") ?: ""

                    } else {
                        Log.d("Firestore", "No such document with email: $userEmail")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting document", exception)
                }
        } ?: run {
            Log.e("Firestore", "User email is null, could not fetch data")
        }
    }

    Scaffold(
        topBar = {
            ReusableTopBar(
                screenTitle = "Perfil",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onProfileClick = { /* Lógica para ir a la pantalla de perfil */ },
                onPostEventClick = { navController.navigate("map") },
                onPublishClick = { navController.navigate("event_form") },
                onSettingsClick = { navController.navigate("settingsScreen") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                // Foto de perfil y botón de editar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mostrar imagen de perfil
                    if (profileImageUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(profileImageUrl),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = "Foto de perfil por defecto",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostrar nombre de usuario y correo
                    Text(text = "Username: $username", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Email: $email", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones en fila (Editar Perfil y Ver Eventos Guardados)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("editprofilescreen")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pencil),
                                contentDescription = "Icono de editar"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Editar Perfil", maxLines = 1)
                        }

                        Button(
                            onClick = { /* Lógica para ver eventos guardados */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bookmark),
                                contentDescription = "Icono de eventos guardados"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Ver Eventos Guardados", maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Título de la sección de eventos publicados
                Text(
                    text = "Eventos Publicados",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Cuadrícula de eventos publicados
            items(10) { index ->
                EventCard(
                    eventName = "Evento $index",
                    location = "@Ubicación $index",
                    imageRes = R.drawable.ic_category
                )
            }
        }
    }
}

@Composable
fun EventCard(eventName: String, location: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = eventName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
