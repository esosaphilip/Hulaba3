package com.example.hulaba3.uilayer.screens.learning

import com.example.hulaba3.data.database.Concept
import com.example.hulaba3.data.database.UserConceptProgress
import com.example.hulaba3.data.database.UserVocabularyProgress
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.utils.MixedLearningAlgorithm

/**
 * UI State for Mixed Learning Screen
 */
data class MixedLearningUiState(
    val screenState: ScreenState = ScreenState.LOADING,
    val currentItem: MixedLearningAlgorithm.MixedLearningItem? = null,
    val currentItemIndex: Int = 0,
    val totalItems: Int = 0,
    val currentItemRevealed: Boolean = false,
    val vocabCompleted: Int = 0,
    val conceptsCompleted: Int = 0,
    val accuracy: Int = 0,
    val timeRemaining: Int = 0,
    val sessionId: Long = 0L,
    val vocabProgress: Map<Long, UserVocabularyProgress> = emptyMap(),
    val conceptProgress: Map<Long, UserConceptProgress> = emptyMap(),
    val sessionPerformance: MixedLearningAlgorithm.SessionPerformance = MixedLearningAlgorithm.SessionPerformance(0, 0, 0, 0f, 0f),
    val errorMessage: String = ""
) {
    enum class ScreenState {
        LOADING, LEARNING, COMPLETED, ERROR
    }
}