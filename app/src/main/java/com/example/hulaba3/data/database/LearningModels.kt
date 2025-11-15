package com.example.hulaba3.data.database

import java.time.LocalDateTime

// Lightweight domain models used by Dashboard and Topic screens
// These are not Room entities and are used for in-memory/demo data

data class LearningTopic(
    val id: Long,
    val name: String,
    val concepts: List<LearningConcept> = emptyList(),
    val totalConcepts: Int = concepts.size,
    val masteredConcepts: Int = concepts.count { it.masteryLevel >= 0.7f }
)

data class LearningConcept(
    val id: Long,
    val topicId: Long,
    val name: String,
    val type: String = "vocabulary",
    val masteryLevel: Float = 0f,
    val lastReviewed: LocalDateTime? = null
)

data class LearningSession(
    val id: Long = 0L,
    val topicId: Long,
    val conceptId: Long,
    val sessionType: String = "mixed",
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val correctAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val sessionDuration: Int = 0,
    val difficultyLevel: String = "medium",
    val isCompleted: Boolean = false
)
