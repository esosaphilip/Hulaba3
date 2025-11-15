package com.example.hulaba3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.supabase.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
 
class AuthViewModel(
    private val authService: AuthService
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<com.example.hulaba3.data.database.User?>(null)
    val currentUser: StateFlow<com.example.hulaba3.data.database.User?> = _currentUser.asStateFlow()
    
    init {
        checkAuthenticationStatus()
    }
    
    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            authService.checkAuthenticationStatus()
            authService.currentUser.collect { user ->
                _currentUser.value = user
                _authState.value = if (user != null) {
                    AuthState.Authenticated(user)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }
    }
    
    fun signUp(email: String, password: String, username: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authService.signUp(email, password, username)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState.Authenticated(user)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign up failed")
                }
            )
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authService.signIn(email, password)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState.Authenticated(user)
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign in failed")
                }
            )
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authService.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }
    
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authService.resetPassword(email)
            result.fold(
                onSuccess = {
                    _authState.value = AuthState.PasswordResetSent
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Password reset failed")
                }
            )
        }
    }
    
    fun updateUserProfile(updates: Map<String, Any>) {
        viewModelScope.launch {
            val result = authService.updateUserProfile(updates)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Profile update failed")
                }
            )
        }
    }
    
    fun refreshUser() {
        viewModelScope.launch {
            authService.refreshUser()
        }
    }
    
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: com.example.hulaba3.data.database.User) : AuthState()
    data class Error(val message: String) : AuthState()
    object PasswordResetSent : AuthState()
}