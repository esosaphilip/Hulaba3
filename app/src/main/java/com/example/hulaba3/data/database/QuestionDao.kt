package com.example.hulaba3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface QuestionDao {
    @Insert
    suspend fun insertQuestion(question: Question): Long

    @Insert
    suspend fun insertQuestions(questions: List<Question>): List<Long>

    @Query("SELECT * FROM questions WHERE topicId = :topicId ORDER BY createdAt DESC")
    suspend fun getQuestionsByTopic(topicId: String): List<Question>

    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun getQuestionById(questionId: Long): Question?

    @Query("""
        SELECT * FROM questions 
        WHERE topicId = :topicId 
        AND (nextReviewTime <= :currentTime OR nextReviewTime = 0)
        ORDER BY nextReviewTime ASC 
        LIMIT :limit
    """)
    suspend fun getQuestionsForReview(topicId: String, currentTime: Long, limit: Int): List<Question>

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun deleteQuestion(question: Question)

    @Query("DELETE FROM questions WHERE topicId = :topicId")
    suspend fun deleteQuestionsByTopic(topicId: String)

    @Query("SELECT COUNT(*) FROM questions WHERE topicId = :topicId")
    suspend fun getQuestionCountByTopic(topicId: String): Int
}
