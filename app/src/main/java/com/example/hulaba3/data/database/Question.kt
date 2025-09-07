package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

// New entity for AI-generated questions
@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["topicId"])]
)
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topicId: String, // Links to Topic.id
    val questionText: String,
    val difficulty: Int = 1, // 1-5 scale
    val createdAt: Long = System.currentTimeMillis(),

    // Spaced repetition fields for questions
    val lastReviewed: Long? = null,
    val reviewCount: Int = 0,
    val nextReviewTime: Long = 0,
    val easeFactor: Float = 2.5f, // SM-2 algorithm ease factor
    val correctStreak: Int = 0, // Track consecutive correct answers
    val incorrectCount: Int = 0 // Track incorrect answers for analysis
)
