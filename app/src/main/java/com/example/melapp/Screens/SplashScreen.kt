package com.example.melapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.melapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Launch a coroutine that delays the splash screen for 1500ms
    LaunchedEffect(Unit) {
        delay(1500L)
        // Navigate to the next screen or perform any action after the delay
        navController.navigate("login") {
            // This popUpTo ensures the splash screen is removed from the back stack
            popUpTo("splash_Screen") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.mel_logo_removebg_preview__1_), // Replace with your logo resource
            contentDescription = "App Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(300.dp) // Adjust the size of the logo
        )
        Spacer(modifier = Modifier.height(40.dp))
        Image(
            painter = painterResource(id = R.drawable.mel_logo_text), // Reemplaza el texto por la imagen
            contentDescription = null,
            modifier = Modifier.size(100.dp) // Ajusta el tamaño según sea necesario
        )
    }
    }
}
