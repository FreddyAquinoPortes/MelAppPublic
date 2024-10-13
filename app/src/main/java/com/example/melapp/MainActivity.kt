package com.example.melapp

import android.content.Context
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
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        FirebaseApp.initializeApp(this) // Inicializar Firebase

        enableEdgeToEdge()
        setContent {
            MelAppTheme {


                AppNavigation()


            }
        }
    }
}

private fun Any.activateApp(context: Context) {

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MelAppTheme {

        AppNavigation()
    }
}