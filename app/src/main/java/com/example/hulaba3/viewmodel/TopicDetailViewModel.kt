package com.example.hulaba3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.LearningConcept
import com.example.hulaba3.data.database.LearningTopic
import com.example.hulaba3.data.repository.LearningRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
 
class TopicDetailViewModel(
    private val learningRepository: LearningRepository
) : ViewModel() {
    
    private val _topicState = MutableStateFlow(TopicDetailState())
    val topicState: StateFlow<TopicDetailState> = _topicState.asStateFlow()
    
    fun loadTopicDetails(topicId: Long) {
        viewModelScope.launch {
            _topicState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Load topic details
                val topic = learningRepository.getTopicById(topicId).firstOrNull()
                
                if (topic != null) {
                    // Load concepts for this topic
                    val concepts = learningRepository.getConceptsByTopicId(topicId).first()
                    
                    // Calculate progress statistics
                    val progress = calculateTopicProgress(concepts)
                    
                    _topicState.update {
                        it.copy(
                            topic = topic,
                            concepts = concepts,
                            progress = progress,
                            isLoading = false
                        )
                    }
                } else {
                    _topicState.update {
                        it.copy(
                            error = "Topic not found",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _topicState.update {
                    it.copy(
                        error = "Failed to load topic details: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun calculateTopicProgress(concepts: List<LearningConcept>): TopicDetailState.TopicProgress {
        val mastered = concepts.count { it.masteryLevel >= 0.7f }
        val learning = concepts.count { it.masteryLevel in 0.3f..0.69f }
        val new = concepts.count { it.masteryLevel < 0.3f }
        
        return TopicDetailState.TopicProgress(
            masteredConcepts = mastered,
            learningConcepts = learning,
            newConcepts = new,
            streakDays = calculateStreakDays()
        )
    }
    
    private fun calculateStreakDays(): Int {
        // TODO: Implement actual streak calculation based on user activity
        return 7
    }
    
    fun updateConceptMastery(conceptId: Long, masteryLevel: Float) {
        viewModelScope.launch {
            try {
                learningRepository.updateConceptMastery(conceptId, masteryLevel)
                // Refresh the topic details
                _topicState.value.topic?.let { loadTopicDetails(it.id) }
            } catch (e: Exception) {
                // Handle error silently or show a toast
            }
        }
    }
    
    fun markConceptAsMastered(conceptId: Long) {
        updateConceptMastery(conceptId, 1.0f)
    }
    
    fun markConceptAsLearning(conceptId: Long) {
        updateConceptMastery(conceptId, 0.5f)
    }
    
    fun markConceptAsNew(conceptId: Long) {
        updateConceptMastery(conceptId, 0.0f)
    }
}

data class TopicDetailState(
    val topic: LearningTopic? = null,
    val concepts: List<LearningConcept> = emptyList(),
    val progress: TopicProgress = TopicProgress(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    data class TopicProgress(
        val masteredConcepts: Int = 0,
        val learningConcepts: Int = 0,
        val newConcepts: Int = 0,
        val streakDays: Int = 0
    )
}