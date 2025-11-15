package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    // Quiz operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: Quiz): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<Quiz>)

    @Update
    suspend fun updateQuiz(quiz: Quiz)

    @Delete
    suspend fun deleteQuiz(quiz: Quiz)

    @Query("DELETE FROM quizzes WHERE id = :quizId")
    suspend fun deleteQuizById(quizId: Long)

    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    suspend fun getQuizById(quizId: Long): Quiz?

    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    fun getQuizByIdFlow(quizId: Long): Flow<Quiz?>

    @Query("SELECT * FROM quizzes WHERE type = :type AND isActive = 1 ORDER BY createdAt DESC")
    suspend fun getQuizzesByType(type: QuizType): List<Quiz>

    @Query("SELECT * FROM quizzes WHERE type = :type AND isActive = 1 ORDER BY createdAt DESC")
    fun getQuizzesByTypeFlow(type: QuizType): Flow<List<Quiz>>

    @Query("SELECT * FROM quizzes WHERE topicId = :topicId AND isActive = 1 ORDER BY createdAt DESC")
    suspend fun getQuizzesByTopic(topicId: Long): List<Quiz>

    @Query("SELECT * FROM quizzes WHERE topicId = :topicId AND isActive = 1 ORDER BY createdAt DESC")
    fun getQuizzesByTopicFlow(topicId: Long): Flow<List<Quiz>>

    @Query("SELECT * FROM quizzes WHERE difficulty = :difficulty AND isActive = 1 ORDER BY createdAt DESC")
    suspend fun getQuizzesByDifficulty(difficulty: QuizDifficulty): List<Quiz>

    @Query("SELECT * FROM quizzes WHERE difficulty = :difficulty AND isActive = 1 ORDER BY createdAt DESC")
    fun getQuizzesByDifficultyFlow(difficulty: QuizDifficulty): Flow<List<Quiz>>

    @Query("SELECT * FROM quizzes WHERE isActive = 1 ORDER BY createdAt DESC")
    suspend fun getAllActiveQuizzes(): List<Quiz>

    @Query("SELECT * FROM quizzes WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveQuizzesFlow(): Flow<List<Quiz>>

    @Query("SELECT * FROM quizzes WHERE isActive = 1 ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomActiveQuizzes(limit: Int): List<Quiz>

    @Query("SELECT * FROM quizzes WHERE isActive = 1 AND topicId = :topicId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuizzesByTopic(topicId: Long, limit: Int): List<Quiz>

    // Quiz question operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestion(question: QuizQuestion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestions(questions: List<QuizQuestion>)

    @Update
    suspend fun updateQuizQuestion(question: QuizQuestion)

    @Delete
    suspend fun deleteQuizQuestion(question: QuizQuestion)

    @Query("DELETE FROM quiz_questions WHERE id = :questionId")
    suspend fun deleteQuizQuestionById(questionId: Long)

    @Query("SELECT * FROM quiz_questions WHERE id = :questionId")
    suspend fun getQuizQuestionById(questionId: Long): QuizQuestion?

    @Query("SELECT * FROM quiz_questions WHERE id = :questionId")
    fun getQuizQuestionByIdFlow(questionId: Long): Flow<QuizQuestion?>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId ORDER BY orderIndex ASC")
    suspend fun getQuizQuestionsByQuiz(quizId: Long): List<QuizQuestion>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId ORDER BY orderIndex ASC")
    fun getQuizQuestionsByQuizFlow(quizId: Long): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId AND isActive = 1 ORDER BY orderIndex ASC")
    suspend fun getActiveQuizQuestionsByQuiz(quizId: Long): List<QuizQuestion>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId AND isActive = 1 ORDER BY orderIndex ASC")
    fun getActiveQuizQuestionsByQuizFlow(quizId: Long): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId AND type = :type ORDER BY orderIndex ASC")
    suspend fun getQuizQuestionsByType(quizId: Long, type: QuestionType): List<QuizQuestion>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId AND type = :type ORDER BY orderIndex ASC")
    fun getQuizQuestionsByTypeFlow(quizId: Long, type: QuestionType): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId AND difficulty = :difficulty ORDER BY orderIndex ASC")
    suspend fun getQuizQuestionsByDifficulty(quizId: Long, difficulty: QuestionDifficulty): List<QuizQuestion>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId AND difficulty = :difficulty ORDER BY orderIndex ASC")
    fun getQuizQuestionsByDifficultyFlow(quizId: Long, difficulty: QuestionDifficulty): Flow<List<QuizQuestion>>

    // Quiz answer operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAnswer(answer: QuizAnswer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAnswers(answers: List<QuizAnswer>)

    @Update
    suspend fun updateQuizAnswer(answer: QuizAnswer)

    @Delete
    suspend fun deleteQuizAnswer(answer: QuizAnswer)

    @Query("DELETE FROM quiz_answers WHERE id = :answerId")
    suspend fun deleteQuizAnswerById(answerId: Long)

    @Query("SELECT * FROM quiz_answers WHERE id = :answerId")
    suspend fun getQuizAnswerById(answerId: Long): QuizAnswer?

    @Query("SELECT * FROM quiz_answers WHERE id = :answerId")
    fun getQuizAnswerByIdFlow(answerId: Long): Flow<QuizAnswer?>

    @Query("SELECT * FROM quiz_answers WHERE questionId = :questionId ORDER BY orderIndex ASC")
    suspend fun getQuizAnswersByQuestion(questionId: Long): List<QuizAnswer>

    @Query("SELECT * FROM quiz_answers WHERE questionId = :questionId ORDER BY orderIndex ASC")
    fun getQuizAnswersByQuestionFlow(questionId: Long): Flow<List<QuizAnswer>>

    @Query("SELECT * FROM quiz_answers WHERE questionId = :questionId AND isCorrect = 1")
    suspend fun getCorrectQuizAnswersByQuestion(questionId: Long): List<QuizAnswer>

    @Query("SELECT * FROM quiz_answers WHERE questionId = :questionId AND isCorrect = 1")
    fun getCorrectQuizAnswersByQuestionFlow(questionId: Long): Flow<List<QuizAnswer>>

    // Quiz session operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizSession(session: QuizSession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizSessions(sessions: List<QuizSession>)

    @Update
    suspend fun updateQuizSession(session: QuizSession)

    @Delete
    suspend fun deleteQuizSession(session: QuizSession)

    @Query("DELETE FROM quiz_sessions WHERE id = :sessionId")
    suspend fun deleteQuizSessionById(sessionId: Long)

    @Query("SELECT * FROM quiz_sessions WHERE id = :sessionId")
    suspend fun getQuizSessionById(sessionId: Long): QuizSession?

    @Query("SELECT * FROM quiz_sessions WHERE id = :sessionId")
    fun getQuizSessionByIdFlow(sessionId: Long): Flow<QuizSession?>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId ORDER BY startedAt DESC")
    suspend fun getQuizSessionsByUser(userId: String): List<QuizSession>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId ORDER BY startedAt DESC")
    fun getQuizSessionsByUserFlow(userId: String): Flow<List<QuizSession>>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId AND quizId = :quizId ORDER BY startedAt DESC")
    suspend fun getQuizSessionsByUserAndQuiz(userId: String, quizId: Long): List<QuizSession>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId AND quizId = :quizId ORDER BY startedAt DESC")
    fun getQuizSessionsByUserAndQuizFlow(userId: String, quizId: Long): Flow<List<QuizSession>>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId AND completed = 1 ORDER BY startedAt DESC")
    suspend fun getCompletedQuizSessionsByUser(userId: String): List<QuizSession>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId AND completed = 1 ORDER BY startedAt DESC")
    fun getCompletedQuizSessionsByUserFlow(userId: String): Flow<List<QuizSession>>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId AND completed = 0 ORDER BY startedAt DESC")
    suspend fun getInProgressQuizSessionsByUser(userId: String): List<QuizSession>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId AND completed = 0 ORDER BY startedAt DESC")
    fun getInProgressQuizSessionsByUserFlow(userId: String): Flow<List<QuizSession>>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId AND startedAt >= :startDate AND startedAt <= :endDate ORDER BY startedAt DESC")
    suspend fun getQuizSessionsByDateRange(userId: String, startDate: Long, endDate: Long): List<QuizSession>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId AND startedAt >= :startDate AND startedAt <= :endDate ORDER BY startedAt DESC")
    fun getQuizSessionsByDateRangeFlow(userId: String, startDate: Long, endDate: Long): Flow<List<QuizSession>>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId ORDER BY startedAt DESC LIMIT :limit")
    suspend fun getRecentQuizSessions(userId: String, limit: Int = 10): List<QuizSession>

    @Query("SELECT * FROM quiz_sessions WHERE userId = :userId ORDER BY startedAt DESC LIMIT :limit")
    fun getRecentQuizSessionsFlow(userId: String, limit: Int = 10): Flow<List<QuizSession>>

    // Quiz session answer operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizSessionAnswer(sessionAnswer: QuizSessionAnswer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizSessionAnswers(sessionAnswers: List<QuizSessionAnswer>)

    @Update
    suspend fun updateQuizSessionAnswer(sessionAnswer: QuizSessionAnswer)

    @Delete
    suspend fun deleteQuizSessionAnswer(sessionAnswer: QuizSessionAnswer)

    @Query("DELETE FROM quiz_session_answers WHERE id = :sessionAnswerId")
    suspend fun deleteQuizSessionAnswerById(sessionAnswerId: Long)

    @Query("SELECT * FROM quiz_session_answers WHERE id = :sessionAnswerId")
    suspend fun getQuizSessionAnswerById(sessionAnswerId: Long): QuizSessionAnswer?

    @Query("SELECT * FROM quiz_session_answers WHERE id = :sessionAnswerId")
    fun getQuizSessionAnswerByIdFlow(sessionAnswerId: Long): Flow<QuizSessionAnswer?>

    @Query("SELECT * FROM quiz_session_answers WHERE sessionId = :sessionId ORDER BY questionOrderIndex ASC")
    suspend fun getQuizSessionAnswersBySession(sessionId: Long): List<QuizSessionAnswer>

    @Query("SELECT * FROM quiz_session_answers WHERE sessionId = :sessionId ORDER BY questionOrderIndex ASC")
    fun getQuizSessionAnswersBySessionFlow(sessionId: Long): Flow<List<QuizSessionAnswer>>

    @Query("SELECT * FROM quiz_session_answers WHERE sessionId = :sessionId AND questionId = :questionId")
    suspend fun getQuizSessionAnswerBySessionAndQuestion(sessionId: Long, questionId: Long): QuizSessionAnswer?

    @Query("SELECT * FROM quiz_session_answers WHERE sessionId = :sessionId AND questionId = :questionId")
    fun getQuizSessionAnswerBySessionAndQuestionFlow(sessionId: Long, questionId: Long): Flow<QuizSessionAnswer?>

    @Query("SELECT * FROM quiz_session_answers WHERE sessionId = :sessionId AND isCorrect = 1")
    suspend fun getCorrectQuizSessionAnswersBySession(sessionId: Long): List<QuizSessionAnswer>

    @Query("SELECT * FROM quiz_session_answers WHERE sessionId = :sessionId AND isCorrect = 1")
    fun getCorrectQuizSessionAnswersBySessionFlow(sessionId: Long): Flow<List<QuizSessionAnswer>>

    @Query("SELECT * FROM quiz_session_answers WHERE sessionId = :sessionId AND isCorrect = 0")
    suspend fun getIncorrectQuizSessionAnswersBySession(sessionId: Long): List<QuizSessionAnswer>

    @Query("SELECT * FROM quiz_session_answers WHERE sessionId = :sessionId AND isCorrect = 0")
    fun getIncorrectQuizSessionAnswersBySessionFlow(sessionId: Long): Flow<List<QuizSessionAnswer>>

    // Quiz statistics operations
    @Query("SELECT COUNT(*) FROM quiz_sessions WHERE userId = :userId")
    suspend fun getTotalQuizSessionsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM quiz_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getCompletedQuizSessionsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM quiz_sessions WHERE userId = :userId AND completed = 0")
    suspend fun getInProgressQuizSessionsCount(userId: String): Int

    @Query("SELECT AVG(score) FROM quiz_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getAverageQuizScore(userId: String): Float?

    @Query("SELECT AVG(score) FROM quiz_sessions WHERE userId = :userId AND completed = 1 AND quizId = :quizId")
    suspend fun getAverageQuizScoreByQuiz(userId: String, quizId: Long): Float?

    @Query("SELECT AVG(score) FROM quiz_sessions WHERE userId = :userId AND completed = 1 AND startedAt >= :startDate")
    suspend fun getAverageQuizScoreByDate(userId: String, startDate: Long): Float?

    @Query("SELECT AVG(timeSpent) FROM quiz_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getAverageQuizTime(userId: String): Long?

    @Query("SELECT AVG(timeSpent) FROM quiz_sessions WHERE userId = :userId AND completed = 1 AND quizId = :quizId")
    suspend fun getAverageQuizTimeByQuiz(userId: String, quizId: Long): Long?

    @Query("SELECT SUM(timeSpent) FROM quiz_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getTotalQuizTime(userId: String): Long?

    @Query("SELECT COUNT(*) FROM quiz_session_answers WHERE sessionId = :sessionId AND isCorrect = 1")
    suspend fun getCorrectAnswersCountForSession(sessionId: Long): Int

    @Query("SELECT COUNT(*) FROM quiz_session_answers WHERE sessionId = :sessionId AND isCorrect = 0")
    suspend fun getIncorrectAnswersCountForSession(sessionId: Long): Int

    @Query("SELECT COUNT(*) FROM quiz_session_answers WHERE sessionId = :sessionId")
    suspend fun getTotalAnswersCountForSession(sessionId: Long): Int

    @Query("SELECT COUNT(*) FROM quiz_session_answers WHERE userId = :userId AND isCorrect = 1")
    suspend fun getTotalCorrectAnswersCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM quiz_session_answers WHERE userId = :userId AND isCorrect = 0")
    suspend fun getTotalIncorrectAnswersCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM quiz_session_answers WHERE userId = :userId")
    suspend fun getTotalAnswersCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM quiz_questions WHERE quizId = :quizId AND isActive = 1")
    suspend fun getTotalQuestionsCountForQuiz(quizId: Long): Int

    @Query("SELECT COUNT(*) FROM quiz_questions WHERE quizId = :quizId AND type = :type AND isActive = 1")
    suspend fun getQuestionsCountForQuizByType(quizId: Long, type: QuestionType): Int

    @Query("SELECT COUNT(*) FROM quiz_questions WHERE quizId = :quizId AND difficulty = :difficulty AND isActive = 1")
    suspend fun getQuestionsCountForQuizByDifficulty(quizId: Long, difficulty: QuestionDifficulty): Int

    @Query("SELECT COUNT(*) FROM quiz_questions WHERE quizId = :quizId AND difficulty = :difficulty AND type = :type AND isActive = 1")
    suspend fun getQuestionsCountForQuizByDifficultyAndType(quizId: Long, difficulty: QuestionDifficulty, type: QuestionType): Int

    // Complex queries for quiz sessions
    @Query("""
        SELECT qs.*, q.title, q.type, q.difficulty, q.topicId, 
               COUNT(qsa.id) as totalAnswers,
               SUM(CASE WHEN qsa.isCorrect = 1 THEN 1 ELSE 0 END) as correctAnswers
        FROM quiz_sessions qs
        LEFT JOIN quizzes q ON qs.quizId = q.id
        LEFT JOIN quiz_session_answers qsa ON qs.id = qsa.sessionId
        WHERE qs.userId = :userId AND qs.completed = 1
        GROUP BY qs.id, q.title, q.type, q.difficulty, q.topicId
        ORDER BY qs.startedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentQuizSessionsWithStats(userId: String, limit: Int = 10): List<QuizSessionWithStats>

    @Query("""
        SELECT qs.*, q.title, q.type, q.difficulty, q.topicId,
               COUNT(qsa.id) as totalAnswers,
               SUM(CASE WHEN qsa.isCorrect = 1 THEN 1 ELSE 0 END) as correctAnswers
        FROM quiz_sessions qs
        LEFT JOIN quizzes q ON qs.quizId = q.id
        LEFT JOIN quiz_session_answers qsa ON qs.id = qsa.sessionId
        WHERE qs.userId = :userId AND qs.completed = 1 AND q.type = :quizType
        GROUP BY qs.id, q.title, q.type, q.difficulty, q.topicId
        ORDER BY qs.startedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentQuizSessionsByTypeWithStats(userId: String, quizType: QuizType, limit: Int = 10): List<QuizSessionWithStats>

    @Query("""
        SELECT qs.*, q.title, q.type, q.difficulty, q.topicId,
               COUNT(qsa.id) as totalAnswers,
               SUM(CASE WHEN qsa.isCorrect = 1 THEN 1 ELSE 0 END) as correctAnswers
        FROM quiz_sessions qs
        LEFT JOIN quizzes q ON qs.quizId = q.id
        LEFT JOIN quiz_session_answers qsa ON qs.id = qsa.sessionId
        WHERE qs.userId = :userId AND qs.completed = 1 AND qs.startedAt >= :startDate
        GROUP BY qs.id, q.title, q.type, q.difficulty, q.topicId
        ORDER BY qs.startedAt DESC
    """)
    suspend fun getQuizSessionsByDateRangeWithStats(userId: String, startDate: Long): List<QuizSessionWithStats>

    // Delete operations
    @Query("DELETE FROM quiz_sessions WHERE userId = :userId")
    suspend fun deleteAllQuizSessionsForUser(userId: String)

    @Query("DELETE FROM quiz_session_answers WHERE userId = :userId")
    suspend fun deleteAllQuizSessionAnswersForUser(userId: String)

    @Query("DELETE FROM quizzes WHERE id = :quizId")
    suspend fun deleteQuizAndRelatedData(quizId: Long)

    @Query("DELETE FROM quiz_questions WHERE quizId = :quizId")
    suspend fun deleteQuizQuestionsByQuiz(quizId: Long)

    @Query("DELETE FROM quiz_answers WHERE questionId IN (SELECT id FROM quiz_questions WHERE quizId = :quizId)")
    suspend fun deleteQuizAnswersByQuiz(quizId: Long)

    @Query("DELETE FROM quiz_session_answers WHERE sessionId IN (SELECT id FROM quiz_sessions WHERE quizId = :quizId)")
    suspend fun deleteQuizSessionAnswersByQuiz(quizId: Long)

    @Query("DELETE FROM quiz_sessions WHERE quizId = :quizId")
    suspend fun deleteQuizSessionsByQuiz(quizId: Long)

    @Transaction
    suspend fun deleteQuizCompletely(quizId: Long) {
        deleteQuizSessionAnswersByQuiz(quizId)
        deleteQuizSessionsByQuiz(quizId)
        deleteQuizAnswersByQuiz(quizId)
        deleteQuizQuestionsByQuiz(quizId)
        deleteQuizAndRelatedData(quizId)
    }

    @Transaction
    suspend fun deleteAllQuizDataForUser(userId: String) {
        deleteAllQuizSessionAnswersForUser(userId)
        deleteAllQuizSessionsForUser(userId)
    }
}

// Data classes for complex queries
data class QuizSessionWithStats(
    @Embedded val session: QuizSession,
    val title: String,
    val type: QuizType,
    val difficulty: QuizDifficulty,
    val topicId: Long,
    val totalAnswers: Int,
    val correctAnswers: Int
)

data class QuizWithQuestions(
    @Embedded val quiz: Quiz,
    @Relation(
        parentColumn = "id",
        entityColumn = "quizId"
    )
    val questions: List<QuizQuestion>
)

data class QuizQuestionWithAnswers(
    @Embedded val question: QuizQuestion,
    @Relation(
        parentColumn = "id",
        entityColumn = "questionId"
    )
    val answers: List<QuizAnswer>
)

data class QuizWithAllData(
    @Embedded val quiz: Quiz,
    @Relation(
        parentColumn = "id",
        entityColumn = "quizId"
    )
    val questions: List<QuizQuestionWithAnswers>
)

data class QuizSessionWithAnswers(
    @Embedded val session: QuizSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val answers: List<QuizSessionAnswer>
)