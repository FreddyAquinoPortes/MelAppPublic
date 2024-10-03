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
                val nombres = it.givenName ?: ""
                val apellidos = it.familyName ?: ""
                val email = it.email ?: "correo@example.com"
                val username = it.displayName ?: "usuario"

                // Valores predeterminados
                val fechaNacimiento = java.util.Date(2000, 1, 1) // Valor predeterminado
                val genero = 0 // Valor predeterminado
                val rol = 0
                val accountStatus = 0 // Por defecto 0 para los nuevos usuarios

                // Crear un objeto User con los valores de Google y predeterminados
                val newUser = User(
                    nombres = nombres,
                    apellidos = apellidos,
                    fechaNacimiento = fechaNacimiento,
                    genero = genero,
                    username = username,
                    rol = rol,
                    accountStatus = accountStatus,
                    location = GeoPoint(0.0, 0.0) // Se actualiza después de obtener la ubicación
                )

                // Obtener la ubicación y proceder a registrar o autenticar el usuario
                getLocationAndRegisterUser(it, newUser)
            }
        } catch (e: ApiException) {
            println("Google sign-in failed: ${e.message}")
        }
    }

    private fun getLocationAndRegisterUser(account: GoogleSignInAccount, user: User) {
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
                // Si el usuario existe, verificar el estado de la cuenta
                checkAccountStatusAndNavigate(account)
            },
            onUserDoesNotExist = {
                // Registrar nuevo usuario
                firestoreHelper.registerNewUser(account, user)
                firebaseAuthWithGoogle(account)
            },
            account = account
        )
    }

    private fun checkAccountStatusAndNavigate(account: GoogleSignInAccount) {
        val userEmail = account.email ?: return
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        usersCollection.document(userEmail).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val accountState = document.getLong("account_state") ?: 0
                if (accountState == 0L) {
                    // Redirigir a HalfSignUpScreen si el estado de la cuenta es 0
                    navController.navigate("half_signup_screen")
                } else {
                    // De lo contrario, redirigir a event_form
                    navController.navigate("map")
                }
            }
        }.addOnFailureListener {
            println("Error al verificar el estado de la cuenta: ${it.message}")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        println("Sign-in successful: ${user.email}")
                        // No hacemos la navegación aquí, ya se maneja en checkAccountStatusAndNavigate
                    } else {
                        println("Error: User is null after sign-in.")
                    }
                } else {
                    println("Firebase Google sign-in failed: ${task.exception?.message}")
                }
            }
    }
}

class FirestoreHelper(private val db: FirebaseFirestore) {

    // Verificar si el usuario ya existe en Firestore
    fun checkUserExists(
        email: String,
        onUserExists: () -> Unit,
        onUserDoesNotExist: () -> Unit,
        account: GoogleSignInAccount
    ) {
        db.collection("users").document(email).get().addOnSuccessListener { document ->
            if (document.exists()) {
                // El usuario ya existe
                onUserExists()
            } else {
                // El usuario no existe
                onUserDoesNotExist()
            }
        }.addOnFailureListener {
            println("Error al verificar la existencia del usuario: ${it.message}")
        }
    }

    // Registrar un nuevo usuario en Firestore
    fun registerNewUser(account: GoogleSignInAccount, user: User) {
        val Phonenumber = ""
        val userEmail = account.email ?: return
        val userMap = hashMapOf(
            "name" to user.nombres,
            "lastname" to user.apellidos,
            "birth_date" to user.fechaNacimiento,
            "gender" to user.genero,
            "user_name" to user.username,
            "rol" to user.rol,
            "account_state" to user.accountStatus,
            "user_location" to user.location,
            "email" to userEmail,
            "Phone_number" to Phonenumber
        )

        db.collection("users").document(userEmail).set(userMap).addOnSuccessListener {
            println("Usuario registrado exitosamente: $userEmail")
        }.addOnFailureListener {
            println("Error al registrar el usuario: ${it.message}")
        }
    }
}
