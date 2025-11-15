package com.example.hulaba3.data.repository

import com.example.hulaba3.data.database.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Simple in-memory User repository **/
class UserRepository {
    private val currentUserFlow = MutableStateFlow<User?>(
        User(
            id = "demo-user",
            email = "demo@example.com",
            username = "Learner"
        )
    )

    fun getCurrentUser(): Flow<User?> = currentUserFlow

    suspend fun updateUsername(newName: String) {
        currentUserFlow.value = currentUserFlow.value?.copy(username = newName)
    }
}
