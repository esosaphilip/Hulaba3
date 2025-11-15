package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyContextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContext(context: VocabularyContext): Long

    @Update
    suspend fun updateContext(context: VocabularyContext)

    @Delete
    suspend fun deleteContext(context: VocabularyContext)

    @Query("SELECT * FROM vocabulary_contexts WHERE id = :contextId")
    suspend fun getContextById(contextId: Long): VocabularyContext?

    @Query("SELECT * FROM vocabulary_contexts ORDER BY orderIndex, name")
    suspend fun getAllContexts(): List<VocabularyContext>

    @Query("SELECT * FROM vocabulary_contexts ORDER BY orderIndex, name")
    fun getAllContextsFlow(): Flow<List<VocabularyContext>>

    @Query("SELECT * FROM vocabulary_contexts WHERE name = :name LIMIT 1")
    suspend fun getContextByName(name: String): VocabularyContext?

    @Query("UPDATE vocabulary_contexts SET orderIndex = :orderIndex WHERE id = :contextId")
    suspend fun updateContextOrder(contextId: Long, orderIndex: Int)

    @Query("DELETE FROM vocabulary_contexts")
    suspend fun deleteAllContexts()
}

@Dao
interface UserVocabularyProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserVocabularyProgress): Long

    @Update
    suspend fun updateProgress(progress: UserVocabularyProgress)

    @Query("SELECT * FROM UserVocabularyProgress WHERE id = :progressId")
    suspend fun getProgressById(progressId: Long): UserVocabularyProgress?

    @Query("SELECT * FROM UserVocabularyProgress WHERE userId = :userId AND wordId = :wordId")
    suspend fun getProgress(userId: String, wordId: Long): UserVocabularyProgress?

    @Query("SELECT * FROM UserVocabularyProgress WHERE userId = :userId AND wordId = :wordId")
    fun getProgressFlow(userId: String, wordId: Long): Flow<UserVocabularyProgress?>

    @Query("""
        SELECT 
            -- Word columns (prefixed)
            w.id AS word_id,
            w.germanWord AS word_germanWord,
            w.englishTranslation AS word_englishTranslation,
            w.pronunciation AS word_pronunciation,
            w.exampleSentenceGerman AS word_exampleSentenceGerman,
            w.exampleSentenceEnglish AS word_exampleSentenceEnglish,
            w.partOfSpeech AS word_partOfSpeech,
            w.gender AS word_gender,
            w.pluralForm AS word_pluralForm,
            w.audioUrl AS word_audioUrl,
            w.difficultyLevel AS word_difficultyLevel,
            w.contextId AS word_contextId,
            w.frequencyRank AS word_frequencyRank,
            w.createdAt AS word_createdAt,
            w.updatedAt AS word_updatedAt,
            -- Progress columns (prefixed)
            uvp.id AS uvp_id,
            uvp.userId AS uvp_userId,
            uvp.wordId AS uvp_wordId,
            uvp.status AS uvp_status,
            uvp.confidenceLevel AS uvp_confidenceLevel,
            uvp.lastReviewedAt AS uvp_lastReviewedAt,
            uvp.nextReviewAt AS uvp_nextReviewAt,
            uvp.reviewCount AS uvp_reviewCount,
            uvp.correctCount AS uvp_correctCount,
            uvp.incorrectCount AS uvp_incorrectCount,
            uvp.isFavorite AS uvp_isFavorite,
            uvp.createdAt AS uvp_createdAt,
            uvp.updatedAt AS uvp_updatedAt
        FROM words w
        INNER JOIN UserVocabularyProgress uvp ON w.id = uvp.wordId
        WHERE uvp.userId = :userId AND uvp.status = :status
        ORDER BY uvp.nextReviewAt ASC
    """)
    suspend fun getWordsByStatus(userId: String, status: String): List<WordWithProgress>

    @Query("""
        SELECT 
            w.id AS word_id,
            w.germanWord AS word_germanWord,
            w.englishTranslation AS word_englishTranslation,
            w.pronunciation AS word_pronunciation,
            w.exampleSentenceGerman AS word_exampleSentenceGerman,
            w.exampleSentenceEnglish AS word_exampleSentenceEnglish,
            w.partOfSpeech AS word_partOfSpeech,
            w.gender AS word_gender,
            w.pluralForm AS word_pluralForm,
            w.audioUrl AS word_audioUrl,
            w.difficultyLevel AS word_difficultyLevel,
            w.contextId AS word_contextId,
            w.frequencyRank AS word_frequencyRank,
            w.createdAt AS word_createdAt,
            w.updatedAt AS word_updatedAt,
            uvp.id AS uvp_id,
            uvp.userId AS uvp_userId,
            uvp.wordId AS uvp_wordId,
            uvp.status AS uvp_status,
            uvp.confidenceLevel AS uvp_confidenceLevel,
            uvp.lastReviewedAt AS uvp_lastReviewedAt,
            uvp.nextReviewAt AS uvp_nextReviewAt,
            uvp.reviewCount AS uvp_reviewCount,
            uvp.correctCount AS uvp_correctCount,
            uvp.incorrectCount AS uvp_incorrectCount,
            uvp.isFavorite AS uvp_isFavorite,
            uvp.createdAt AS uvp_createdAt,
            uvp.updatedAt AS uvp_updatedAt
        FROM words w
        INNER JOIN UserVocabularyProgress uvp ON w.id = uvp.wordId
        WHERE uvp.userId = :userId AND uvp.nextReviewAt <= :currentTime
        ORDER BY uvp.nextReviewAt ASC
    """)
    suspend fun getWordsForReview(userId: String, currentTime: Long): List<WordWithProgress>

    @Query("""
        SELECT 
            w.id AS word_id,
            w.germanWord AS word_germanWord,
            w.englishTranslation AS word_englishTranslation,
            w.pronunciation AS word_pronunciation,
            w.exampleSentenceGerman AS word_exampleSentenceGerman,
            w.exampleSentenceEnglish AS word_exampleSentenceEnglish,
            w.partOfSpeech AS word_partOfSpeech,
            w.gender AS word_gender,
            w.pluralForm AS word_pluralForm,
            w.audioUrl AS word_audioUrl,
            w.difficultyLevel AS word_difficultyLevel,
            w.contextId AS word_contextId,
            w.frequencyRank AS word_frequencyRank,
            w.createdAt AS word_createdAt,
            w.updatedAt AS word_updatedAt,
            uvp.id AS uvp_id,
            uvp.userId AS uvp_userId,
            uvp.wordId AS uvp_wordId,
            uvp.status AS uvp_status,
            uvp.confidenceLevel AS uvp_confidenceLevel,
            uvp.lastReviewedAt AS uvp_lastReviewedAt,
            uvp.nextReviewAt AS uvp_nextReviewAt,
            uvp.reviewCount AS uvp_reviewCount,
            uvp.correctCount AS uvp_correctCount,
            uvp.incorrectCount AS uvp_incorrectCount,
            uvp.isFavorite AS uvp_isFavorite,
            uvp.createdAt AS uvp_createdAt,
            uvp.updatedAt AS uvp_updatedAt
        FROM words w
        INNER JOIN UserVocabularyProgress uvp ON w.id = uvp.wordId
        WHERE uvp.userId = :userId AND uvp.isFavorite = 1
        ORDER BY w.germanWord ASC
    """)
    suspend fun getFavoriteWords(userId: String): List<WordWithProgress>

    @Query("""
        SELECT 
            w.id AS word_id,
            w.germanWord AS word_germanWord,
            w.englishTranslation AS word_englishTranslation,
            w.pronunciation AS word_pronunciation,
            w.exampleSentenceGerman AS word_exampleSentenceGerman,
            w.exampleSentenceEnglish AS word_exampleSentenceEnglish,
            w.partOfSpeech AS word_partOfSpeech,
            w.gender AS word_gender,
            w.pluralForm AS word_pluralForm,
            w.audioUrl AS word_audioUrl,
            w.difficultyLevel AS word_difficultyLevel,
            w.contextId AS word_contextId,
            w.frequencyRank AS word_frequencyRank,
            w.createdAt AS word_createdAt,
            w.updatedAt AS word_updatedAt,
            uvp.id AS uvp_id,
            uvp.userId AS uvp_userId,
            uvp.wordId AS uvp_wordId,
            uvp.status AS uvp_status,
            uvp.confidenceLevel AS uvp_confidenceLevel,
            uvp.lastReviewedAt AS uvp_lastReviewedAt,
            uvp.nextReviewAt AS uvp_nextReviewAt,
            uvp.reviewCount AS uvp_reviewCount,
            uvp.correctCount AS uvp_correctCount,
            uvp.incorrectCount AS uvp_incorrectCount,
            uvp.isFavorite AS uvp_isFavorite,
            uvp.createdAt AS uvp_createdAt,
            uvp.updatedAt AS uvp_updatedAt
        FROM words w
        INNER JOIN UserVocabularyProgress uvp ON w.id = uvp.wordId
        WHERE uvp.userId = :userId AND w.contextId = :contextId
        ORDER BY uvp.nextReviewAt ASC
    """)
    suspend fun getWordsByContext(userId: String, contextId: Long): List<WordWithProgress>

    @Query("""
        SELECT 
            w.id AS word_id,
            w.germanWord AS word_germanWord,
            w.englishTranslation AS word_englishTranslation,
            w.pronunciation AS word_pronunciation,
            w.exampleSentenceGerman AS word_exampleSentenceGerman,
            w.exampleSentenceEnglish AS word_exampleSentenceEnglish,
            w.partOfSpeech AS word_partOfSpeech,
            w.gender AS word_gender,
            w.pluralForm AS word_pluralForm,
            w.audioUrl AS word_audioUrl,
            w.difficultyLevel AS word_difficultyLevel,
            w.contextId AS word_contextId,
            w.frequencyRank AS word_frequencyRank,
            w.createdAt AS word_createdAt,
            w.updatedAt AS word_updatedAt,
            uvp.id AS uvp_id,
            uvp.userId AS uvp_userId,
            uvp.wordId AS uvp_wordId,
            uvp.status AS uvp_status,
            uvp.confidenceLevel AS uvp_confidenceLevel,
            uvp.lastReviewedAt AS uvp_lastReviewedAt,
            uvp.nextReviewAt AS uvp_nextReviewAt,
            uvp.reviewCount AS uvp_reviewCount,
            uvp.correctCount AS uvp_correctCount,
            uvp.incorrectCount AS uvp_incorrectCount,
            uvp.isFavorite AS uvp_isFavorite,
            uvp.createdAt AS uvp_createdAt,
            uvp.updatedAt AS uvp_updatedAt
        FROM words w
        INNER JOIN UserVocabularyProgress uvp ON w.id = uvp.wordId
        WHERE uvp.userId = :userId AND w.difficultyLevel = :level
        ORDER BY uvp.nextReviewAt ASC
    """)
    suspend fun getWordsByDifficulty(userId: String, level: String): List<WordWithProgress>

    @Query("""
        SELECT COUNT(*) FROM UserVocabularyProgress
        WHERE userId = :userId AND status = :status
    """)
    suspend fun getWordCountByStatus(userId: String, status: String): Int

    @Query("""
        SELECT 
            COUNT(*) as totalWords,
            SUM(CASE WHEN status = 'new' THEN 1 ELSE 0 END) as newWords,
            SUM(CASE WHEN status = 'learning' THEN 1 ELSE 0 END) as learningWords,
            SUM(CASE WHEN status = 'reviewing' THEN 1 ELSE 0 END) as reviewingWords,
            SUM(CASE WHEN status = 'mastered' THEN 1 ELSE 0 END) as masteredWords,
            AVG(confidenceLevel) as avgConfidence
        FROM UserVocabularyProgress
        WHERE userId = :userId
    """)
    suspend fun getVocabularyStats(userId: String): VocabularyStats?

    @Query("UPDATE UserVocabularyProgress SET status = :status, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateWordStatus(progressId: Long, status: String, updatedAt: Long)

    @Query("UPDATE UserVocabularyProgress SET confidenceLevel = :confidence, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateConfidenceLevel(progressId: Long, confidence: Int, updatedAt: Long)

    @Query("UPDATE UserVocabularyProgress SET lastReviewedAt = :reviewedAt, nextReviewAt = :nextReviewAt, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateReviewSchedule(progressId: Long, reviewedAt: Long?, nextReviewAt: Long, updatedAt: Long)

    @Query("UPDATE UserVocabularyProgress SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateFavoriteStatus(progressId: Long, isFavorite: Boolean, updatedAt: Long)

    @Query("UPDATE UserVocabularyProgress SET reviewCount = reviewCount + 1, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun incrementReviewCount(progressId: Long, updatedAt: Long)

    @Query("UPDATE UserVocabularyProgress SET correctCount = correctCount + 1, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun incrementCorrectCount(progressId: Long, updatedAt: Long)

    @Query("UPDATE UserVocabularyProgress SET incorrectCount = incorrectCount + 1, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun incrementIncorrectCount(progressId: Long, updatedAt: Long)

    @Query("DELETE FROM UserVocabularyProgress WHERE userId = :userId")
    suspend fun deleteAllUserProgress(userId: String)

    @Query("DELETE FROM UserVocabularyProgress WHERE id = :progressId")
    suspend fun deleteProgress(progressId: Long)
}

@Dao
interface VocabularyReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: VocabularyReview): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<VocabularyReview>)

    @Query("SELECT * FROM VocabularyReview WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: Long): VocabularyReview?

    @Query("""
        SELECT * FROM VocabularyReview
        WHERE userVocabularyProgressId = :progressId
        ORDER BY reviewedAt DESC
    """)
    suspend fun getReviewsByProgress(progressId: Long): List<VocabularyReview>

    @Query("""
        SELECT * FROM VocabularyReview
        WHERE userVocabularyProgressId = :progressId
        ORDER BY reviewedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentReviews(progressId: Long, limit: Int = 10): List<VocabularyReview>

    @Query("""
        SELECT * FROM VocabularyReview
        WHERE sessionId = :sessionId
        ORDER BY reviewedAt DESC
    """)
    suspend fun getReviewsBySession(sessionId: Long): List<VocabularyReview>

    @Query("""
        SELECT AVG(difficultyRating) as avgDifficulty,
               AVG(timeSpentSeconds) as avgTimeSpent,
               SUM(CASE WHEN wasCorrect = 1 THEN 1 ELSE 0 END) as correctCount,
               SUM(CASE WHEN wasCorrect = 0 THEN 1 ELSE 0 END) as incorrectCount,
               COUNT(*) as totalReviews
        FROM VocabularyReview
        WHERE userVocabularyProgressId = :progressId
    """)
    suspend fun getReviewStats(progressId: Long): ReviewStats?

    @Query("""
        SELECT AVG(difficultyRating) as avgDifficulty,
               AVG(timeSpentSeconds) as avgTimeSpent,
               SUM(CASE WHEN wasCorrect = 1 THEN 1 ELSE 0 END) as correctCount,
               SUM(CASE WHEN wasCorrect = 0 THEN 1 ELSE 0 END) as incorrectCount,
               COUNT(*) as totalReviews
        FROM VocabularyReview vr
        INNER JOIN UserVocabularyProgress uvp ON vr.userVocabularyProgressId = uvp.id
        WHERE uvp.userId = :userId
    """)
    suspend fun getUserReviewStats(userId: String): ReviewStats?

    @Query("DELETE FROM VocabularyReview WHERE userVocabularyProgressId IN (SELECT id FROM UserVocabularyProgress WHERE userId = :userId)")
    suspend fun deleteAllUserReviews(userId: String)

    @Query("DELETE FROM VocabularyReview WHERE id = :reviewId")
    suspend fun deleteReview(reviewId: Long)
}

// Data classes for complex queries
data class WordWithProgress(
    @Embedded(prefix = "word_") val word: Word,
    @Embedded(prefix = "uvp_") val progress: UserVocabularyProgress
)

data class VocabularyStats(
    val totalWords: Int,
    val newWords: Int,
    val learningWords: Int,
    val reviewingWords: Int
)

data class ReviewStats(
    val avgDifficulty: Double?,
    val avgTimeSpent: Double?,
    val correctCount: Int,
    val incorrectCount: Int,
    val totalReviews: Int
)