package com.example.melapp.Backend

import android.content.Context
import android.location.Geocoder
import android.util.Log
import java.util.Locale

fun getAddressFromLatLng(context: Context, lat: Double, lng: Double): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val streetAddress = address.getAddressLine(0)  // Dirección completa
            val city = address.locality  // Ciudad
            val state = address.adminArea  // Estado o provincia
            val country = address.countryName  // País
            "$streetAddress, $city, $state, $country"  // Retorna la dirección completa
        } else {
            "Dirección no encontrada"
        }
    } catch (e: Exception) {
        Log.e("GeocoderError", "Error al obtener la dirección: ${e.message}")
        "Error al obtener dirección"
    }
}
