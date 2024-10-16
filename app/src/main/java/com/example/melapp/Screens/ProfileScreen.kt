package com.example.melapp.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    // State to hold user_name, email, profile image URL, and role
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var profileImageUrl by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(0) } // Rol del usuario (0 = normal, 1 = organizador)

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
                        rol = document.getLong("rol")?.toInt() ?: 0 // Obtener el rol del usuario

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
                onProfileClick = { navController.navigate("profileScreen") },
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
                            onClick = {navController.navigate("fav_events") },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bookmark),
                                contentDescription = "Icono de eventos guardados"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Ver Eventos Favoritos", maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mostrar si el usuario es organizador de eventos
                if (rol == 1) {
                    Text(text = "Perfil de organizador",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(16.dp))
                }

                // Mostrar el botón de "Mis eventos" solo si el rol es 1
                if (rol == 1) {
                    Button(
                        onClick = {
                            // Navegar a la pantalla de eventos creados por el usuario
                            navController.navigate("event_list")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = "Mis eventos")
                    }
                }
            }
        }
    }
}


