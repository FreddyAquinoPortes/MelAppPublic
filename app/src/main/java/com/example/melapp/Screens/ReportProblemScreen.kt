package com.example.melapp.Screens

import androidx.compose.foundation.layout.* // Para layouts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportProblemScreen(navController: NavController) {
    // Estado para los campos de título y descripción
    var problemTitle by remember { mutableStateOf("") }
    var problemDescription by remember { mutableStateOf("") }
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    // Estado para manejar el Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar un Problema") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Para mostrar Snackbar
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Campo para el título del problema
                OutlinedTextField(
                    value = problemTitle,
                    onValueChange = { problemTitle = it },
                    label = { Text("Título del Problema") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para la descripción del problema
                OutlinedTextField(
                    value = problemDescription,
                    onValueChange = { problemDescription = it },
                    label = { Text("Descripción del Problema") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón para enviar el reporte
                Button(onClick = {
                    if (user != null) {
                        // Enviar el reporte a Firestore
                        val report = hashMapOf(
                            "user_email" to user.email,
                            "problem_title" to problemTitle,
                            "problem_description" to problemDescription,
                            "timestamp" to Date()
                        )
                        db.collection("problem_reports")
                            .add(report)
                            .addOnSuccessListener {
                                // Mostrar mensaje de éxito
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Reporte enviado exitosamente")
                                }
                                // Navegar hacia atrás después de enviar
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                // Mostrar mensaje de error
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Error al enviar el reporte: ${e.message}")
                                }
                            }
                    }
                }) {
                    Text("Enviar Reporte")
                }
            }
        }
    )
}

