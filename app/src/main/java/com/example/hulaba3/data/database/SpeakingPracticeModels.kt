package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey

// Enums for speaking practice
enum class SpeakingPracticeType { Pronunciation, Fluency, Conversation, Reading }
enum class SpeakingPracticeDifficulty { Easy, Medium, Hard }
enum class SpeakingPracticeExerciseType { RepeatAfterMe, ReadAloud, MinimalPairs, TongueTwisters }
enum class SpeakingPracticeExerciseDifficulty { Easy, Medium, Hard }

@Entity(
    tableName = "speaking_practice_sessions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["startedAt"]),
        Index(value = ["type"]),
        Index(value = ["difficulty"]),
        Index(value = ["completed"])
    ]
)
data class SpeakingPracticeSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val type: SpeakingPracticeType = SpeakingPracticeType.Pronunciation,
    val difficulty: SpeakingPracticeDifficulty = SpeakingPracticeDifficulty.Medium,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val completed: Boolean = false,
    val accuracyScore: Float? = null,
    val fluencyScore: Float? = null,
    val pronunciationScore: Float? = null,
    val completenessScore: Float? = null,
    val overallScore: Float? = null,
    val timeSpent: Long? = null
)

@Entity(
    tableName = "speaking_practice_exercises",
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["type"]),
        Index(value = ["difficulty"]),
        Index(value = ["isActive"]),
        Index(value = ["orderIndex"]),
        Index(value = ["completed"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = SpeakingPracticeSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SpeakingPracticeExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val type: SpeakingPracticeExerciseType = SpeakingPracticeExerciseType.RepeatAfterMe,
    val difficulty: SpeakingPracticeExerciseDifficulty = SpeakingPracticeExerciseDifficulty.Medium,
    val promptText: String? = null,
    val orderIndex: Int = 0,
    val isActive: Boolean = true,
    val completed: Boolean = false
)

@Entity(
    tableName = "speaking_practice_attempts",
    indices = [
        Index(value = ["exerciseId"]),
        Index(value = ["sessionId"]),
        Index(value = ["userId"]),
        Index(value = ["attemptNumber"]),
        Index(value = ["isSuccessful"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = SpeakingPracticeExercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SpeakingPracticeSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SpeakingPracticeAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val sessionId: Long,
    val userId: String,
    val attemptNumber: Int = 1,
    val isSuccessful: Boolean = false,
    val accuracyScore: Float = 0f,
    val fluencyScore: Float = 0f,
    val pronunciationScore: Float = 0f,
    val completenessScore: Float = 0f,
    val overallScore: Float = 0f,
    val recordedAt: Long = System.currentTimeMillis()
)
