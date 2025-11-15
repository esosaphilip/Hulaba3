package com.example.hulaba3.data.repository

import com.example.hulaba3.data.database.LearningConcept
import com.example.hulaba3.data.database.LearningSession
import com.example.hulaba3.data.database.LearningTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.hulaba3.viewmodel.UserProgress
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * Simple in-memory repository to satisfy Dashboard and TopicDetail dependencies
 * until full Room-backed repos are wired.
 */
class LearningRepository {
    private val topicsFlow = MutableStateFlow(sampleTopics())
    private val sessionsFlow = MutableStateFlow<List<LearningSession>>(emptyList())
    private val progressFlow = MutableStateFlow(UserProgress(totalStudyTime = 0, totalSessions = 0, averageAccuracy = 0f, weeklyGoal = 5, weeklyProgress = 0))
    private var nextSessionId: Long = 1L

    fun getAllTopics(): Flow<List<LearningTopic>> = topicsFlow

    fun getTopicById(id: Long): Flow<LearningTopic?> = topicsFlow.map { it.find { t -> t.id == id } }

    fun getConceptsByTopicId(topicId: Long): Flow<List<LearningConcept>> = topicsFlow.map { t ->
        t.find { it.id == topicId }?.concepts ?: emptyList()
    }

    fun getTodaysSessions(): Flow<List<LearningSession>> = sessionsFlow.map { list ->
        val today = LocalDateTime.now().toLocalDate()
        list.filter { it.startTime.toLocalDate() == today }
    }

    fun getAllSessions(): Flow<List<LearningSession>> = sessionsFlow

fun getUserProgress(): Flow<UserProgress> = progressFlow

    suspend fun createLearningSession(session: LearningSession): Long {
        val id = nextSessionId++
        val new = session.copy(id = id)
        sessionsFlow.value = sessionsFlow.value + new
        return id
    }

    suspend fun completeLearningSession(sessionId: Long, correctAnswers: Int, totalQuestions: Int) {
        val now = LocalDateTime.now()
        sessionsFlow.value = sessionsFlow.value.map { s ->
            if (s.id == sessionId) s.copy(
                endTime = now,
                correctAnswers = correctAnswers,
                totalQuestions = totalQuestions,
                isCompleted = true
            ) else s
        }
        // update progress stats
        val accuracy = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f
        val totalTime = progressFlow.value.totalStudyTime + 5 // add a small placeholder minutes
        val sessions = progressFlow.value.totalSessions + 1
        val avgAcc = if (sessions > 0) ((progressFlow.value.averageAccuracy * (sessions - 1)) + accuracy) / sessions else 0f
        val weeklyProgress = (progressFlow.value.weeklyProgress + 1).coerceAtMost(progressFlow.value.weeklyGoal)
        progressFlow.value = UserProgress(
            totalStudyTime = totalTime,
            totalSessions = sessions,
            averageAccuracy = avgAcc,
            weeklyGoal = progressFlow.value.weeklyGoal,
            weeklyProgress = weeklyProgress
        )
    }

    suspend fun updateTopicProgress(topicId: Long, progress: Float) {
        // naive implementation: set all concept mastery to at least the provided progress
        topicsFlow.value = topicsFlow.value.map { t ->
            if (t.id == topicId) {
                val updated = t.concepts.map { c ->
                    val newLevel = if (c.masteryLevel < progress) progress else c.masteryLevel
                    c.copy(masteryLevel = newLevel, lastReviewed = LocalDateTime.now())
                }
                t.copy(concepts = updated)
            } else t
        }
    }

    suspend fun addSession(session: LearningSession) {
        sessionsFlow.value = sessionsFlow.value + session
    }

    suspend fun updateConceptMastery(conceptId: Long, newMastery: Float) {
        topicsFlow.value = topicsFlow.value.map { topic ->
            val updatedConcepts = topic.concepts.map { concept ->
                if (concept.id == conceptId) concept.copy(masteryLevel = newMastery, lastReviewed = LocalDateTime.now()) else concept
            }
            topic.copy(concepts = updatedConcepts)
        }
    }

    companion object {
        private fun sampleTopics(): List<LearningTopic> {
            val t1Concepts = listOf(
                LearningConcept(id = 1, topicId = 1, name = "Basics: Hallo", masteryLevel = 0.6f),
                LearningConcept(id = 2, topicId = 1, name = "Basics: Danke", masteryLevel = 0.8f),
                LearningConcept(id = 3, topicId = 1, name = "Basics: Bitte", masteryLevel = 0.3f)
            )
            val t2Concepts = listOf(
                LearningConcept(id = 4, topicId = 2, name = "Tech: Cloud", masteryLevel = 0.5f),
                LearningConcept(id = 5, topicId = 2, name = "Tech: AI", masteryLevel = 0.2f)
            )
            return listOf(
                LearningTopic(id = 1, name = "German Basics", concepts = t1Concepts),
                LearningTopic(id = 2, name = "German Tech", concepts = t2Concepts)
            )
        }
    }
}
