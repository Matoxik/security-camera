package com.macieandrz.securitycamera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.UnAuthenticated)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthStatus()
    }


    fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = if (auth.currentUser == null) {
                AuthState.UnAuthenticated
            } else {
                AuthState.Authenticated
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty()) {
                _authState.value = AuthState.Error("Email or password can't be empty")
                return@launch
            }

            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login error")
            }
        }
    }

    fun signup(email: String, password: String, repeatPassword: String) {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty()) {
                _authState.value = AuthState.Error("Email or password can't be empty")
                return@launch
            }

            if (repeatPassword != password) {
                _authState.value = AuthState.Error("Password and repeated password are not the same")
                return@launch
            }

            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Signup error")
            }
        }
    }

    fun signout() {
        viewModelScope.launch {
            auth.signOut()
            _authState.value = AuthState.UnAuthenticated
        }
    }
}

sealed class AuthState {
    object Authenticated : AuthState() // User is logged in
    object UnAuthenticated : AuthState() // User is not logged in
    object Loading : AuthState() // Authentication process is ongoing
    data class Error(val message: String) : AuthState()
}
