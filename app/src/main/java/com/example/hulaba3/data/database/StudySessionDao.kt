package com.example.hulaba3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface StudySessionDao {
    @Insert
    suspend fun insertStudySession(session: StudySession): Long

    @Query("SELECT * FROM study_sessions WHERE topicId = :topicId ORDER BY startTime DESC")
    suspend fun getSessionsByTopic(topicId: String): List<StudySession>

    @Query("SELECT * FROM study_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): StudySession?

    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC LIMIT 10")
    suspend fun getRecentSessions(): List<StudySession>

    @Update
    suspend fun updateStudySession(session: StudySession)

    @Delete
    suspend fun deleteStudySession(session: StudySession)

    // Analytics queries
    @Query("""
        SELECT AVG(completionPercentage) 
        FROM study_sessions 
        WHERE topicId = :topicId AND endTime IS NOT NULL
    """)
    suspend fun getAverageScoreByTopic(topicId: String): Float?

    @Query("""
        SELECT COUNT(*) 
        FROM study_sessions 
        WHERE topicId = :topicId AND endTime IS NOT NULL
    """)
    suspend fun getCompletedSessionCount(topicId: String): Int
}
