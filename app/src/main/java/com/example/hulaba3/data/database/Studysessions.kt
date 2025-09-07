package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey


// New entity to track study sessions and performance
@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topicId: String, // Links to Topic.id
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val completionPercentage: Float = 0f,
    val averageResponseTime: Long = 0, // in milliseconds
    val sessionType: String = "QUIZ" // QUIZ, REVIEW, PRACTICE
)
