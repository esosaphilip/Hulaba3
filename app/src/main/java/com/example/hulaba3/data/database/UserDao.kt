package com.example.hulaba3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Basic CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    // Basic lookups
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmailFlow(email: String): Flow<User?>

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsernameFlow(username: String): Flow<User?>

    // Collections
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getTotalUsersCount(): Int

    // Simple search on supported fields
    @Query(
        "SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY createdAt DESC"
    )
    suspend fun searchUsers(query: String): List<User>

    @Query(
        "SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY createdAt DESC"
    )
    fun searchUsersFlow(query: String): Flow<List<User>>
}