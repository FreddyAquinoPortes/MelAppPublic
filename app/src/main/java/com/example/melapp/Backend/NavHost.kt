package com.example.melapp.Backend

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.melapp.Screens.HomePage
import com.example.melapp.Screens.LoginScreen
import com.example.melapp.Screens.PasswordRecoveryScreen
import com.example.melapp.Screens.SignUpScreen
import com.example.melapp.Screens.SplashScreen
import com.example.melapp.Screens.TradicionalLoginScreen

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
        composable("passwordRecovery") {
            PasswordRecoveryScreen(navController = navController)
        }
    }
}