package com.example.melapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.melapp.R
import androidx.navigation.NavController
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(navController: NavController) {
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current

    // Create Google SignIn options
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Aquí se usa el ID del cliente web
            .requestEmail()
            .build()
    }

    // Create Google SignIn client
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Callback for Google Sign-In result
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                firebaseAuthWithGoogle(it, auth, navController)
            }
        } catch (e: ApiException) {
            // Handle sign-in error
            println("Google sign-in failed: ${e.message}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Logo and Title
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
            Text(
                text = "Mel",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Login Options
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginOption(
                iconResId = R.drawable.vector1,
                text = "Login with Google",
                backgroundColor = Color(0xFFA4C639),
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    signInLauncher.launch(signInIntent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginOption(
                iconResId = R.drawable.vector,
                text = "Login with Steam",
                backgroundColor = Color(0xFF4C6A92)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginOption(
                iconResId = R.drawable.vector2,
                text = "Login with Discord",
                backgroundColor = Color(0xFF7289DA)
            )
        }

        // Footer buttons
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
                Text(text = "Login/SignUp")
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
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
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

// Function to authenticate Firebase with Google account
private fun firebaseAuthWithGoogle(
    account: GoogleSignInAccount,
    auth: FirebaseAuth,
    navController: NavController
) {
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Check if the user is signed in
                val user = auth.currentUser
                if (user != null) {
                    println("Sign-in successful: ${user.email}")
                    // Navegar a la pantalla principal
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // Elimina la pantalla de login del stack
                    }
                } else {
                    println("Error: User is null after sign-in.")
                }
            } else {
                // Log detailed error message
                println("Firebase Google sign-in failed: ${task.exception?.message}")
            }
        }
}
