package com.example.hulaba3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface AnswerDao {
    @Insert
    suspend fun insertAnswer(answer: Answer): Long

    @Insert
    suspend fun insertAnswers(answers: List<Answer>): List<Long>

    @Query("SELECT * FROM answers WHERE questionId = :questionId ORDER BY orderIndex ASC")
    suspend fun getAnswersByQuestion(questionId: Long): List<Answer>

    @Query("SELECT * FROM answers WHERE questionId = :questionId AND isCorrect = 1 LIMIT 1")
    suspend fun getCorrectAnswer(questionId: Long): Answer?

    @Update
    suspend fun updateAnswer(answer: Answer)

    @Delete
    suspend fun deleteAnswer(answer: Answer)

    @Query("DELETE FROM answers WHERE questionId = :questionId")
    suspend fun deleteAnswersByQuestion(questionId: Long)
}
