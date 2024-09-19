package com.example.melapp.Screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.maps.android.compose.GoogleMap
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.GoogleMap

@Composable
fun MapScreen() {
    GoogleMap(modifier = Modifier.fillMaxSize())
}

