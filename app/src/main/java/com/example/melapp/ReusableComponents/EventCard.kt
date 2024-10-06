package com.example.melapp.ReusableComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.melapp.R

@Composable
fun EventCardDescription(
    imageResource: Int, // Asegúrate de que este es el parámetro que estás usando en todo el código.
    eventName: String,
    eventDescription: String,
    eventLocation: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Miniatura del evento
            Image(
                painter = painterResource(id = imageResource), // Usando imageResource aquí
                contentDescription = "Event Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            )
            // Información del evento (nombre, descripción, ubicación)
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = eventName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = eventDescription,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = eventLocation,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewEventCard() {
    EventCardDescription(
        imageResource = R.drawable.img_juan_luis, // Reemplaza con tu recurso de imagen
        eventName = "Juan Luis Tour 2024",
        eventDescription = "Disfruta del merengue y bachata clasica dominicana",
        eventLocation = "Casa de Teatro, Santo Domingo"
    )
}
