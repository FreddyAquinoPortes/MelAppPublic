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
import androidx.compose.material3.MaterialTheme
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Miniatura del evento",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable(onClick = onEventImageClick)
                .align(Alignment.Start)
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
                    contentDescription = "Select Event thumbnail Image",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Imágenes Adicionales",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable(onClick = onAdditionalImagesClick)
                .align(Alignment.Start)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
                .clickable(onClick = onAdditionalImagesClick),
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
