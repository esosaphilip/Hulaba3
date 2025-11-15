package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: UserAchievement)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<UserAchievement>)
    
    @Query("SELECT * FROM user_achievements WHERE id = :id")
    suspend fun getAchievementById(id: Long): UserAchievement?
    
    @Query("SELECT * FROM user_achievements WHERE userId = :userId")
    suspend fun getAchievementsByUserId(userId: String): List<UserAchievement>
    
    @Query("SELECT * FROM user_achievements WHERE userId = :userId")
    fun getAchievementsByUserIdFlow(userId: String): Flow<List<UserAchievement>>
    
    @Query("SELECT * FROM user_achievements WHERE userId = :userId AND achievementType = :achievementType")
    suspend fun getAchievementsByType(userId: String, achievementType: String): List<UserAchievement>
    
    @Query("SELECT * FROM user_achievements WHERE userId = :userId AND achievementType = :achievementType ORDER BY achievementLevel DESC LIMIT 1")
    suspend fun getHighestLevelAchievement(userId: String, achievementType: String): UserAchievement?
    
    @Query("SELECT * FROM user_achievements WHERE userId = :userId ORDER BY unlockedAt DESC")
    suspend fun getAchievementsByUserIdSorted(userId: String): List<UserAchievement>
    
    @Query("SELECT COUNT(*) FROM user_achievements WHERE userId = :userId")
    suspend fun getAchievementCount(userId: String): Int
    
    @Query("SELECT SUM(pointsAwarded) FROM user_achievements WHERE userId = :userId")
    suspend fun getTotalPoints(userId: String): Int?
    
    @Query("SELECT * FROM user_achievements WHERE userId = :userId AND unlockedAt >= :startTime AND unlockedAt <= :endTime")
    suspend fun getAchievementsInTimeRange(userId: String, startTime: Long, endTime: Long): List<UserAchievement>
    
    @Update
    suspend fun updateAchievement(achievement: UserAchievement)
    
    @Delete
    suspend fun deleteAchievement(achievement: UserAchievement)
    
    @Query("DELETE FROM user_achievements WHERE id = :id")
    suspend fun deleteAchievementById(id: Long)
    
    @Query("DELETE FROM user_achievements WHERE userId = :userId")
    suspend fun deleteAchievementsByUserId(userId: String)
    
    @Query("DELETE FROM user_achievements")
    suspend fun deleteAllAchievements()
}