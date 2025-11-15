package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeakingPracticeDao {
    // Speaking practice session operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakingPracticeSession(session: SpeakingPracticeSession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakingPracticeSessions(sessions: List<SpeakingPracticeSession>)

    @Update
    suspend fun updateSpeakingPracticeSession(session: SpeakingPracticeSession)

    @Delete
    suspend fun deleteSpeakingPracticeSession(session: SpeakingPracticeSession)

    @Query("DELETE FROM speaking_practice_sessions WHERE id = :sessionId")
    suspend fun deleteSpeakingPracticeSessionById(sessionId: Long)

    @Query("SELECT * FROM speaking_practice_sessions WHERE id = :sessionId")
    suspend fun getSpeakingPracticeSessionById(sessionId: Long): SpeakingPracticeSession?

    @Query("SELECT * FROM speaking_practice_sessions WHERE id = :sessionId")
    fun getSpeakingPracticeSessionByIdFlow(sessionId: Long): Flow<SpeakingPracticeSession?>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId ORDER BY startedAt DESC")
    suspend fun getSpeakingPracticeSessionsByUser(userId: String): List<SpeakingPracticeSession>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId ORDER BY startedAt DESC")
    fun getSpeakingPracticeSessionsByUserFlow(userId: String): Flow<List<SpeakingPracticeSession>>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1 ORDER BY startedAt DESC")
    suspend fun getCompletedSpeakingPracticeSessionsByUser(userId: String): List<SpeakingPracticeSession>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1 ORDER BY startedAt DESC")
    fun getCompletedSpeakingPracticeSessionsByUserFlow(userId: String): Flow<List<SpeakingPracticeSession>>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND completed = 0 ORDER BY startedAt DESC")
    suspend fun getInProgressSpeakingPracticeSessionsByUser(userId: String): List<SpeakingPracticeSession>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND completed = 0 ORDER BY startedAt DESC")
    fun getInProgressSpeakingPracticeSessionsByUserFlow(userId: String): Flow<List<SpeakingPracticeSession>>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND startedAt >= :startDate AND startedAt <= :endDate ORDER BY startedAt DESC")
    suspend fun getSpeakingPracticeSessionsByDateRange(userId: String, startDate: Long, endDate: Long): List<SpeakingPracticeSession>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND startedAt >= :startDate AND startedAt <= :endDate ORDER BY startedAt DESC")
    fun getSpeakingPracticeSessionsByDateRangeFlow(userId: String, startDate: Long, endDate: Long): Flow<List<SpeakingPracticeSession>>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId ORDER BY startedAt DESC LIMIT :limit")
    suspend fun getRecentSpeakingPracticeSessions(userId: String, limit: Int = 10): List<SpeakingPracticeSession>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId ORDER BY startedAt DESC LIMIT :limit")
    fun getRecentSpeakingPracticeSessionsFlow(userId: String, limit: Int = 10): Flow<List<SpeakingPracticeSession>>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND type = :type ORDER BY startedAt DESC")
    suspend fun getSpeakingPracticeSessionsByType(userId: String, type: SpeakingPracticeType): List<SpeakingPracticeSession>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND type = :type ORDER BY startedAt DESC")
    fun getSpeakingPracticeSessionsByTypeFlow(userId: String, type: SpeakingPracticeType): Flow<List<SpeakingPracticeSession>>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND difficulty = :difficulty ORDER BY startedAt DESC")
    suspend fun getSpeakingPracticeSessionsByDifficulty(userId: String, difficulty: SpeakingPracticeDifficulty): List<SpeakingPracticeSession>

    @Query("SELECT * FROM speaking_practice_sessions WHERE userId = :userId AND difficulty = :difficulty ORDER BY startedAt DESC")
    fun getSpeakingPracticeSessionsByDifficultyFlow(userId: String, difficulty: SpeakingPracticeDifficulty): Flow<List<SpeakingPracticeSession>>

    // Speaking practice exercise operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakingPracticeExercise(exercise: SpeakingPracticeExercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakingPracticeExercises(exercises: List<SpeakingPracticeExercise>)

    @Update
    suspend fun updateSpeakingPracticeExercise(exercise: SpeakingPracticeExercise)

    @Delete
    suspend fun deleteSpeakingPracticeExercise(exercise: SpeakingPracticeExercise)

    @Query("DELETE FROM speaking_practice_exercises WHERE id = :exerciseId")
    suspend fun deleteSpeakingPracticeExerciseById(exerciseId: Long)

    @Query("SELECT * FROM speaking_practice_exercises WHERE id = :exerciseId")
    suspend fun getSpeakingPracticeExerciseById(exerciseId: Long): SpeakingPracticeExercise?

    @Query("SELECT * FROM speaking_practice_exercises WHERE id = :exerciseId")
    fun getSpeakingPracticeExerciseByIdFlow(exerciseId: Long): Flow<SpeakingPracticeExercise?>

    @Query("SELECT * FROM speaking_practice_exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    suspend fun getSpeakingPracticeExercisesBySession(sessionId: Long): List<SpeakingPracticeExercise>

    @Query("SELECT * FROM speaking_practice_exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    fun getSpeakingPracticeExercisesBySessionFlow(sessionId: Long): Flow<List<SpeakingPracticeExercise>>

    @Query("SELECT * FROM speaking_practice_exercises WHERE sessionId = :sessionId AND completed = 1 ORDER BY orderIndex ASC")
    suspend fun getCompletedSpeakingPracticeExercisesBySession(sessionId: Long): List<SpeakingPracticeExercise>

    @Query("SELECT * FROM speaking_practice_exercises WHERE sessionId = :sessionId AND completed = 1 ORDER BY orderIndex ASC")
    fun getCompletedSpeakingPracticeExercisesBySessionFlow(sessionId: Long): Flow<List<SpeakingPracticeExercise>>

    @Query("SELECT * FROM speaking_practice_exercises WHERE sessionId = :sessionId AND completed = 0 ORDER BY orderIndex ASC")
    suspend fun getInProgressSpeakingPracticeExercisesBySession(sessionId: Long): List<SpeakingPracticeExercise>

    @Query("SELECT * FROM speaking_practice_exercises WHERE sessionId = :sessionId AND completed = 0 ORDER BY orderIndex ASC")
    fun getInProgressSpeakingPracticeExercisesBySessionFlow(sessionId: Long): Flow<List<SpeakingPracticeExercise>>

    @Query("SELECT * FROM speaking_practice_exercises WHERE type = :type ORDER BY orderIndex ASC")
    suspend fun getSpeakingPracticeExercisesByType(type: SpeakingPracticeExerciseType): List<SpeakingPracticeExercise>

    @Query("SELECT * FROM speaking_practice_exercises WHERE type = :type ORDER BY orderIndex ASC")
    fun getSpeakingPracticeExercisesByTypeFlow(type: SpeakingPracticeExerciseType): Flow<List<SpeakingPracticeExercise>>

    @Query("SELECT * FROM speaking_practice_exercises WHERE difficulty = :difficulty ORDER BY orderIndex ASC")
    suspend fun getSpeakingPracticeExercisesByDifficulty(difficulty: SpeakingPracticeExerciseDifficulty): List<SpeakingPracticeExercise>

    @Query("SELECT * FROM speaking_practice_exercises WHERE difficulty = :difficulty ORDER BY orderIndex ASC")
    fun getSpeakingPracticeExercisesByDifficultyFlow(difficulty: SpeakingPracticeExerciseDifficulty): Flow<List<SpeakingPracticeExercise>>

    @Query("SELECT * FROM speaking_practice_exercises WHERE isActive = 1 ORDER BY orderIndex ASC")
    suspend fun getAllActiveSpeakingPracticeExercises(): List<SpeakingPracticeExercise>

    @Query("SELECT * FROM speaking_practice_exercises WHERE isActive = 1 ORDER BY orderIndex ASC")
    fun getAllActiveSpeakingPracticeExercisesFlow(): Flow<List<SpeakingPracticeExercise>>

    @Query("SELECT * FROM speaking_practice_exercises WHERE isActive = 1 AND type = :type ORDER BY orderIndex ASC")
    suspend fun getActiveSpeakingPracticeExercisesByType(type: SpeakingPracticeExerciseType): List<SpeakingPracticeExercise>

    @Query("SELECT * FROM speaking_practice_exercises WHERE isActive = 1 AND type = :type ORDER BY orderIndex ASC")
    fun getActiveSpeakingPracticeExercisesByTypeFlow(type: SpeakingPracticeExerciseType): Flow<List<SpeakingPracticeExercise>>

    @Query("SELECT * FROM speaking_practice_exercises WHERE isActive = 1 AND difficulty = :difficulty ORDER BY orderIndex ASC")
    suspend fun getActiveSpeakingPracticeExercisesByDifficulty(difficulty: SpeakingPracticeExerciseDifficulty): List<SpeakingPracticeExercise>

    @Query("SELECT * FROM speaking_practice_exercises WHERE isActive = 1 AND difficulty = :difficulty ORDER BY orderIndex ASC")
    fun getActiveSpeakingPracticeExercisesByDifficultyFlow(difficulty: SpeakingPracticeExerciseDifficulty): Flow<List<SpeakingPracticeExercise>>

    // Speaking practice attempt operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakingPracticeAttempt(attempt: SpeakingPracticeAttempt): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakingPracticeAttempts(attempts: List<SpeakingPracticeAttempt>)

    @Update
    suspend fun updateSpeakingPracticeAttempt(attempt: SpeakingPracticeAttempt)

    @Delete
    suspend fun deleteSpeakingPracticeAttempt(attempt: SpeakingPracticeAttempt)

    @Query("DELETE FROM speaking_practice_attempts WHERE id = :attemptId")
    suspend fun deleteSpeakingPracticeAttemptById(attemptId: Long)

    @Query("SELECT * FROM speaking_practice_attempts WHERE id = :attemptId")
    suspend fun getSpeakingPracticeAttemptById(attemptId: Long): SpeakingPracticeAttempt?

    @Query("SELECT * FROM speaking_practice_attempts WHERE id = :attemptId")
    fun getSpeakingPracticeAttemptByIdFlow(attemptId: Long): Flow<SpeakingPracticeAttempt?>

    @Query("SELECT * FROM speaking_practice_attempts WHERE exerciseId = :exerciseId ORDER BY attemptNumber ASC")
    suspend fun getSpeakingPracticeAttemptsByExercise(exerciseId: Long): List<SpeakingPracticeAttempt>

    @Query("SELECT * FROM speaking_practice_attempts WHERE exerciseId = :exerciseId ORDER BY attemptNumber ASC")
    fun getSpeakingPracticeAttemptsByExerciseFlow(exerciseId: Long): Flow<List<SpeakingPracticeAttempt>>

    @Query("SELECT * FROM speaking_practice_attempts WHERE exerciseId = :exerciseId AND isSuccessful = 1 ORDER BY attemptNumber ASC")
    suspend fun getSuccessfulSpeakingPracticeAttemptsByExercise(exerciseId: Long): List<SpeakingPracticeAttempt>

    @Query("SELECT * FROM speaking_practice_attempts WHERE exerciseId = :exerciseId AND isSuccessful = 1 ORDER BY attemptNumber ASC")
    fun getSuccessfulSpeakingPracticeAttemptsByExerciseFlow(exerciseId: Long): Flow<List<SpeakingPracticeAttempt>>

    @Query("SELECT * FROM speaking_practice_attempts WHERE sessionId = :sessionId ORDER BY attemptNumber ASC")
    suspend fun getSpeakingPracticeAttemptsBySession(sessionId: Long): List<SpeakingPracticeAttempt>

    @Query("SELECT * FROM speaking_practice_attempts WHERE sessionId = :sessionId ORDER BY attemptNumber ASC")
    fun getSpeakingPracticeAttemptsBySessionFlow(sessionId: Long): Flow<List<SpeakingPracticeAttempt>>

   
    // Simple speaking practice entity operations for repository compatibility
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPractice(practice: SpeakingPracticeEntity)

    @Query("SELECT * FROM speaking_practice ORDER BY createdAt DESC")
    fun getAllPractices(): kotlinx.coroutines.flow.Flow<List<SpeakingPracticeEntity>>

    @Query("SELECT * FROM speaking_practice WHERE id = :practiceId LIMIT 1")
    suspend fun getPracticeById(practiceId: String): SpeakingPracticeEntity?

    @Query("SELECT * FROM speaking_practice WHERE wordId = :wordId ORDER BY createdAt DESC")
    fun getPracticesForWord(wordId: String): kotlinx.coroutines.flow.Flow<List<SpeakingPracticeEntity>>

    @Query("DELETE FROM speaking_practice WHERE id = :practiceId")
    suspend fun deletePractice(practiceId: String)
 // Speaking practice statistics operations
    @Query("SELECT COUNT(*) FROM speaking_practice_sessions WHERE userId = :userId")
    suspend fun getTotalSpeakingPracticeSessionsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getCompletedSpeakingPracticeSessionsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_sessions WHERE userId = :userId AND completed = 0")
    suspend fun getInProgressSpeakingPracticeSessionsCount(userId: String): Int

    @Query("SELECT AVG(accuracyScore) FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getAverageSpeakingPracticeAccuracy(userId: String): Float?

    @Query("SELECT AVG(fluencyScore) FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getAverageSpeakingPracticeFluency(userId: String): Float?

    @Query("SELECT AVG(pronunciationScore) FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getAverageSpeakingPracticePronunciation(userId: String): Float?

    @Query("SELECT AVG(completenessScore) FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getAverageSpeakingPracticeCompleteness(userId: String): Float?

    @Query("SELECT AVG(overallScore) FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getAverageSpeakingPracticeOverallScore(userId: String): Float?

    @Query("SELECT AVG(timeSpent) FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getAverageSpeakingPracticeTime(userId: String): Long?

    @Query("SELECT SUM(timeSpent) FROM speaking_practice_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getTotalSpeakingPracticeTime(userId: String): Long?

    @Query("SELECT COUNT(*) FROM speaking_practice_exercises WHERE sessionId = :sessionId")
    suspend fun getTotalExercisesCountForSession(sessionId: Long): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_exercises WHERE sessionId = :sessionId AND completed = 1")
    suspend fun getCompletedExercisesCountForSession(sessionId: Long): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_exercises WHERE sessionId = :sessionId AND completed = 0")
    suspend fun getInProgressExercisesCountForSession(sessionId: Long): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_attempts WHERE exerciseId = :exerciseId")
    suspend fun getTotalAttemptsCountForExercise(exerciseId: Long): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_attempts WHERE exerciseId = :exerciseId AND isSuccessful = 1")
    suspend fun getSuccessfulAttemptsCountForExercise(exerciseId: Long): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_attempts WHERE exerciseId = :exerciseId AND isSuccessful = 0")
    suspend fun getUnsuccessfulAttemptsCountForExercise(exerciseId: Long): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_attempts WHERE userId = :userId")
    suspend fun getTotalSpeakingPracticeAttemptsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_attempts WHERE userId = :userId AND isSuccessful = 1")
    suspend fun getTotalSuccessfulSpeakingPracticeAttemptsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM speaking_practice_attempts WHERE userId = :userId AND isSuccessful = 0")
    suspend fun getTotalUnsuccessfulSpeakingPracticeAttemptsCount(userId: String): Int

    @Query("SELECT AVG(accuracyScore) FROM speaking_practice_attempts WHERE exerciseId = :exerciseId")
    suspend fun getAverageAccuracyScoreForExercise(exerciseId: Long): Float?

    @Query("SELECT AVG(fluencyScore) FROM speaking_practice_attempts WHERE exerciseId = :exerciseId")
    suspend fun getAverageFluencyScoreForExercise(exerciseId: Long): Float?

    @Query("SELECT AVG(pronunciationScore) FROM speaking_practice_attempts WHERE exerciseId = :exerciseId")
    suspend fun getAveragePronunciationScoreForExercise(exerciseId: Long): Float?

    @Query("SELECT AVG(completenessScore) FROM speaking_practice_attempts WHERE exerciseId = :exerciseId")
    suspend fun getAverageCompletenessScoreForExercise(exerciseId: Long): Float?

    @Query("SELECT AVG(overallScore) FROM speaking_practice_attempts WHERE exerciseId = :exerciseId")
    suspend fun getAverageOverallScoreForExercise(exerciseId: Long): Float?

    @Query("SELECT AVG(accuracyScore) FROM speaking_practice_attempts WHERE userId = :userId")
    suspend fun getAverageSpeakingPracticeAttemptAccuracy(userId: String): Float?

    @Query("SELECT AVG(fluencyScore) FROM speaking_practice_attempts WHERE userId = :userId")
    suspend fun getAverageSpeakingPracticeAttemptFluency(userId: String): Float?

    @Query("SELECT AVG(pronunciationScore) FROM speaking_practice_attempts WHERE userId = :userId")
    suspend fun getAverageSpeakingPracticeAttemptPronunciation(userId: String): Float?

    @Query("SELECT AVG(completenessScore) FROM speaking_practice_attempts WHERE userId = :userId")
    suspend fun getAverageSpeakingPracticeAttemptCompleteness(userId: String): Float?

    @Query("SELECT AVG(overallScore) FROM speaking_practice_attempts WHERE userId = :userId")
    suspend fun getAverageSpeakingPracticeAttemptOverallScore(userId: String): Float?

    // Complex queries for speaking practice sessions
    @Query("""
        SELECT sps.*, COUNT(spe.id) as totalExercises,
               COUNT(CASE WHEN spe.completed = 1 THEN 1 END) as completedExercises,
               AVG(spa.accuracyScore) as avgAccuracy,
               AVG(spa.fluencyScore) as avgFluency,
               AVG(spa.pronunciationScore) as avgPronunciation,
               AVG(spa.completenessScore) as avgCompleteness,
               AVG(spa.overallScore) as avgOverallScore
        FROM speaking_practice_sessions sps
        LEFT JOIN speaking_practice_exercises spe ON sps.id = spe.sessionId
        LEFT JOIN speaking_practice_attempts spa ON spe.id = spa.exerciseId
        WHERE sps.userId = :userId AND sps.completed = 1
        GROUP BY sps.id
        ORDER BY sps.startedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentSpeakingPracticeSessionsWithStats(userId: String, limit: Int = 10): List<SpeakingPracticeSessionWithStats>

    @Query("""
        SELECT sps.*, COUNT(spe.id) as totalExercises,
               COUNT(CASE WHEN spe.completed = 1 THEN 1 END) as completedExercises,
               AVG(spa.accuracyScore) as avgAccuracy,
               AVG(spa.fluencyScore) as avgFluency,
               AVG(spa.pronunciationScore) as avgPronunciation,
               AVG(spa.completenessScore) as avgCompleteness,
               AVG(spa.overallScore) as avgOverallScore
        FROM speaking_practice_sessions sps
        LEFT JOIN speaking_practice_exercises spe ON sps.id = spe.sessionId
        LEFT JOIN speaking_practice_attempts spa ON spe.id = spa.exerciseId
        WHERE sps.userId = :userId AND sps.completed = 1 AND sps.type = :type
        GROUP BY sps.id
        ORDER BY sps.startedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentSpeakingPracticeSessionsByTypeWithStats(userId: String, type: SpeakingPracticeType, limit: Int = 10): List<SpeakingPracticeSessionWithStats>

    @Query("""
        SELECT sps.*, COUNT(spe.id) as totalExercises,
               COUNT(CASE WHEN spe.completed = 1 THEN 1 END) as completedExercises,
               AVG(spa.accuracyScore) as avgAccuracy,
               AVG(spa.fluencyScore) as avgFluency,
               AVG(spa.pronunciationScore) as avgPronunciation,
               AVG(spa.completenessScore) as avgCompleteness,
               AVG(spa.overallScore) as avgOverallScore
        FROM speaking_practice_sessions sps
        LEFT JOIN speaking_practice_exercises spe ON sps.id = spe.sessionId
        LEFT JOIN speaking_practice_attempts spa ON spe.id = spa.exerciseId
        WHERE sps.userId = :userId AND sps.completed = 1 AND sps.startedAt >= :startDate
        GROUP BY sps.id
        ORDER BY sps.startedAt DESC
    """)
    suspend fun getSpeakingPracticeSessionsByDateRangeWithStats(userId: String, startDate: Long): List<SpeakingPracticeSessionWithStats>

    // Delete operations
    @Query("DELETE FROM speaking_practice_sessions WHERE userId = :userId")
    suspend fun deleteAllSpeakingPracticeSessionsForUser(userId: String)

    @Query("DELETE FROM speaking_practice_exercises WHERE sessionId = :sessionId")
    suspend fun deleteSpeakingPracticeExercisesBySession(sessionId: Long)

    @Query("DELETE FROM speaking_practice_attempts WHERE exerciseId IN (SELECT id FROM speaking_practice_exercises WHERE sessionId = :sessionId)")
    suspend fun deleteSpeakingPracticeAttemptsBySession(sessionId: Long)

    @Query("DELETE FROM speaking_practice_attempts WHERE exerciseId = :exerciseId")
    suspend fun deleteSpeakingPracticeAttemptsByExercise(exerciseId: Long)

    @Query("DELETE FROM speaking_practice_exercises WHERE id = :exerciseId")
    suspend fun deleteSpeakingPracticeExerciseAndRelatedData(exerciseId: Long)

    @Transaction
    suspend fun deleteSpeakingPracticeExerciseCompletely(exerciseId: Long) {
        deleteSpeakingPracticeAttemptsByExercise(exerciseId)
        deleteSpeakingPracticeExerciseAndRelatedData(exerciseId)
    }

    @Transaction
    suspend fun deleteSpeakingPracticeSessionCompletely(sessionId: Long) {
        deleteSpeakingPracticeAttemptsBySession(sessionId)
        deleteSpeakingPracticeExercisesBySession(sessionId)
        deleteSpeakingPracticeSessionById(sessionId)
    }

    @Transaction
    suspend fun deleteAllSpeakingPracticeDataForUser(userId: String) {
        val sessions = getSpeakingPracticeSessionsByUser(userId)
        sessions.forEach { session ->
            deleteSpeakingPracticeAttemptsBySession(session.id)
            deleteSpeakingPracticeExercisesBySession(session.id)
        }
        deleteAllSpeakingPracticeSessionsForUser(userId)
    }
}

// Data classes for complex queries
data class SpeakingPracticeSessionWithStats(
    @Embedded val session: SpeakingPracticeSession,
    val totalExercises: Int,
    val completedExercises: Int,
    val avgAccuracy: Float?,
    val avgFluency: Float?,
    val avgPronunciation: Float?,
    val avgCompleteness: Float?,
    val avgOverallScore: Float?
)

data class SpeakingPracticeSessionWithExercises(
    @Embedded val session: SpeakingPracticeSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val exercises: List<SpeakingPracticeExercise>
)

data class SpeakingPracticeExerciseWithAttempts(
    @Embedded val exercise: SpeakingPracticeExercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId"
    )
    val attempts: List<SpeakingPracticeAttempt>
)

data class SpeakingPracticeSessionWithAllData(
    @Embedded val session: SpeakingPracticeSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val exercises: List<SpeakingPracticeExerciseWithAttempts>
)