package com.healthconnect.app.utils

import android.util.Patterns

object Validators {
    fun isValidEmail(value: String) = Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()
    fun isValidPassword(value: String) = value.length >= 6
    fun isValidName(value: String) = value.trim().length >= 2
    fun passwordsMatch(a: String, b: String) = a == b
    fun validationError(name: String, email: String, password: String, confirmPassword: String): String? = when {
        !isValidName(name) -> "Please enter your full name."
        !isValidEmail(email) -> "Please enter a valid email address."
        !isValidPassword(password) -> "Password must be at least 6 characters."
        !passwordsMatch(password, confirmPassword) -> "Passwords do not match."
        else -> null
    }
}
