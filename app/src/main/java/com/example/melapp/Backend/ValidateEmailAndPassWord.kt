package com.example.melapp.Backend

import android.util.Patterns

fun validateEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validatePassword(password: String): String? {
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { it in "!@#\$%^&*()-+=<>?/" }
    val hasMinLength = password.length >= 8

    return when {
        !hasMinLength -> "La contraseña debe tener al menos 8 caracteres."
        !hasUpperCase -> "La contraseña debe contener al menos una letra mayúscula."
        !hasDigit -> "La contraseña debe contener al menos un número."
        !hasSpecialChar -> "La contraseña debe contener al menos un carácter especial (ej: >!@#)."
        else -> null
    }
}

fun validateEmailAndPassword(email: String, password: String): String? {
    if (!validateEmail(email)) {
        return "Correo inválido"
    }
    return validatePassword(password)
}