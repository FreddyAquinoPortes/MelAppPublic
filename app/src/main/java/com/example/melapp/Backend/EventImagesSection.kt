package com.example.melapp.Backend

// File: EventImagesComposable.kt

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.melapp.R

@Composable
fun EventImagesSection(
    eventImage: String?,
    additionalImageUri: Uri?,
    selectedImageUri: Uri?,
    onEventImageClick: () -> Unit,
    onAdditionalImagesClick: () -> Unit
) {
    /*Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sección para la imagen del evento
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Imagen del Evento",
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable(onClick = onEventImageClick)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
                    .clickable(onClick = onEventImageClick),
                contentAlignment = Alignment.Center
            ) {
                if (eventImage != null) {
                    AsyncImage(
                        model = eventImage,
                        contentDescription = "Event Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_images),
                        contentDescription = "Select Event Image",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }*/

        Spacer(modifier = Modifier.height(16.dp))

        // Sección para la imagen adicional
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Miniatura del evento",
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable(onClick = onEventImageClick)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
                    .clickable(onClick = onEventImageClick),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Event thumbnail Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_images),
                        contentDescription = "Select Event thumnail Image",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Imágenes Adicionales",
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable(onClick = onAdditionalImagesClick)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
                    .clickable(onClick = onEventImageClick),
                contentAlignment = Alignment.Center
            ) {
                if (additionalImageUri != null) {
                    AsyncImage(
                        model = additionalImageUri,
                        contentDescription = "Additional Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_images),
                        contentDescription = "Select Additional Image",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
