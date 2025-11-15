package com.example.hulaba3.uilayer.screens.learning

data class GermanTechUiState(
    val screenState: ScreenState = ScreenState.IDLE,
    val currentWord: GermanTechWord? = null,
    val currentWordIndex: Int = 0,
    val totalWords: Int = 0,
    val showAnswer: Boolean = false,
    val learningMode: String = "vocabulary", // vocabulary, pronunciation, context
    val score: Int = 0,
    val accuracy: Float = 0f,
    val streakDays: Int = 0,
    val isRecording: Boolean = false,
    val lastAnswerResult: AnswerResult? = null,
    val recordingResult: RecordingResult? = null,
    val errorMessage: String? = null,
    val sessionPerformance: SessionPerformance? = null
) {
    enum class ScreenState {
        IDLE,
        LOADING,
        LEARNING,
        COMPLETED,
        ERROR
    }
    
    data class AnswerResult(
        val isCorrect: Boolean,
        val userAnswer: String,
        val correctAnswer: String,
        val feedback: String
    )
    
    data class RecordingResult(
        val pronunciationScore: Float,
        val feedback: String
    )
    
    data class SessionPerformance(
        val totalWords: Int = 0,
        val correctWords: Int = 0,
        val accuracy: Float = 0f,
        val timeSpent: Long = 0L,
        val wordsLearned: Int = 0,
        val pronunciationScore: Float = 0f
    )
}

data class GermanTechWord(
    val id: String,
    val german: String,
    val english: String,
    val pronunciation: String,
    val category: String, // AI, Data Science, Programming, Web Development
    val difficulty: String, // beginner, intermediate, advanced
    val exampleSentence: String,
    val contextExplanation: String,
    val technicalDetails: String,
    val masteryLevel: Int, // 0-10
    val lastReviewed: Long,
    val reviewCount: Int,
    val nextReviewTime: Long
)