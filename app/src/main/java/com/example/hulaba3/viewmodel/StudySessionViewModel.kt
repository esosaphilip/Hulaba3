package com.example.hulaba3.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.*
import com.example.hulaba3.data.repository.StudySessionRepository
import com.example.hulaba3.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudySessionViewModel(
    private val studySessionRepository: StudySessionRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _sessions = MutableStateFlow<List<StudySession>>(emptyList())
    val sessions: StateFlow<List<StudySession>> = _sessions.asStateFlow()

    private val _topicStats = MutableStateFlow<Map<String, TopicPerformance>>(emptyMap())
    val topicStats: StateFlow<Map<String, TopicPerformance>> = _topicStats.asStateFlow()

    private val _recentSessions = MutableStateFlow<List<StudySession>>(emptyList())
    val recentSessions: StateFlow<List<StudySession>> = _recentSessions.asStateFlow()

    fun loadSessionsByTopic(topicId: String) {
        viewModelScope.launch {
            try {
                val topicSessions = studySessionRepository.getSessionsByTopic(topicId)
                _sessions.value = topicSessions
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadRecentSessions() {
        viewModelScope.launch {
            try {
                val recent = studySessionRepository.getRecentSessions()
                _recentSessions.value = recent
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getTopicPerformance(topicId: String, topicTitle: String) {
        viewModelScope.launch {
            try {
                val averageScore = studySessionRepository.getAverageScoreByTopic(topicId) ?: 0f
                val sessionCount = studySessionRepository.getCompletedSessionCount(topicId)

                val performance = TopicPerformance(
                    topicId = topicId,
                    topicTitle = topicTitle,
                    totalSessions = sessionCount,
                    averageScore = averageScore,
                    totalTimeSpent = 0L, // TODO: Calculate from session data
                    improvementTrend = 0f // TODO: Calculate trend
                )

                val currentStats = _topicStats.value.toMutableMap()
                currentStats[topicId] = performance
                _topicStats.value = currentStats

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getSessionWithDetails(sessionId: Long): StudySessionWithResults? {
        // This would be implemented to return detailed session results
        return null
    }
}
