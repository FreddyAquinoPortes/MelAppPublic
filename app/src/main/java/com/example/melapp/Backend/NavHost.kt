// NavHost.kt
package com.example.melapp.Backend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.melapp.Screens.EditProfileScreen
import com.example.melapp.Screens.EventFormScreen
import com.example.melapp.Screens.EventListScreen
import com.example.melapp.Screens.EventoDetailsScreen
import com.example.melapp.Screens.EventosFavoritosScreen
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("event_form/{eventoId}",
            arguments = listOf(navArgument("eventoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getString("eventoId")
            if (eventoId != null) {
                EventFormScreen(navController, eventoId)
            } else {
                // Manejar el caso donde eventoId es null
                navController.navigate("event_form") // Navegar a la ruta general
            }
        }
        composable("home") {
            HomePage() // Asegúrate de pasar el navController si es necesario
        }
        composable("splash_screen") {
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
        composable("event_form") { // Ruta para crear un nuevo evento
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
            EditProfileScreen(navController = navController)
        }
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
                //navController.popBackStack()
            }
        }
        composable("ReportProblemScreen") {
            ReportProblemScreen(navController)
        }
        composable("half_signup_screen") {
            HalfSignUpScreen(navController)
        }
        composable("event_list") { // Nueva ruta para EventListScreen
            EventListScreen(navController)
        }
        composable("fav_events") { // Nueva ruta para EventListScreen
            EventosFavoritosScreen(navController)
        }
    }
}
