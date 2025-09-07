package com.example.hulaba3.data.database


// Performance analytics data
data class TopicPerformance(
    val topicId: String,
    val topicTitle: String,
    val totalSessions: Int,
    val averageScore: Float,
    val totalTimeSpent: Long,
    val improvementTrend: Float // Percentage improvement over last 5 sessions
)