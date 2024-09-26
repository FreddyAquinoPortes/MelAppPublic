package com.example.melapp.ReusableComponents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.melapp.R

@Composable
fun NavigationBottomBar(
    navController: NavController,
    onPostEventClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0xFF1A237E), // Morado oscuro
        contentColor = Color.White,
        tonalElevation = 4.dp,
        contentPadding = PaddingValues(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left icon - Profile (For future use)
            IconButton(onClick = { /* Navigate to Profile */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user), // Replace with actual drawable
                    contentDescription = "User Profile",
                    tint = Color.White
                )
            }

            // Center icon - Map Screen
            IconButton(onClick = { navController.navigate("mapScreen") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_earth), // Replace with actual drawable
                    contentDescription = "Map Screen",
                    tint = Color.White
                )
            }

            // Right icon - Settings Screen
            IconButton(onClick = { navController.navigate("settingsScreen") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings), // Replace with actual drawable
                    contentDescription = "Settings Screen",
                    tint = Color.White
                )
            }
        }
    }
}
