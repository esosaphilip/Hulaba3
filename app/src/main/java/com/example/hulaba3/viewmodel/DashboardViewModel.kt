package com.example.hulaba3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.LearningTopic
import com.example.hulaba3.data.database.LearningConcept
import com.example.hulaba3.data.database.LearningSession
import com.example.hulaba3.data.database.User
import com.example.hulaba3.data.repository.LearningRepository
import com.example.hulaba3.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
class DashboardViewModel(
    private val learningRepository: LearningRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    fun loadDashboardData() {
        viewModelScope.launch {
            _dashboardState.value = _dashboardState.value.copy(isLoading = true)
            
            try {
                // Load user data
                userRepository.getCurrentUser().collect { user ->
                    _currentUser.value = user
                }
                
                // Load learning data
                combine(
                    learningRepository.getAllTopics(),
                    learningRepository.getTodaysSessions(),
                    learningRepository.getUserProgress()
                ) { topics, todaysSessions, progress ->
                    Triple(topics, todaysSessions, progress)
                }.collect { (topics, todaysSessions, progress) ->
                    val todaysPlan = createTodaysLearningPlan(topics, todaysSessions)
                    
                    _dashboardState.value = DashboardState(
                        isLoading = false,
                        user = _currentUser.value,
                        topics = topics,
                        todaysPlan = todaysPlan,
                        todaysSessions = todaysSessions,
                        userProgress = progress,
                        totalConcepts = topics.sumOf { it.totalConcepts },
                        masteredConcepts = topics.sumOf { it.masteredConcepts },
                        streakDays = calculateStreak(todaysSessions)
                    )
                }
            } catch (e: Exception) {
                _dashboardState.value = _dashboardState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
            }
        }
    }
    
    fun refreshData() {
        loadDashboardData()
    }
    
    fun startLearningSession(topicId: Long, conceptId: Long) {
        viewModelScope.launch {
            try {
                val session = LearningSession(
                    topicId = topicId,
                    conceptId = conceptId,
                    sessionType = "mixed",
                    startTime = LocalDateTime.now(),
                    endTime = null,
                    correctAnswers = 0,
                    totalQuestions = 0,
                    sessionDuration = 0,
                    difficultyLevel = "medium",
                    isCompleted = false
                )
                
                val sessionId = learningRepository.createLearningSession(session)
                _dashboardState.value = _dashboardState.value.copy(
                    currentSessionId = sessionId
                )
            } catch (e: Exception) {
                _dashboardState.value = _dashboardState.value.copy(
                    error = e.message ?: "Failed to start learning session"
                )
            }
        }
    }
    
    fun completeLearningSession(sessionId: Long, correctAnswers: Int, totalQuestions: Int) {
        viewModelScope.launch {
            try {
                learningRepository.completeLearningSession(
                    sessionId = sessionId,
                    correctAnswers = correctAnswers,
                    totalQuestions = totalQuestions
                )
                
                _dashboardState.value = _dashboardState.value.copy(
                    currentSessionId = null
                )
                
                // Refresh dashboard data after session completion
                loadDashboardData()
            } catch (e: Exception) {
                _dashboardState.value = _dashboardState.value.copy(
                    error = e.message ?: "Failed to complete learning session"
                )
            }
        }
    }
    
    fun updateTopicProgress(topicId: Long, progress: Float) {
        viewModelScope.launch {
            try {
                learningRepository.updateTopicProgress(topicId, progress)
                // Refresh dashboard data
                loadDashboardData()
            } catch (e: Exception) {
                _dashboardState.value = _dashboardState.value.copy(
                    error = e.message ?: "Failed to update topic progress"
                )
            }
        }
    }
    
    private fun createTodaysLearningPlan(
        topics: List<LearningTopic>,
        todaysSessions: List<LearningSession>
    ): List<LearningPlanItem> {
        val completedConcepts = todaysSessions.map { it.conceptId }.toSet()
        val planItems = mutableListOf<LearningPlanItem>()
        
        // Use interleaved learning algorithm
        val conceptsToLearn = mutableListOf<LearningConcept>()
        topics.forEach { topic ->
            val topicConcepts = topic.concepts.filter { concept ->
                concept.id !in completedConcepts && concept.masteryLevel < 1.0f
            }
            conceptsToLearn.addAll(topicConcepts)
        }
        
        // Sort by priority (spaced repetition algorithm)
        conceptsToLearn.sortBy { concept ->
            when {
                concept.lastReviewed == null -> 0 // Never reviewed
                concept.masteryLevel < 0.3f -> 1 // Low mastery
                concept.masteryLevel < 0.7f -> 2 // Medium mastery
                else -> 3 // High mastery
            }
        }
        
        // Create plan items (limit to 5-7 items per day)
        conceptsToLearn.take(6).forEach { concept ->
            val topic = topics.find { it.id == concept.topicId }
            if (topic != null) {
                planItems.add(
                    LearningPlanItem(
                        topicId = topic.id,
                        topicName = topic.name,
                        conceptId = concept.id,
                        conceptName = concept.name,
                        conceptType = concept.type,
                        priority = getConceptPriority(concept),
                        estimatedTime = getEstimatedTime(concept)
                    )
                )
            }
        }
        
        return planItems
    }
    
    private fun getConceptPriority(concept: LearningConcept): String {
        return when {
            concept.lastReviewed == null -> "high"
            concept.masteryLevel < 0.3f -> "high"
            concept.masteryLevel < 0.7f -> "medium"
            else -> "low"
        }
    }
    
    private fun getEstimatedTime(concept: LearningConcept): Int {
        return when (concept.type) {
            "vocabulary" -> 3 // minutes
            "grammar" -> 5
            "phrase" -> 4
            "conversation" -> 8
            else -> 5
        }
    }
    
    private fun calculateStreak(sessions: List<LearningSession>): Int {
        if (sessions.isEmpty()) return 0
        
        val today = LocalDate.now()
        val completedDates = sessions
            .filter { it.isCompleted }
            .map { it.startTime.toLocalDate() }
            .distinct()
            .sortedDescending()
        
        var streak = 0
        var currentDate = today
        
        for (date in completedDates) {
            if (date == currentDate || date == currentDate.minusDays(1)) {
                streak++
                currentDate = date
            } else {
                break
            }
        }
        
        return streak
    }
    
    fun clearError() {
        _dashboardState.value = _dashboardState.value.copy(error = null)
    }
}

data class DashboardState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val topics: List<LearningTopic> = emptyList(),
    val todaysPlan: List<LearningPlanItem> = emptyList(),
    val todaysSessions: List<LearningSession> = emptyList(),
    val userProgress: UserProgress? = null,
    val totalConcepts: Int = 0,
    val masteredConcepts: Int = 0,
    val streakDays: Int = 0,
    val currentSessionId: Long? = null,
    val error: String? = null
)

data class LearningPlanItem(
    val topicId: Long,
    val topicName: String,
    val conceptId: Long,
    val conceptName: String,
    val conceptType: String,
    val priority: String,
    val estimatedTime: Int
)

data class UserProgress(
    val totalStudyTime: Long,
    val totalSessions: Int,
    val averageAccuracy: Float,
    val weeklyGoal: Int,
    val weeklyProgress: Int
)