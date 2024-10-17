

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.melapp.Backend.Evento
import com.example.melapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EventCardDescription(
    evento: Evento,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit, // Acción de cierre
    onSaveClick: (Boolean) -> Unit,
    onCardClick: () -> Unit
) {
    var isSaved by remember { mutableStateOf(false) }
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Obtener el email del usuario autenticado
    val user = FirebaseAuth.getInstance().currentUser
    val userEmail = user?.email

    // Comprobar si el evento ya está guardado en Firestore
    LaunchedEffect(evento.id, userEmail) {
        userEmail?.let { email ->
            firestore.collection("event_saved")
                .whereEqualTo("id_event", evento.id)
                .whereEqualTo("user_email", email)
                .get()
                .addOnSuccessListener { documents ->
                    isSaved = !documents.isEmpty
                }
                .addOnFailureListener {
                    // Manejar error
                }
        }
    }

    // La tarjeta ya no se controla con "isVisible" aquí
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Imagen del evento
            SubcomposeAsyncImage(
                model = evento.event_thumbnail,
                contentDescription = "Event Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterVertically) // Centra la imagen verticalmente
                    .padding(8.dp) // Aplica el mismo padding en todos los lados
                    .clip(RoundedCornerShape(8.dp)) // Bordes redondeados con un radio de 8dp (puedes ajustar según tu preferencia)
            ) {
                val painterState = painter.state
                if (painterState is AsyncImagePainter.State.Loading || painterState is AsyncImagePainter.State.Error) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    SubcomposeAsyncImageContent()
                }
            }


            // Información del evento
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = evento.event_name ?: "Evento sin título",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = evento.event_description ?: "Sin descripción",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = evento.event_location ?: "Ubicación no disponible",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Fecha: ${evento.event_date ?: "No disponible"} | ${evento.event_start_time ?: ""} - ${evento.event_end_time ?: ""}",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Categoría: ${evento.event_category ?: "No especificada"} | Precio: ${evento.event_price_range ?: "No disponible"}",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Columna con los botones de cerrar y guardar
            Column(
                modifier = Modifier.align(Alignment.Top),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón de cierre
                IconButton(onClick = {
                    onCloseClick() // Ejecutar cualquier acción adicional al cerrar
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_closedcross),
                        contentDescription = "Cerrar",
                        tint = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                // Botón de guardar
                IconButton(onClick = {
                    userEmail?.let { email ->
                        if (isSaved) {
                            firestore.collection("event_saved")
                                .whereEqualTo("id_event", evento.id)
                                .whereEqualTo("user_email", email)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        document.reference.delete()
                                    }
                                    isSaved = false
                                    Toast.makeText(context, "Evento eliminado de favoritos", Toast.LENGTH_SHORT).show()
                                    onSaveClick(isSaved)
                                }
                        } else {
                            val eventData = hashMapOf(
                                "id_event" to evento.id,
                                "user_email" to email
                            )
                            firestore.collection("event_saved").add(eventData)
                                .addOnSuccessListener {
                                    isSaved = true
                                    Toast.makeText(context, "Evento agregado a favoritos", Toast.LENGTH_SHORT).show()
                                    onSaveClick(isSaved)
                                }
                        }
                    } ?: run {
                        Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bookmark),
                        contentDescription = "Guardar evento",
                        tint = if (isSaved) Color(0xFF1A237E) else Color.Gray
                    )
                }
            }
        }
    }
}

