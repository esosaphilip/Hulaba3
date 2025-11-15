package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLearningPatternDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: UserLearningPattern): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatterns(patterns: List<UserLearningPattern>): List<Long>
    
    @Query("SELECT * FROM user_learning_patterns WHERE id = :id")
    suspend fun getPatternById(id: Long): UserLearningPattern?
    
    @Query("SELECT * FROM user_learning_patterns WHERE userId = :userId")
    suspend fun getPatternsByUserId(userId: String): List<UserLearningPattern>
    
    @Query("SELECT * FROM user_learning_patterns WHERE userId = :userId")
    fun getPatternsByUserIdFlow(userId: String): Flow<List<UserLearningPattern>>
    
    @Query("SELECT * FROM user_learning_patterns WHERE userId = :userId AND patternType = :patternType")
    suspend fun getPatternsByType(userId: String, patternType: String): List<UserLearningPattern>
    
    @Query("SELECT * FROM user_learning_patterns WHERE userId = :userId AND patternType = :patternType")
    fun getPatternsByTypeFlow(userId: String, patternType: String): Flow<List<UserLearningPattern>>
    
    @Query("SELECT * FROM user_learning_patterns WHERE userId = :userId AND patternType = :patternType ORDER BY confidence DESC LIMIT 1")
    suspend fun getHighestConfidencePattern(userId: String, patternType: String): UserLearningPattern?
    
    @Query("SELECT * FROM user_learning_patterns WHERE userId = :userId ORDER BY confidence DESC")
    suspend fun getPatternsByConfidence(userId: String): List<UserLearningPattern>
    
    @Query("SELECT * FROM user_learning_patterns WHERE userId = :userId AND confidence >= :minConfidence")
    suspend fun getHighConfidencePatterns(userId: String, minConfidence: Float): List<UserLearningPattern>
    
    @Query("UPDATE user_learning_patterns SET confidence = :confidence, evidenceCount = :evidenceCount, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updatePatternConfidence(id: Long, confidence: Float, evidenceCount: Int, lastUpdated: Long)
    
    @Query("UPDATE user_learning_patterns SET patternValue = :patternValue, confidence = :confidence, evidenceCount = :evidenceCount, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updatePatternValue(id: Long, patternValue: String, confidence: Float, evidenceCount: Int, lastUpdated: Long)
    
    @Query("SELECT COUNT(*) FROM user_learning_patterns WHERE userId = :userId")
    suspend fun getPatternCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM user_learning_patterns WHERE userId = :userId AND patternType = :patternType")
    suspend fun getPatternCountByType(userId: String, patternType: String): Int
    
    @Query("SELECT AVG(confidence) FROM user_learning_patterns WHERE userId = :userId")
    suspend fun getAverageConfidence(userId: String): Float?
    
    @Update
    suspend fun updatePattern(pattern: UserLearningPattern)
    
    @Delete
    suspend fun deletePattern(pattern: UserLearningPattern)
    
    @Query("DELETE FROM user_learning_patterns WHERE id = :id")
    suspend fun deletePatternById(id: Long)
    
    @Query("DELETE FROM user_learning_patterns WHERE userId = :userId")
    suspend fun deletePatternsByUserId(userId: String)
    
    @Query("DELETE FROM user_learning_patterns WHERE userId = :userId AND patternType = :patternType")
    suspend fun deletePatternsByType(userId: String, patternType: String)
    
    @Query("DELETE FROM user_learning_patterns")
    suspend fun deleteAllPatterns()
}