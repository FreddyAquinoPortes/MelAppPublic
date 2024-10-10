import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun EventCardDescription(
    event_thumbnail: String, // Este será el URL de Firebase Storage
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
            // Cargar miniatura desde Firebase Storage usando Coil
            Image(
                painter = rememberAsyncImagePainter(model = event_thumbnail), // URL de Firebase Storage
                contentDescription = "Event Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            )

            // Información del evento (nombre, descripción, ubicación)
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(0.7f) // Controlar el ancho para evitar que el texto se desborde
            ) {
                Text(
                    text = eventName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1, // Limitar a una línea
                    overflow = TextOverflow.Ellipsis // Mostrar "..." si es demasiado largo
                )
                Text(
                    text = eventDescription,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp),
                    maxLines = 2, // Limitar la descripción a 2 líneas
                    overflow = TextOverflow.Ellipsis // Mostrar "..." si es demasiado larga
                )
                Text(
                    text = eventLocation,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 1, // Limitar la ubicación a una línea
                    overflow = TextOverflow.Ellipsis // Mostrar "..." si es demasiado largo
                )
            }
        }
    }
}








