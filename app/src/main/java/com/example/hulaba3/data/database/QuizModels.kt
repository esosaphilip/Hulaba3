package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey

// Enums for Quiz system
enum class QuizType { MULTIPLE_CHOICE, TRUE_FALSE, FILL_IN_BLANK }
enum class QuizDifficulty { EASY, MEDIUM, HARD }
enum class QuestionType { SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, FILL_IN_BLANK }
enum class QuestionDifficulty { EASY, MEDIUM, HARD }

@Entity(
    tableName = "quizzes",
    indices = [
        Index(value = ["type"]),
        Index(value = ["difficulty"]),
        Index(value = ["topicId"]),
        Index(value = ["isActive"]),
        Index(value = ["createdAt"]) 
    ]
)
data class Quiz(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: QuizType = QuizType.MULTIPLE_CHOICE,
    val difficulty: QuizDifficulty = QuizDifficulty.MEDIUM,
    val topicId: Long = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "quiz_questions",
    indices = [
        Index(value = ["quizId"]),
        Index(value = ["orderIndex"]),
        Index(value = ["type"]),
        Index(value = ["difficulty"]),
        Index(value = ["isActive"]) 
    ],
    foreignKeys = [
        ForeignKey(
            entity = Quiz::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val orderIndex: Int = 0,
    val type: QuestionType = QuestionType.SINGLE_CHOICE,
    val difficulty: QuestionDifficulty = QuestionDifficulty.MEDIUM,
    val promptText: String,
    val isActive: Boolean = true
)

@Entity(
    tableName = "quiz_answers",
    indices = [
        Index(value = ["questionId"]),
        Index(value = ["orderIndex"]),
        Index(value = ["isCorrect"]) 
    ],
    foreignKeys = [
        ForeignKey(
            entity = QuizQuestion::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuizAnswer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: Long,
    val orderIndex: Int = 0,
    val answerText: String,
    val isCorrect: Boolean = false
)

@Entity(
    tableName = "quiz_sessions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["quizId"]),
        Index(value = ["startedAt"]),
        Index(value = ["completed"]) 
    ],
    foreignKeys = [
        ForeignKey(
            entity = Quiz::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuizSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val quizId: Long,
    val startedAt: Long = System.currentTimeMillis(),
    val completed: Boolean = false,
    val score: Float? = null,
    val timeSpent: Long? = null
)

@Entity(
    tableName = "quiz_session_answers",
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["userId"]),
        Index(value = ["questionOrderIndex"]),
        Index(value = ["questionId"]),
        Index(value = ["isCorrect"]) 
    ],
    foreignKeys = [
        ForeignKey(
            entity = QuizSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuizQuestion::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuizSessionAnswer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val userId: String,
    val questionOrderIndex: Int = 0,
    val questionId: Long,
    val selectedAnswerId: Long? = null,
    val isCorrect: Boolean = false
)

