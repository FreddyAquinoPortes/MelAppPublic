package com.example.melapp.Backend

// File: EventImagesComposable.kt
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.melapp.R

@Composable
fun EventImagesSection(
    eventImage: String?, // URL or path of the event image
    additionalImages: List<String>, // List of URLs or paths for additional images
    onEventImageClick: () -> Unit, // Lambda to handle click on the main image
    onAdditionalImagesClick: () -> Unit // Lambda to handle click on additional images
) {
    Spacer(modifier = Modifier.height(8.dp))

    // Main Event Image
    Text("Agregar Imagen Principal del Evento", style = MaterialTheme.typography.titleMedium)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onEventImageClick() },
        contentAlignment = Alignment.Center
    ) {
        if (eventImage != null) {
            Image(
                painter = rememberAsyncImagePainter(eventImage),
                contentDescription = "Imagen del evento",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_images),
                contentDescription = "Agregar imagen",
                modifier = Modifier.size(64.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Additional Images
    Text("Agregar Imágenes Adicionales del Evento", style = MaterialTheme.typography.titleMedium)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onAdditionalImagesClick() },
        contentAlignment = Alignment.Center
    ) {
        if (additionalImages.isNotEmpty()) {
            // Here you can customize to show a grid or list of selected images
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_images),
                contentDescription = "Agregar imágenes",
                modifier = Modifier.size(64.dp)
            )
        }
    }
}
