package com.example.hulaba3.data.supabase

import com.example.hulaba3.data.database.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
 
class AuthService(
    private val supabaseClient: SupabaseClient
) {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    suspend fun signUp(email: String, password: String, username: String): Result<User> {
        return try {
            val user = User(
                id = "local_${System.nanoTime()}",
                email = email,
                username = username,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            _currentUser.value = user
            _isAuthenticated.value = true
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            // Minimal local sign-in: create or return existing local user
            val existing = _currentUser.value
            val user = existing ?: User(
                id = "local_${System.nanoTime()}",
                email = email,
                username = email.substringBefore("@"),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            _currentUser.value = user
            _isAuthenticated.value = true
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        try {
            // No-op sign out for local mode
            _currentUser.value = null
            _isAuthenticated.value = false
        } catch (e: Exception) {
            // Log the error but still clear local state
            _currentUser.value = null
            _isAuthenticated.value = false
        }
    }
    
    suspend fun resetPassword(email: String): Result<Unit> {
        return Result.success(Unit)
    }
    
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return Result.success(Unit)
    }
    
    suspend fun updateUserProfile(updates: Map<String, Any>): Result<User> {
        val currentUser = _currentUser.value
        return if (currentUser != null) {
            val updatedUser = currentUser.copy(
                username = updates["username"] as? String ?: currentUser.username,
                avatarUrl = updates["avatarUrl"] as? String ?: currentUser.avatarUrl,
                germanLevel = updates["germanLevel"] as? String ?: currentUser.germanLevel,
                targetGermanLevel = updates["targetGermanLevel"] as? String ?: currentUser.targetGermanLevel,
                dailyMixRatioWords = updates["dailyMixRatioWords"] as? Int ?: currentUser.dailyMixRatioWords,
                dailyMixRatioTopics = updates["dailyMixRatioTopics"] as? Int ?: currentUser.dailyMixRatioTopics,
                preferredSessionLength = updates["preferredSessionLength"] as? Int ?: currentUser.preferredSessionLength,
                updatedAt = System.currentTimeMillis()
            )
            _currentUser.value = updatedUser
            Result.success(updatedUser)
        } else {
            Result.failure(Exception("No authenticated user"))
        }
    }
    
    private fun createUserProfile(username: String, email: String): User {
        return User(
            id = "local_${System.nanoTime()}",
            email = email,
            username = username,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    private fun loadUserProfile(userId: String): User? {
        return _currentUser.value?.takeIf { it.id == userId }
    }
    
    suspend fun refreshUser() {
        val user = _currentUser.value
        if (user != null) {
            _isAuthenticated.value = true
        }
    }
    
    suspend fun checkAuthenticationStatus() {
        val user = _currentUser.value
        _isAuthenticated.value = user != null
    }
}