package com.drdisagree.rushly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.rushly.utils.validations.LoginRegisterValidation
import com.drdisagree.rushly.utils.Resource
import com.drdisagree.rushly.utils.validations.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    private val _authenticated = MutableSharedFlow<Resource<String>>()
    val authenticated = _authenticated.asSharedFlow()

    fun login(email: String, password: String) {
        if (checkValidation(email, password)) {
            viewModelScope.launch {
                _login.emit(Resource.Loading())
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    firebaseAuth.currentUser?.isEmailVerified?.let { isEmailVerified ->
                        if (isEmailVerified) {
                            viewModelScope.launch {
                                it.user?.let {
                                    _login.emit(Resource.Success(it))
                                }
                            }
                        } else {
                            viewModelScope.launch {
                                _login.emit(Resource.Error("Please verify your email"))
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _login.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else {
            viewModelScope.launch {
                var errorSent = false
                if (email.isEmpty()) {
                    _login.emit(Resource.Error("Email cannot be empty"))
                    errorSent = true
                }
                if (password.isEmpty()) {
                    _login.emit(Resource.Error("Password cannot be empty"))
                    errorSent = true
                }
                if (!errorSent) {
                    _login.emit(Resource.Error("Invalid email or password"))
                }
            }
        }
    }

    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = password.isNotEmpty() && password.length >= 8
        return emailValidation is LoginRegisterValidation.Valid && passwordValidation
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success("Password reset email sent"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun resendVerificationMail() {
        viewModelScope.launch {
            _authenticated.emit(Resource.Loading())
        }

        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                viewModelScope.launch {
                    _authenticated.emit(Resource.Success("Verification email sent"))
                }
            }
            ?.addOnFailureListener {
                viewModelScope.launch {
                    _authenticated.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}