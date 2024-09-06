package com.example.melapp

import com.example.melapp.Backend.validateEmail
import com.example.melapp.Backend.validateEmailAndPassword
import com.example.melapp.Backend.validatePassword



import org.junit.Assert.*
import org.junit.Test

class BackendUtilsTest {

    @Test
    fun validateEmail_validEmail_returnsTrue() {
        val email = "test@example.com"
        assertTrue(validateEmail(email))
    }

    @Test
    fun validateEmail_invalidEmail_returnsFalse() {
        val email = "invalid-email"
        assertFalse(validateEmail(email))
    }

    @Test
    fun validatePassword_validPassword_returnsNull() {
        val password = "Valid123!"
        assertNull(validatePassword(password))
    }

    @Test
    fun validatePassword_shortPassword_returnsError() {
        val password = "Short1!"
        assertEquals("La contraseña debe tener al menos 8 caracteres.", validatePassword(password))
    }

    @Test
    fun validatePassword_noUpperCase_returnsError() {
        val password = "lowercase1!"
        assertEquals("La contraseña debe contener al menos una letra mayúscula.", validatePassword(password))
    }

    @Test
    fun validatePassword_noDigit_returnsError() {
        val password = "Password!"
        assertEquals("La contraseña debe contener al menos un número.", validatePassword(password))
    }

    @Test
    fun validatePassword_noSpecialChar_returnsError() {
        val password = "Password1"
        assertEquals("La contraseña debe contener al menos un carácter especial (ej: >!@#).", validatePassword(password))
    }

    @Test
    fun validateEmailAndPassword_validInput_returnsNull() {
        val email = "test@example.com"
        val password = "Valid123!"
        assertNull(validateEmailAndPassword(email, password))
    }

    @Test
    fun validateEmailAndPassword_invalidEmail_returnsError() {
        val email = "invalid-email"
        val password = "Valid123!"
        assertEquals("Correo inválido", validateEmailAndPassword(email, password))
    }

    @Test
    fun validateEmailAndPassword_invalidPassword_returnsPasswordError() {
        val email = "test@example.com"
        val password = "short1!"
        assertEquals("La contraseña debe tener al menos 8 caracteres.", validateEmailAndPassword(email, password))
    }
}
