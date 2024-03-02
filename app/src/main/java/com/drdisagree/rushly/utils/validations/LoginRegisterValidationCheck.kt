package com.drdisagree.rushly.utils.validations

import android.util.Patterns

fun validateEmail(email: String): LoginRegisterValidation {
    return if (email.isEmpty()) {
        LoginRegisterValidation.Invalid("Email cannot be empty")
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        LoginRegisterValidation.Invalid("Invalid email format")
    } else {
        LoginRegisterValidation.Valid
    }
}

fun validatePassword(password: String): LoginRegisterValidation {
    return if (password.isEmpty()) {
        LoginRegisterValidation.Invalid("Password cannot be empty")
    } else if (password.length < 8) {
        LoginRegisterValidation.Invalid("Password must be at least 8 characters")
    } else {
        LoginRegisterValidation.Valid
    }
}