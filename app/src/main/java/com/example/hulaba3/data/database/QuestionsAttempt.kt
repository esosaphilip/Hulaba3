package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// New entity to track individual question attempts within sessions
@Entity(
    tableName = "question_attempts",
    foreignKeys = [
        ForeignKey(
            entity = StudySession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"]), Index(value = ["questionId"])]
)
data class QuestionAttempt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long, // Links to StudySession.id
    val questionId: Long, // Links to Question.id
    val selectedAnswerId: Long? = null, // Links to Answer.id
    val isCorrect: Boolean = false,
    val responseTime: Long = 0, // Time taken to answer in milliseconds
    val attemptedAt: Long = System.currentTimeMillis()
)
