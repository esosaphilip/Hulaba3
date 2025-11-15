package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: UserGoal): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<UserGoal>): List<Long>
    
    @Query("SELECT * FROM user_goals WHERE id = :id")
    suspend fun getGoalById(id: Long): UserGoal?
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId")
    suspend fun getGoalsByUserId(userId: String): List<UserGoal>
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId")
    fun getGoalsByUserIdFlow(userId: String): Flow<List<UserGoal>>
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId AND isActive = 1")
    suspend fun getActiveGoalsByUserId(userId: String): List<UserGoal>
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId AND isActive = 1")
    fun getActiveGoalsByUserIdFlow(userId: String): Flow<List<UserGoal>>
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId AND goalType = :goalType")
    suspend fun getGoalsByType(userId: String, goalType: String): List<UserGoal>
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId AND goalType = :goalType AND isActive = 1")
    suspend fun getActiveGoalsByType(userId: String, goalType: String): List<UserGoal>
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId AND deadline IS NOT NULL AND deadline <= :currentTime")
    suspend fun getOverdueGoals(userId: String, currentTime: Long): List<UserGoal>
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId AND deadline IS NOT NULL AND deadline > :currentTime")
    suspend fun getUpcomingGoals(userId: String, currentTime: Long): List<UserGoal>
    
    @Query("UPDATE user_goals SET currentValue = :currentValue, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateGoalProgress(id: Long, currentValue: String, updatedAt: Long)
    
    @Query("UPDATE user_goals SET isActive = 0, updatedAt = :updatedAt WHERE id = :id")
    suspend fun deactivateGoal(id: Long, updatedAt: Long)
    
    @Query("UPDATE user_goals SET isActive = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun activateGoal(id: Long, updatedAt: Long)
    
    @Query("SELECT COUNT(*) FROM user_goals WHERE userId = :userId")
    suspend fun getGoalCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM user_goals WHERE userId = :userId AND isActive = 1")
    suspend fun getActiveGoalCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM user_goals WHERE userId = :userId AND goalType = :goalType")
    suspend fun getGoalCountByType(userId: String, goalType: String): Int
    
    @Update
    suspend fun updateGoal(goal: UserGoal)
    
    @Delete
    suspend fun deleteGoal(goal: UserGoal)
    
    @Query("DELETE FROM user_goals WHERE id = :id")
    suspend fun deleteGoalById(id: Long)
    
    @Query("DELETE FROM user_goals WHERE userId = :userId")
    suspend fun deleteGoalsByUserId(userId: String)
    
    @Query("DELETE FROM user_goals")
    suspend fun deleteAllGoals()
}