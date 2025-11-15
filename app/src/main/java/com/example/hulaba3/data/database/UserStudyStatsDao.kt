package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStudyStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: UserStudyStats)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatsList(statsList: List<UserStudyStats>)
    
    @Query("SELECT * FROM user_study_stats WHERE id = :id")
    suspend fun getStatsById(id: String): UserStudyStats?
    
    @Query("SELECT * FROM user_study_stats WHERE userId = :userId")
    suspend fun getStatsByUserId(userId: String): List<UserStudyStats>
    
    @Query("SELECT * FROM user_study_stats WHERE userId = :userId")
    fun getStatsByUserIdFlow(userId: String): Flow<List<UserStudyStats>>
    
    @Query("SELECT * FROM user_study_stats WHERE userId = :userId AND date = :date")
    suspend fun getStatsByDate(userId: String, date: Long): UserStudyStats?
    
    @Query("SELECT * FROM user_study_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date")
    suspend fun getStatsByDateRange(userId: String, startDate: Long, endDate: Long): List<UserStudyStats>
    
    @Query("SELECT * FROM user_study_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date")
    fun getStatsByDateRangeFlow(userId: String, startDate: Long, endDate: Long): Flow<List<UserStudyStats>>
    
    @Query("SELECT * FROM user_study_stats WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestStats(userId: String): UserStudyStats?
    
    @Query("SELECT * FROM user_study_stats WHERE userId = :userId AND streakDay > 0 ORDER BY date DESC LIMIT 1")
    suspend fun getLatestStreakStats(userId: String): UserStudyStats?
    
    @Query("SELECT SUM(totalStudyTimeMinutes) FROM user_study_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalStudyTimeInRange(userId: String, startDate: Long, endDate: Long): Int?
    
    @Query("SELECT SUM(wordsReviewedCount) FROM user_study_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalWordsReviewedInRange(userId: String, startDate: Long, endDate: Long): Int?
    
    @Query("SELECT SUM(conceptsReviewedCount) FROM user_study_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalConceptsReviewedInRange(userId: String, startDate: Long, endDate: Long): Int?
    
    @Query("SELECT SUM(sessionsCount) FROM user_study_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalSessionsInRange(userId: String, startDate: Long, endDate: Long): Int?
    
    @Query("SELECT AVG(retentionRate) FROM user_study_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate AND retentionRate IS NOT NULL")
    suspend fun getAverageRetentionRateInRange(userId: String, startDate: Long, endDate: Long): Float?
    
    @Query("SELECT AVG(accuracyRate) FROM user_study_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate AND accuracyRate IS NOT NULL")
    suspend fun getAverageAccuracyRateInRange(userId: String, startDate: Long, endDate: Long): Float?
    
    @Query("SELECT MAX(streakDay) FROM user_study_stats WHERE userId = :userId")
    suspend fun getMaxStreak(userId: String): Int?
    
    @Query("UPDATE user_study_stats SET totalStudyTimeMinutes = totalStudyTimeMinutes + :additionalMinutes WHERE id = :id")
    suspend fun addStudyTime(id: String, additionalMinutes: Int)
    
    @Query("UPDATE user_study_stats SET wordsReviewedCount = wordsReviewedCount + :additionalWords WHERE id = :id")
    suspend fun addWordsReviewed(id: String, additionalWords: Int)
    
    @Query("UPDATE user_study_stats SET conceptsReviewedCount = conceptsReviewedCount + :additionalConcepts WHERE id = :id")
    suspend fun addConceptsReviewed(id: String, additionalConcepts: Int)
    
    @Query("UPDATE user_study_stats SET sessionsCount = sessionsCount + 1 WHERE id = :id")
    suspend fun incrementSessionCount(id: String)
    
    @Query("UPDATE user_study_stats SET retentionRate = :retentionRate, accuracyRate = :accuracyRate WHERE id = :id")
    suspend fun updatePerformanceMetrics(id: String, retentionRate: Float?, accuracyRate: Float?)
    
    @Update
    suspend fun updateStats(stats: UserStudyStats)
    
    @Delete
    suspend fun deleteStats(stats: UserStudyStats)
    
    @Query("DELETE FROM user_study_stats WHERE id = :id")
    suspend fun deleteStatsById(id: String)
    
    @Query("DELETE FROM user_study_stats WHERE userId = :userId")
    suspend fun deleteStatsByUserId(userId: String)
    
    @Query("DELETE FROM user_study_stats WHERE userId = :userId AND date < :beforeDate")
    suspend fun deleteStatsBeforeDate(userId: String, beforeDate: Long)
    
    @Query("DELETE FROM user_study_stats")
    suspend fun deleteAllStats()
}