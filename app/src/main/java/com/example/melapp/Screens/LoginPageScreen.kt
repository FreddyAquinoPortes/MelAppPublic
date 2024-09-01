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

@Composable
fun LoginScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Logo y título en la parte superior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.mel_logo_removebg_preview__1_), // Asegúrate de tener tu logo aquí
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(top = 16.dp) // Ajusta el tamaño según sea necesario
            )
            Text(
                text = "Mel",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Opciones de inicio de sesión en el centro
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginOption(
                iconResId = R.drawable.vector, // Icono de Steam
                text = "login with steam",
                backgroundColor = Color(0xFF4C6A92) // Color personalizado
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginOption(
                iconResId = R.drawable.vector1, // Icono de Google
                text = "login with google",
                backgroundColor = Color(0xFFA4C639) // Color personalizado
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginOption(
                iconResId = R.drawable.vector2, // Icono de Discord
                text = "login with discord",
                backgroundColor = Color(0xFF7289DA) // Color personalizado
            )
        }

        // Botón de inicio de sesión o registro en el pie de página
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("traditional_login") }, // Navega a la pantalla de login tradicional
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Login/SignUp")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("register") }, // Navega a la pantalla de registro
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
fun LoginOption(iconResId: Int, text: String, backgroundColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
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
