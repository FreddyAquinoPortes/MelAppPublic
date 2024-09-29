package com.example.melapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    ) {
        Column(
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
            Text(
                text = "Mel",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold, // Make the text bold
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
