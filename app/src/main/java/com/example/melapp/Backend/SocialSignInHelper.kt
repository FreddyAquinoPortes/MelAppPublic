package com.example.melapp.Backend

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.Date



class SocialSignInHelper(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val navController: NavController
) {
    private val googleSignInClient: GoogleSignInClient
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val loginManager: LoginManager = LoginManager.getInstance()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.example.melapp.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun startFacebookLogin() {
        println("Iniciando login de Facebook")
        loginManager.logInWithReadPermissions(
            context as android.app.Activity,
            listOf("email", "public_profile")
        )
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                println("Login de Facebook exitoso")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                println("Login de Facebook cancelado")
            }

            override fun onError(error: FacebookException) {
                println("Error en login de Facebook: ${error.message}")
                error.printStackTrace()
            }
        })
    }
    class FirestoreHelper(private val db: FirebaseFirestore) {

        // Verificar si el usuario ya existe en Firestore
        fun checkUserExists(
            email: String,
            onUserExists: () -> Unit,
            onUserDoesNotExist: () -> Unit
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

        // Actualizar la última fecha de inicio de sesión
        fun updateLastSignIn(email: String) {
            val userRef = db.collection("users").document(email)
            userRef.update("user_last_signIn", Date()).addOnSuccessListener {
                println("Última fecha de inicio de sesión actualizada.")
            }.addOnFailureListener {
                println("Error al actualizar la última fecha de inicio de sesión: ${it.message}")
            }
        }

        // Registrar un nuevo usuario en Firestore
        fun registerNewUser(email: String, user: User) {
            val userMap = hashMapOf(
                "name" to user.nombres,
                "lastname" to user.apellidos,
                "birth_date" to user.fechaNacimiento,
                "gender" to user.genero,
                "user_name" to user.username,
                "rol" to user.rol,
                "account_state" to user.accountStatus,
                "user_location" to user.location,
                "email" to email,
                "user_registration_date" to Date(),
                "user_last_signIn" to Date()
            )

            db.collection("users").document(email).set(userMap).addOnSuccessListener {
                println("Usuario registrado exitosamente: $email")
            }.addOnFailureListener {
                println("Error al registrar el usuario: ${it.message}")
            }
        }
    }
    fun handleGoogleSignInResult(result: ActivityResult) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                val user = createUserFromGoogleAccount(it)
                getLocationAndRegisterUser(user, it.email ?: "")
            }
        } catch (e: ApiException) {
            println("Google sign-in failed: ${e.message}")
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        println("Manejando token de Facebook: ${token.token}")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Autenticación de Firebase exitosa")
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        println("Usuario de Firebase no es nulo")
                        val user = createUserFromFirebaseUser(firebaseUser)
                        getLocationAndRegisterUser(user, firebaseUser.email ?: "")
                    } else {
                        println("Error: Usuario de Firebase es nulo después de la autenticación")
                    }
                } else {
                    println("Autenticación de Firebase falló: ${task.exception?.message}")
                    task.exception?.printStackTrace()
                }
            }
    }

    private fun createUserFromGoogleAccount(account: GoogleSignInAccount): User {
        return User(
            nombres = account.givenName ?: "",
            apellidos = account.familyName ?: "",
            fechaNacimiento = Date(2000, 1, 1),
            genero = 0,
            username = account.displayName ?: "usuario",
            rol = 0,
            accountStatus = 0,
            location = GeoPoint(0.0, 0.0)
        )
    }

    private fun createUserFromFirebaseUser(user: com.google.firebase.auth.FirebaseUser): User {
        val nombres = user.displayName?.split(" ")?.firstOrNull() ?: ""
        val apellidos = user.displayName?.split(" ")?.lastOrNull() ?: ""
        return User(
            nombres = nombres,
            apellidos = apellidos,
            fechaNacimiento = Date(2000, 1, 1),
            genero = 0,
            username = user.displayName ?: "usuario",
            rol = 0,
            accountStatus = 0,
            location = GeoPoint(0.0, 0.0)
        )
    }

    private fun getLocationAndRegisterUser(user: User, email: String) {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as android.app.Activity),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                val updatedUser = user.copy(location = geoPoint)
                checkUserInFirestore(email, updatedUser)
            } else {
                println("Error al obtener la ubicación.")
                checkUserInFirestore(email, user)
            }
        }
    }

    private fun checkUserInFirestore(email: String, user: User) {
        val firestoreHelper = FirestoreHelper(db)
        firestoreHelper.checkUserExists(
            email = email,
            onUserExists = {
                println("Usuario existe en Firestore")
                firestoreHelper.updateLastSignIn(email)
                checkAccountStatusAndNavigate(email)
            },
            onUserDoesNotExist = {
                println("Usuario no existe en Firestore, registrando...")
                firestoreHelper.registerNewUser(email, user)
                checkAccountStatusAndNavigate(email)
            }
        )
    }

    private fun checkAccountStatusAndNavigate(email: String) {
        db.collection("users").document(email).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val accountState = document.getLong("account_state") ?: 0
                if (accountState == 0L) {
                    navController.navigate("half_signup_screen")
                } else {
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
                    } else {
                        println("Error: User is null after sign-in.")
                    }
                } else {
                    println("Firebase Google sign-in failed: ${task.exception?.message}")
                }
            }
    }
    fun onError(error: FacebookException) {
        println("Error en login de Facebook: ${error.message}")
        error.printStackTrace()
    }
    
}
