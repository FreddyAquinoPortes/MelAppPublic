package com.example.melapp.Backend


import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.melapp.Screens.EditProfileScreen
import com.example.melapp.Screens.EventFormScreen
import com.example.melapp.Screens.EventoDetailsScreen
import com.example.melapp.Screens.HalfSignUpScreen
import com.example.melapp.Screens.HelpScreen
import com.example.melapp.Screens.HomePage
import com.example.melapp.Screens.LoginScreen
import com.example.melapp.Screens.MapScreen
import com.example.melapp.Screens.PasswordRecoveryScreen
import com.example.melapp.Screens.ProfileScreen
import com.example.melapp.Screens.RegistrationSuccessScreen
import com.example.melapp.Screens.ReportProblemScreen
import com.example.melapp.Screens.SelectLocationScreen
import com.example.melapp.Screens.SettingsScreen
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
        composable("home"){
            HomePage()
        }
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
        composable("settingsScreen") {
            SettingsScreen(navController)
        }
        composable("helpScreen") {
            HelpScreen(navController)
        }
        composable("profileScreen") {
            ProfileScreen(navController)
        }
        composable("editprofileScreen") {
            EditProfileScreen(navController = navController) }

        composable("registration_success") {
            RegistrationSuccessScreen(navController)
        }
        composable(
            "eventDetails/{eventoId}",
            arguments = listOf(navArgument("eventoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getString("eventoId") ?: return@composable
            EventoDetailsScreen(navController, eventoId)
        }
        composable("selectLocation") {
            SelectLocationScreen(navController = navController) { lat, lng ->
                // Pasar las coordenadas seleccionadas de vuelta a EventFormScreen
                navController.previousBackStackEntry?.savedStateHandle?.set("latitud", lat)
                navController.previousBackStackEntry?.savedStateHandle?.set("longitud", lng)
//                navController.popBackStack()
            }
        }
        composable("ReportProblemScreen") {
            ReportProblemScreen(navController)
        }
        composable("registration_success") {
            RegistrationSuccessScreen(navController)
        }
        composable("half_signup_screen") {
            HalfSignUpScreen(navController)
        }

    }
}