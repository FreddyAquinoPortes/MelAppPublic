package com.example.melapp.Backend

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.type.Date

// Data class para agrupar datos de usuario
data class User(
    val nombres: String,
    val apellidos: String,
    val fechaNacimiento: java.util.Date,
    val genero: Int,
    val username: String,
    val rol: Int = 0,
    val accountStatus: Int = 0,
    val location: GeoPoint
)

// Clase para manejar la interacción con Firestore
class FirestoreHelper(private val db: FirebaseFirestore) {

    fun checkUserExists(
        email: String,
        onUserExists: () -> Unit,
        onUserDoesNotExist: (GoogleSignInAccount) -> Unit,
        account: GoogleSignInAccount
    ) {
        val usersCollection = db.collection("users")
        val query = usersCollection.whereEqualTo("correo", email)

        query.get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                // Usuario no existe
                onUserDoesNotExist(account)
            } else {
                // Usuario ya existe
                onUserExists()
            }
        }.addOnFailureListener {
            println("Error al verificar si el usuario existe: ${it.message}")
        }
    }

    fun registerNewUser(
        account: GoogleSignInAccount,
        user: User
    ) {
        val userData = hashMapOf(
            "Nombres" to user.nombres,
            "Apellidos" to user.apellidos,
            "Fecha de nacimiento" to user.fechaNacimiento,
            "Genero" to user.genero,
            "correo" to account.email,
            "Username" to "@${user.username}",
            "rol" to user.rol,
            "accountStatus" to user.accountStatus,
            "location" to user.location
        )

        db.collection("users").document(account.email ?: "").set(userData)
            .addOnSuccessListener {
                println("Usuario registrado exitosamente.")
            }.addOnFailureListener { e ->
                println("Error al registrar usuario: ${e.message}")
            }
    }
}

// Clase para manejar la autenticación de Google
// Clase para manejar la autenticación de Google
class GoogleSignInHelper(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val navController: NavController
) {
    private val googleSignInClient: GoogleSignInClient
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.example.melapp.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(result: ActivityResult) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                // Extraer la información de la cuenta de Google
                val nombres = it.givenName ?: "Nombre" // Nombre del usuario
                val apellidos = it.familyName ?: "Apellido" // Apellido del usuario
                val email = it.email ?: "correo@example.com" // Correo electrónico
                val username = it.displayName ?: "usuario" // Nombre de usuario, puede ser el nombre completo
                val fechaNacimiento =
                    java.util.Date(2000, 1, 1) // Suponiendo que no está disponible en Google

                // Crear un nuevo objeto User con los datos de la cuenta de Google
                val updatedUser = User(
                    nombres = nombres,
                    apellidos = apellidos,
                    fechaNacimiento = fechaNacimiento, // Puedes ajustar este valor si tienes un método para obtener la fecha real
                    genero = 0, // El género puede ser ajustado de otra forma si tienes esa información
                    username = username,
                    rol = 0,
                    accountStatus = 0,
                    location = GeoPoint(0.0, 0.0) // Se actualizará después de obtener la ubicación
                )

                // Obtener la ubicación y proceder a registrar el usuario
                getLocationAndRegisterUser(it, updatedUser)
            }
        } catch (e: ApiException) {
            println("Google sign-in failed: ${e.message}")
        }
    }

    private fun getLocationAndRegisterUser(account: GoogleSignInAccount, user: User) {
        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as android.app.Activity),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        // Obtener la ubicación
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                val updatedUser = user.copy(location = geoPoint)
                checkUserInFirestore(account, updatedUser)
            } else {
                println("Error al obtener la ubicación.")
            }
        }
    }

    private fun checkUserInFirestore(account: GoogleSignInAccount, user: User) {
        val firestoreHelper = FirestoreHelper(FirebaseFirestore.getInstance())
        firestoreHelper.checkUserExists(
            email = account.email ?: "",
            onUserExists = {
                firebaseAuthWithGoogle(account) // Si el usuario ya existe, inicia sesión normalmente
            },
            onUserDoesNotExist = {
                // Registrar nuevo usuario
                firestoreHelper.registerNewUser(account, user)
                firebaseAuthWithGoogle(account) // Iniciar sesión después de registrar
            },
            account = account
        )
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        println("Sign-in successful: ${user.email}")
                        navController.navigate("map") {
                            popUpTo("event_form") { inclusive = true }
                        }
                    } else {
                        println("Error: User is null after sign-in.")
                    }
                } else {
                    println("Firebase Google sign-in failed: ${task.exception?.message}")
                }
            }
    }
}

    fun handleSignInResult(result: ActivityResult) {

    }


