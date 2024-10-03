package com.example.melapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.melapp.Backend.AppNavigation
import com.example.melapp.Screens.LoginScreen
import com.example.melapp.Screens.SignUpScreen
import com.example.melapp.Screens.SplashScreen
import com.example.melapp.Screens.TradicionalLoginScreen
import com.example.melapp.ui.theme.MelAppTheme
import com.google.firebase.FirebaseApp

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.melapp.Backend.EventoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Inicializar Firebase
        enableEdgeToEdge()
        setContent {
            MelAppTheme {


                AppNavigation()


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MelAppTheme {

        AppNavigation()
    }
}