package com.example.expensetracker.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val jwtManager: JwtManager
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    fun authenticate(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                jwtManager.saveToken(response.token)
                _authState.value = AuthState.Authenticated(response.token)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            }
        }
    }

    fun authenticateWithBiometrics() {
        viewModelScope.launch {
            try {
                val token = jwtManager.getToken()
                if (token != null && jwtManager.isTokenValid()) {
                    _authState.value = AuthState.Authenticated(token)
                } else {
                    _authState.value = AuthState.Error("Session expired")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Biometric auth failed")
            }
        }
    }
}