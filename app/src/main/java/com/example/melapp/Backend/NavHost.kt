package com.example.melapp.Backend


import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.melapp.ReusableComponents.NavigationBottomBar
import com.example.melapp.Screens.EventFormScreen
import com.example.melapp.Screens.HelpScreen
import com.example.melapp.Screens.HomePage
import com.example.melapp.Screens.LoginScreen
import com.example.melapp.Screens.PasswordRecoveryScreen
import com.example.melapp.Screens.SignUpScreen
import com.example.melapp.Screens.SplashScreen
import com.example.melapp.Screens.TradicionalLoginScreen
import com.example.melapp.Screens.MapScreen
import com.example.melapp.Screens.ProfileScreen
import com.example.melapp.Screens.RegistrationSuccessScreen
import com.example.melapp.Screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("home"){ HomePage()}
        composable("splash_screen"){
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("traditional_login") {
            TradicionalLoginScreen(navController)
        }
        composable("register") {
            SignUpScreen(navController)
        }
        composable("map") {
            MapScreen(navController)
        }
        composable("passwordRecovery") {
            PasswordRecoveryScreen(navController = navController)
        }
        composable("event_form") { // Agregamos la nueva ruta para el formulario de eventos
            EventFormScreen(navController)
        }
        composable("settingsScreen") { SettingsScreen(navController) }
        composable("helpScreen") { HelpScreen(navController) }
        composable("profileScreen") { ProfileScreen(navController) }


    }
}