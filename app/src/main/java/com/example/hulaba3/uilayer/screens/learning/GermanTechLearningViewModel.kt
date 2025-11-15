package com.example.hulaba3.uilayer.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.repository.GermanTechRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
 
class GermanTechLearningViewModel(
    private val repository: GermanTechRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GermanTechUiState())
    val uiState: StateFlow<GermanTechUiState> = _uiState.asStateFlow()
    
    private var currentWords: List<GermanTechWord> = emptyList()
    private var currentSessionId: String = ""
    private val answersHistory = mutableListOf<GermanTechUiState.AnswerResult>()
    
    fun startLearningSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = GermanTechUiState.ScreenState.LOADING) }
            
            try {
                // Get mixed learning words
                currentWords = repository.getMixedLearningWords(limit = 20)
                currentSessionId = "german_tech_${System.currentTimeMillis()}"
                
                if (currentWords.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            screenState = GermanTechUiState.ScreenState.ERROR,
                            errorMessage = "No words available for learning"
                        )
                    }
                    return@launch
                }
                
                _uiState.update {
                    it.copy(
                        screenState = GermanTechUiState.ScreenState.LEARNING,
                        currentWord = currentWords.firstOrNull(),
                        currentWordIndex = 1,
                        totalWords = currentWords.size,
                        learningMode = getLearningModeForWord(currentWords.firstOrNull())
                    )
                }
                
                // Start session tracking
                repository.startLearningSession(currentSessionId)
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        screenState = GermanTechUiState.ScreenState.ERROR,
                        errorMessage = e.message ?: "Failed to start learning session"
                    )
                }
            }
        }
    }
    
    fun submitAnswer(answer: String) {
        val currentWord = _uiState.value.currentWord ?: return

        viewModelScope.launch {
            val isCorrect = answer.lowercase() == currentWord.english.lowercase()

            val result = GermanTechUiState.AnswerResult(
                isCorrect = isCorrect,
                userAnswer = answer,
                correctAnswer = currentWord.english,
                feedback = if (isCorrect) {
                    "Correct! ${currentWord.german} means ${currentWord.english}"
                } else {
                    "Not quite. ${currentWord.german} means ${currentWord.english}"
                }
            )

            answersHistory.add(result)

            _uiState.update {
                it.copy(
                    lastAnswerResult = result,
                    showAnswer = true
                )
            }

            // Record answer in session
            repository.recordAnswer(
                sessionId = currentSessionId,
                wordId = currentWord.id,
                isCorrect = isCorrect,
                answerTime = System.currentTimeMillis()
            )

            // Update performance metrics
            updatePerformanceMetrics()
        }
    }
    
    fun toggleAnswer() {
        _uiState.update { it.copy(showAnswer = !it.showAnswer) }
    }
    
    fun nextWord() {
        val currentIndex = _uiState.value.currentWordIndex
        val totalWords = _uiState.value.totalWords
        
        if (currentIndex >= totalWords) {
            // Session completed
            completeSession()
            return
        }
        
        val nextIndex = currentIndex
        val nextWord = currentWords.getOrNull(nextIndex)
        
        _uiState.update {
            it.copy(
                currentWord = nextWord,
                currentWordIndex = currentIndex + 1,
                showAnswer = false,
                lastAnswerResult = null,
                learningMode = getLearningModeForWord(nextWord)
            )
        }
    }
    
    fun startRecording() {
        _uiState.update { it.copy(isRecording = true) }
        // Start voice recording
        // This would integrate with ML Kit for speech recognition
    }
    
    fun stopRecording() {
        _uiState.update { it.copy(isRecording = false) }
        // Stop recording and process result
        // Simulate pronunciation result between 0.7 and 1.0
        val pronunciationScore = (0..30).random() / 100f + 0.7f

        _uiState.update {
            it.copy(
                recordingResult = GermanTechUiState.RecordingResult(
                    pronunciationScore = pronunciationScore,
                    feedback = if (pronunciationScore >= 0.8f) {
                        "Great pronunciation!"
                    } else {
                        "Good try! Focus on the 'ch' sound."
                    }
                )
            )
        }
    }
    
    fun restartSession() {
        startLearningSession()
    }
    
    private fun getLearningModeForWord(word: GermanTechWord?): String {
        return when (_uiState.value.currentWordIndex % 3) {
            0 -> "vocabulary"
            1 -> "pronunciation"
            else -> "context"
        }
    }
    
    private fun updatePerformanceMetrics() {
        val totalAnswers = answersHistory.size
        val correctAnswers = answersHistory.count { it.isCorrect }
        val accuracy = if (totalAnswers > 0) correctAnswers.toFloat() / totalAnswers else 0f

        _uiState.update { current ->
            current.copy(
                sessionPerformance = GermanTechUiState.SessionPerformance(
                    wordsLearned = totalAnswers,
                    accuracy = accuracy,
                    pronunciationScore = current.recordingResult?.pronunciationScore ?: 0f,
                    totalWords = current.totalWords,
                    correctWords = correctAnswers
                )
            )
        }
    }
    
    private fun completeSession() {
        viewModelScope.launch {
            repository.completeLearningSession(currentSessionId)
            val totalAnswers = answersHistory.size
            val correctAnswers = answersHistory.count { it.isCorrect }
            val accuracy = if (totalAnswers > 0) correctAnswers.toFloat() / totalAnswers else 0f

            _uiState.update {
                it.copy(
                    screenState = GermanTechUiState.ScreenState.COMPLETED,
                    sessionPerformance = GermanTechUiState.SessionPerformance(
                        totalWords = it.totalWords,
                        correctWords = correctAnswers,
                        accuracy = accuracy,
                        timeSpent = 0L,
                        wordsLearned = totalAnswers,
                        pronunciationScore = it.recordingResult?.pronunciationScore ?: 0f
                    )
                )
            }
        }
    }
    
    // Additional performance calculations can be added here if needed
    
    // Scheduling next review can be handled in repository in future
    
    // Updating user stats can be handled in repository in future
    
    // Event handling API can be added later as needed
    
}