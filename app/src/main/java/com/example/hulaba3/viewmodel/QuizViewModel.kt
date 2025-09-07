package com.example.hulaba3.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.*
import com.example.hulaba3.data.repository.QuestionRepository
import com.example.hulaba3.data.repository.StudySessionRepository
import com.example.hulaba3.utils.QuizGenerationService
import com.example.hulaba3.utils.QuizSpacedRepetitionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val questionRepository: QuestionRepository,
    private val studySessionRepository: StudySessionRepository,
    private val quizGenerationService: QuizGenerationService
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // Questions for current topic
    private val _questionsWithAnswers = MutableStateFlow<List<QuestionWithAnswers>>(emptyList())
    val questionsWithAnswers: StateFlow<List<QuestionWithAnswers>> = _questionsWithAnswers.asStateFlow()

    // Current question index
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    // Quiz session tracking
    private var currentSessionId: Long? = null
    private var sessionStartTime: Long = 0

    fun generateQuestionsForTopic(topic: Topic, questionCount: Int = 5) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val result = quizGenerationService.generateQuestionsFromPdf(
                    topic = topic,
                    questionCount = questionCount,
                    difficulty = "medium"
                )

                result.fold(
                    onSuccess = { generatedCount ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isGenerationComplete = true,
                            generatedQuestionsCount = generatedCount
                        )
                        // Load the newly generated questions
                        loadQuestionsForTopic(topic.id)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to generate questions: ${error.localizedMessage}"
                        )
                        Log.e("QuizViewModel", "Question generation failed", error)
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unexpected error: ${e.localizedMessage}"
                )
                Log.e("QuizViewModel", "Unexpected error in question generation", e)
            }
        }
    }

    fun loadQuestionsForTopic(topicId: String) {
        viewModelScope.launch {
            try {
                val questions = questionRepository.getQuestionsWithAnswersByTopic(topicId)
                _questionsWithAnswers.value = questions
                _currentQuestionIndex.value = 0

                _uiState.value = _uiState.value.copy(
                    hasQuestions = questions.isNotEmpty(),
                    totalQuestions = questions.size
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load questions: ${e.localizedMessage}"
                )
                Log.e("QuizViewModel", "Error loading questions", e)
            }
        }
    }

    fun loadQuestionsForReview(topicId: String, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val reviewQuestions = questionRepository.getQuestionsForReview(topicId, limit)
                _questionsWithAnswers.value = reviewQuestions
                _currentQuestionIndex.value = 0

                _uiState.value = _uiState.value.copy(
                    hasQuestions = reviewQuestions.isNotEmpty(),
                    totalQuestions = reviewQuestions.size,
                    isReviewMode = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load review questions: ${e.localizedMessage}"
                )
                Log.e("QuizViewModel", "Error loading review questions", e)
            }
        }
    }
    // Add to QuizViewModel
    fun getCurrentSessionId(): Long? = currentSessionId
    suspend fun getQuestionCountForTopic(topicId: String): Int {
        return questionRepository.getQuestionCountByTopic(topicId)
    }

    fun startQuizSession(topicId: String) {
        viewModelScope.launch {
            try {
                sessionStartTime = System.currentTimeMillis()
                val session = StudySession(
                    topicId = topicId,
                    startTime = sessionStartTime,
                    totalQuestions = _questionsWithAnswers.value.size,
                    sessionType = if (_uiState.value.isReviewMode) "REVIEW" else "QUIZ"
                )

                currentSessionId = studySessionRepository.insertStudySession(session)
                _uiState.value = _uiState.value.copy(isSessionActive = true)

                Log.d("QuizViewModel", "Quiz session started with ID: $currentSessionId")
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error starting quiz session", e)
            }
        }
    }

    fun answerQuestion(selectedAnswerId: Long, responseTimeMs: Long) {
        val currentQuestions = _questionsWithAnswers.value
        val currentIndex = _currentQuestionIndex.value

        if (currentIndex >= currentQuestions.size) return

        val currentQuestion = currentQuestions[currentIndex]
        val selectedAnswer = currentQuestion.answers.find { it.id == selectedAnswerId }
        val isCorrect = selectedAnswer?.isCorrect == true

        viewModelScope.launch {
            try {
                // Record the attempt if session is active
                currentSessionId?.let { sessionId ->
                    val attempt = QuestionAttempt(
                        sessionId = sessionId,
                        questionId = currentQuestion.question.id,
                        selectedAnswerId = selectedAnswerId,
                        isCorrect = isCorrect,
                        responseTime = responseTimeMs
                    )
                    studySessionRepository.insertQuestionAttempt(attempt)
                }

                // Update question using spaced repetition
                val updatedQuestion = QuizSpacedRepetitionHelper.updateQuestionAfterAttempt(
                    currentQuestion.question,
                    isCorrect,
                    if (isCorrect) 4 else 1
                )
                questionRepository.updateQuestion(updatedQuestion)

                // Update UI state
                _uiState.value = _uiState.value.copy(
                    correctAnswers = if (isCorrect) _uiState.value.correctAnswers + 1 else _uiState.value.correctAnswers,
                    answeredQuestions = _uiState.value.answeredQuestions + 1,
                    lastAnswerCorrect = isCorrect,
                    showResult = true
                )

                Log.d("QuizViewModel", "Question answered. Correct: $isCorrect")
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error processing answer", e)
            }
        }
    }

    fun nextQuestion() {
        val nextIndex = _currentQuestionIndex.value + 1
        if (nextIndex < _questionsWithAnswers.value.size) {
            _currentQuestionIndex.value = nextIndex
            _uiState.value = _uiState.value.copy(showResult = false)
        } else {
            finishQuizSession()
        }
    }

    fun finishQuizSession() {
        viewModelScope.launch {
            try {
                currentSessionId?.let { sessionId ->
                    val session = studySessionRepository.getSessionById(sessionId)
                    session?.let {
                        val completionPercentage = if (_uiState.value.totalQuestions > 0) {
                            (_uiState.value.correctAnswers.toFloat() / _uiState.value.totalQuestions) * 100f
                        } else 0f

                        val updatedSession = it.copy(
                            endTime = System.currentTimeMillis(),
                            correctAnswers = _uiState.value.correctAnswers,
                            completionPercentage = completionPercentage,
                            averageResponseTime = calculateAverageResponseTime()
                        )

                        studySessionRepository.updateStudySession(updatedSession)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isSessionActive = false,
                    isQuizComplete = true
                )

                Log.d("QuizViewModel", "Quiz session completed")
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error finishing quiz session", e)
            }
        }
    }

    private fun calculateAverageResponseTime(): Long {
        // Placeholder - would need to track individual response times
        return 5000L // 5 seconds average
    }

    fun resetQuizState() {
        _uiState.value = QuizUiState()
        _currentQuestionIndex.value = 0
        currentSessionId = null
        sessionStartTime = 0
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
