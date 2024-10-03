package com.example.melapp.Backend

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

data class Country(val country_name: String, val phone_code: String)

class MainActivity : ComponentActivity() {
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        setContent {
            MainScreen()
        }
    }

    @Composable
    fun MainScreen() {
        val context = LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Importing CSV data...",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LaunchedEffect(Unit) {
            insertCountriesFromCSV(context)
        }
    }

    private suspend fun insertCountriesFromCSV(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("countries_phone_codes.csv")
                val reader = BufferedReader(InputStreamReader(inputStream))

                // Saltar la primera línea (encabezados)
                reader.readLine()

                // Leer el archivo línea por línea
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val columns = line?.split(",")
                    if (columns != null && columns.size >= 2) {
                        val countryName = columns[0]
                        val phoneCode = columns[1]

                        // Crear un objeto Country
                        val country = Country(country_name = countryName, phone_code = phoneCode)

                        // Insertar en la colección "Country" de Firestore
                        firestore.collection("Country")
                            .add(country)
                            .addOnSuccessListener {
                                println("Documento añadido con éxito: $countryName")
                            }
                            .addOnFailureListener { e ->
                                println("Error al añadir el documento: $e")
                            }
                    }
                }
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}