package com.example.hulaba3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface QuestionAttemptDao {
    @Insert
    suspend fun insertQuestionAttempt(attempt: QuestionAttempt): Long

    @Query("SELECT * FROM question_attempts WHERE sessionId = :sessionId ORDER BY attemptedAt ASC")
    suspend fun getAttemptsBySession(sessionId: Long): List<QuestionAttempt>

    @Query("SELECT * FROM question_attempts WHERE questionId = :questionId ORDER BY attemptedAt DESC")
    suspend fun getAttemptsByQuestion(questionId: Long): List<QuestionAttempt>

    @Update
    suspend fun updateQuestionAttempt(attempt: QuestionAttempt)

    @Delete
    suspend fun deleteQuestionAttempt(attempt: QuestionAttempt)

    // Performance analytics
    @Query("""
        SELECT AVG(responseTime) 
        FROM question_attempts 
        WHERE questionId = :questionId AND isCorrect = 1
    """)
    suspend fun getAverageCorrectResponseTime(questionId: Long): Long?

    @Query("""
        SELECT COUNT(*) * 100.0 / (SELECT COUNT(*) FROM question_attempts WHERE questionId = :questionId) 
        FROM question_attempts 
        WHERE questionId = :questionId AND isCorrect = 1
    """)
    suspend fun getQuestionAccuracyRate(questionId: Long): Float?
}
