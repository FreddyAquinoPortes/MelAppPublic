package com.example.melapp.Screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.melapp.Backend.GoogleSignInHelper
import com.example.melapp.Backend.SocialSignInHelper
import com.example.melapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavController) {
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() } // Instancia de FirebaseFirestore

    BackHandler {
        // No hacemos nada aquí para bloquear el botón de retroceso
    }
    // Instancia de GoogleSignInHelper para manejar la autenticación de Google
    val googleSignInHelper = remember {
        GoogleSignInHelper(context, auth, db, navController) // Se pasa FirebaseFirestore y NavController
    }

    // Callback para el resultado de la actividad de inicio de sesión
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleSignInHelper.handleSignInResult(result) // Se elimina el paso del objeto User
    }
    val socialSignInHelper = SocialSignInHelper(
        context = context,
        auth = FirebaseAuth.getInstance(),
        db = FirebaseFirestore.getInstance(),
        navController = navController
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Logo y Título
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.mellogo),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(top = 40.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.mel_logo_text), // Reemplaza el texto por la imagen
                contentDescription = null,
                modifier = Modifier
                    .size(75.dp) // Ajusta el tamaño según sea necesario
                    .padding(vertical = 16.dp)
            )
        }


        // Opciones de inicio de sesión
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginOption(
                iconResId = R.drawable.ic_google,
                text = "Iniciar con Google",
                backgroundColor = Color(0xFFA4C639),
                onClick = {
                    val signInIntent = googleSignInHelper.getSignInIntent()
                    signInLauncher.launch(signInIntent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginOption(
                iconResId = R.drawable.ic_facebook,
                text = "Iniciar con Facebook",
                backgroundColor = Color(0xFF4C6A92),
                onClick = {
                    socialSignInHelper.startFacebookLogin()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginOption(
                iconResId = R.drawable.ic_appleinc,
                text = "Iniciar con  Apple",
                backgroundColor = Color(0xFF7289DA)
            )
        }

        // Botones de pie de página
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("traditional_login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Registrarse")
            }
        }
    }
}

@Composable
fun LoginOption(iconResId: Int, text: String, backgroundColor: Color, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = Color.White, // Cambia el color del ícono a blanco
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
