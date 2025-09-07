package com.example.hulaba3.viewmodel


data class QuizUiState(
    val isLoading: Boolean = false,
    val isGenerationComplete: Boolean = false,
    val generatedQuestionsCount: Int = 0,
    val hasQuestions: Boolean = false,
    val totalQuestions: Int = 0,
    val answeredQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val isSessionActive: Boolean = false,
    val isQuizComplete: Boolean = false,
    val isReviewMode: Boolean = false,
    val showResult: Boolean = false,
    val lastAnswerCorrect: Boolean = false,
    val error: String? = null
)

