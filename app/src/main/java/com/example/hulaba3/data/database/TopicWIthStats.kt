package com.example.hulaba3.data.database


// Topic with question statistics
data class TopicWithStats(
    val topic: Topic,
    val totalQuestions: Int,
    val averageScore: Float?,
    val lastSessionDate: Long?,
    val questionsForReview: Int
)