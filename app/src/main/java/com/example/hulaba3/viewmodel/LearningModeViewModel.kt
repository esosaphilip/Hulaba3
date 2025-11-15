package com.example.hulaba3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.*
import com.example.hulaba3.data.repository.WordRepository
import com.example.hulaba3.data.repository.TopicRepository
import com.example.hulaba3.data.repository.StudySessionRepository
import com.example.hulaba3.utils.MixedLearningAlgorithm
import com.example.hulaba3.utils.SpacedRepetitionHelper
import com.example.hulaba3.uilayer.screens.learning.GermanTechUiState
import com.example.hulaba3.uilayer.screens.learning.GermanTechWord
import com.example.hulaba3.uilayer.screens.learning.MixedLearningUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
 
class LearningModeViewModel(
    private val wordRepository: WordRepository,
    private val topicRepository: TopicRepository,
    private val studySessionRepository: StudySessionRepository,
    private val userVocabularyProgressDao: UserVocabularyProgressDao,
    private val userConceptProgressDao: UserConceptProgressDao,
    private val vocabularyReviewDao: VocabularyReviewDao,
    private val conceptReviewDao: ConceptReviewDao
) : ViewModel() {

    // UI state flows
    private val _mixedLearningUiState = MutableStateFlow(MixedLearningUiState())
    val mixedLearningUiState: StateFlow<MixedLearningUiState> = _mixedLearningUiState.asStateFlow()

    private val _germanTechUiState = MutableStateFlow(GermanTechUiState())
    val germanTechUiState: StateFlow<GermanTechUiState> = _germanTechUiState.asStateFlow()

    // Session tracking
    private var currentSession: StudySession? = null
    private var mixedLearningItems: List<MixedLearningAlgorithm.MixedLearningItem> = emptyList()
    private var germanTechWords: List<GermanTechWord> = emptyList()
    private var sessionStartTime: Long = 0L
    private var currentItemStartTime: Long = 0L
    private var sessionAnswers = mutableListOf<SessionAnswer>()

    init {
        loadMixedLearningSession()
    }

    // ------------------------- German + Tech Learning -------------------------
    fun loadGermanTechSession() {
        viewModelScope.launch {
            try {
                _germanTechUiState.value = _germanTechUiState.value.copy(
                    screenState = GermanTechUiState.ScreenState.LOADING
                )

                // Generate sample words (UI only)
                germanTechWords = generateGermanTechWords()

                _germanTechUiState.value = GermanTechUiState(
                    screenState = GermanTechUiState.ScreenState.LEARNING,
                    currentWord = germanTechWords.firstOrNull(),
                    currentWordIndex = 0,
                    totalWords = germanTechWords.size,
                    showAnswer = false,
                    learningMode = "vocabulary",
                    score = 0,
                    streakDays = getCurrentStreak(),
                    accuracy = 0f
                )

            } catch (e: Exception) {
                _germanTechUiState.value = _germanTechUiState.value.copy(
                    screenState = GermanTechUiState.ScreenState.ERROR,
                    errorMessage = "Failed to load German+Tech session: ${e.message}"
                )
            }
        }
    }

    fun revealAnswer() {
        _germanTechUiState.value = _germanTechUiState.value.copy(showAnswer = true)
    }

    fun submitGermanTechAnswer(userAnswer: String) {
        val currentWord = _germanTechUiState.value.currentWord ?: return
        val isCorrect = userAnswer.trim().equals(currentWord.english, ignoreCase = true)
        val newScore = if (isCorrect) _germanTechUiState.value.score + 1 else _germanTechUiState.value.score
        val newAccuracy = if (_germanTechUiState.value.currentWordIndex > 0) {
            newScore.toFloat() / (_germanTechUiState.value.currentWordIndex + 1).toFloat()
        } else 0f

        val answerResult = GermanTechUiState.AnswerResult(
            isCorrect = isCorrect,
            userAnswer = userAnswer,
            correctAnswer = currentWord.english,
            feedback = if (isCorrect) "Correct!" else "Try again."
        )

        _germanTechUiState.value = _germanTechUiState.value.copy(
            showAnswer = true,
            score = newScore,
            accuracy = newAccuracy,
            lastAnswerResult = answerResult
        )

        viewModelScope.launch { updateGermanTechWordProgress(currentWord, isCorrect) }
    }

    fun nextGermanTechWord() {
        val currentIndex = _germanTechUiState.value.currentWordIndex
        val nextIndex = currentIndex + 1
        if (nextIndex >= germanTechWords.size) {
            completeGermanTechSession()
        } else {
            _germanTechUiState.value = _germanTechUiState.value.copy(
                currentWord = germanTechWords[nextIndex],
                currentWordIndex = nextIndex,
                showAnswer = false,
                lastAnswerResult = null
            )
        }
    }

    fun setLearningMode(mode: String) {
        _germanTechUiState.value = _germanTechUiState.value.copy(learningMode = mode)
    }

    fun playGermanAudio(word: String) {
        // TODO: Hook up audio playback (ExoPlayer)
    }

    fun startGermanRecording() {
        _germanTechUiState.value = _germanTechUiState.value.copy(isRecording = true)
    }

    fun stopGermanRecording() {
        _germanTechUiState.value = _germanTechUiState.value.copy(isRecording = false)
        val mockResult = GermanTechUiState.RecordingResult(
            pronunciationScore = 0.8f,
            feedback = "Good pronunciation, keep practicing!"
        )
        _germanTechUiState.value = _germanTechUiState.value.copy(recordingResult = mockResult)
    }

    fun endGermanTechSession() { viewModelScope.launch { completeGermanTechSession() } }
    fun startNewGermanTechSession() { loadGermanTechSession() }

    private suspend fun updateGermanTechWordProgress(word: GermanTechWord, wasCorrect: Boolean) {
        val newMasteryLevel = if (wasCorrect) (word.masteryLevel + 1).coerceAtMost(10) else (word.masteryLevel - 1).coerceAtLeast(0)
        val updatedWord = word.copy(
            masteryLevel = newMasteryLevel,
            lastReviewed = System.currentTimeMillis(),
            reviewCount = word.reviewCount + 1,
            nextReviewTime = calculateNextReviewTime(newMasteryLevel)
        )
        // UI-only; persistence can be added via repository if needed.
    }

    private fun calculateNextReviewTime(masteryLevel: Int): Long {
        val baseInterval = when (masteryLevel) {
            in 0..2 -> 1 * 24 * 60 * 60 * 1000L
            in 3..5 -> 3 * 24 * 60 * 60 * 1000L
            in 6..8 -> 7 * 24 * 60 * 60 * 1000L
            else -> 14 * 24 * 60 * 60 * 1000L
        }
        return System.currentTimeMillis() + baseInterval
    }

    private fun completeGermanTechSession() {
        _germanTechUiState.value = _germanTechUiState.value.copy(screenState = GermanTechUiState.ScreenState.COMPLETED)
    }

    private fun generateGermanTechWords(): List<GermanTechWord> {
        return listOf(
            GermanTechWord(
                id = "1",
                german = "der Algorithmus",
                english = "algorithm",
                pronunciation = "[ˌalɡoˈʁɪtmʊs]",
                category = "AI",
                difficulty = "intermediate",
                exampleSentence = "Der Algorithmus kann komplexe Probleme lösen.",
                contextExplanation = "Ein Algorithmus ist eine Schritt-für-Schritt-Anleitung zur Lösung eines Problems.",
                technicalDetails = "Algorithmen in der KI umfassen verschiedene Techniken wie tiefes Lernen.",
                masteryLevel = 3,
                lastReviewed = System.currentTimeMillis() - 86400000,
                reviewCount = 5,
                nextReviewTime = System.currentTimeMillis()
            )
        )
    }

    private fun getCurrentStreak(): Int = 12 // TODO: real streak calculation

    // --------------------------- Mixed Learning ---------------------------
    fun loadMixedLearningSession() {
        viewModelScope.launch {
            try {
                _mixedLearningUiState.value = _mixedLearningUiState.value.copy(
                    screenState = MixedLearningUiState.ScreenState.LOADING
                )

                val userId = getCurrentUserId()
                val now = System.currentTimeMillis()

                // Vocabulary items due for review
                val vocabItems: List<UserVocabularyProgress> =
                    userVocabularyProgressDao
                        .getWordsForReview(userId = userId, currentTime = now)
                        .map { it.progress }
                        .take(12)

                // Concepts due for review - requires topic context; keep empty until topic selection is available
                val conceptItems: List<UserConceptProgress> = emptyList()

                val mixedItems = MixedLearningAlgorithm.generateMixedLearningItems(
                    vocabularyItems = vocabItems,
                    topicConcepts = conceptItems,
                    config = MixedLearningAlgorithm.MixedLearningConfig(
                        totalItems = 20,
                        vocabularyRatio = 0.6f,
                        topicRatio = 0.4f
                    )
                )

                mixedLearningItems = mixedItems

                // Create and start study session
                val session = StudySession(
                    topicId = "MIXED",
                    startTime = System.currentTimeMillis(),
                    endTime = null,
                    totalQuestions = mixedItems.size,
                    correctAnswers = 0,
                    completionPercentage = 0f,
                    averageResponseTime = 0L,
                    sessionType = "MIXED_LEARNING"
                )

                val sessionId = studySessionRepository.insertStudySession(session)
                currentSession = session.copy(id = sessionId)
                sessionStartTime = System.currentTimeMillis()

                _mixedLearningUiState.value = MixedLearningUiState(
                    screenState = MixedLearningUiState.ScreenState.LEARNING,
                    currentItem = mixedItems.firstOrNull(),
                    currentItemIndex = 0,
                    totalItems = mixedItems.size,
                    currentItemRevealed = false,
                    sessionId = sessionId
                )

                loadProgressData(userId)
                currentItemStartTime = System.currentTimeMillis()
            } catch (e: Exception) {
                _mixedLearningUiState.value = _mixedLearningUiState.value.copy(
                    screenState = MixedLearningUiState.ScreenState.ERROR,
                    errorMessage = "Failed to load mixed learning session: ${e.message}"
                )
            }
        }
    }

    private suspend fun loadProgressData(userId: String) {
        try {
            val vocabProgressMap = mutableMapOf<Long, UserVocabularyProgress>()
            val conceptProgressMap = mutableMapOf<Long, UserConceptProgress>()

            mixedLearningItems.forEach { item ->
                when (item.type) {
                    MixedLearningAlgorithm.LearningType.VOCABULARY -> {
                        val vp = item.content as UserVocabularyProgress
                        vocabProgressMap[vp.wordId] = vp
                    }
                    MixedLearningAlgorithm.LearningType.TOPIC_CONCEPT -> {
                        val cp = item.content as UserConceptProgress
                        conceptProgressMap[cp.conceptId] = cp
                    }
                }
            }

            _mixedLearningUiState.value = _mixedLearningUiState.value.copy(
                vocabProgress = vocabProgressMap,
                conceptProgress = conceptProgressMap
            )
        } catch (_: Exception) { }
    }

    fun submitWordAnswer(difficultyRating: Int) {
        viewModelScope.launch {
            val currentItem = _mixedLearningUiState.value.currentItem ?: return@launch
            val vocabProgress = currentItem.content as UserVocabularyProgress

            val answer = SessionAnswer(
                itemId = currentItem.id,
                itemType = "vocabulary",
                difficultyRating = difficultyRating,
                timeSpent = System.currentTimeMillis() - currentItemStartTime,
                wasCorrect = difficultyRating >= 3,
                timestamp = System.currentTimeMillis()
            )
            sessionAnswers.add(answer)

            updateVocabularyProgress(vocabProgress, difficultyRating)

            val newVocabCompleted = _mixedLearningUiState.value.vocabCompleted + if (difficultyRating >= 3) 1 else 0
            _mixedLearningUiState.value = _mixedLearningUiState.value.copy(
                vocabCompleted = newVocabCompleted,
                accuracy = calculateCurrentAccuracy()
            )
        }
    }

    fun submitConceptAnswer(difficultyRating: Int) {
        viewModelScope.launch {
            val currentItem = _mixedLearningUiState.value.currentItem ?: return@launch
            val conceptProgress = currentItem.content as UserConceptProgress

            val answer = SessionAnswer(
                itemId = currentItem.id,
                itemType = "concept",
                difficultyRating = difficultyRating,
                timeSpent = System.currentTimeMillis() - currentItemStartTime,
                wasCorrect = difficultyRating >= 3,
                timestamp = System.currentTimeMillis()
            )
            sessionAnswers.add(answer)

            updateConceptProgress(conceptProgress, difficultyRating)

            val newConceptsCompleted = _mixedLearningUiState.value.conceptsCompleted + if (difficultyRating >= 3) 1 else 0
            _mixedLearningUiState.value = _mixedLearningUiState.value.copy(
                conceptsCompleted = newConceptsCompleted,
                accuracy = calculateCurrentAccuracy()
            )
        }
    }

    fun revealCurrentItem() { _mixedLearningUiState.value = _mixedLearningUiState.value.copy(currentItemRevealed = true) }

    fun nextItem() {
        viewModelScope.launch {
            try {
                val currentIndex = _mixedLearningUiState.value.currentItemIndex
                val nextIndex = currentIndex + 1
                if (nextIndex >= mixedLearningItems.size) {
                    completeMixedLearningSession()
                } else {
                    val nextItem = mixedLearningItems[nextIndex]
                    _mixedLearningUiState.value = _mixedLearningUiState.value.copy(
                        currentItem = nextItem,
                        currentItemIndex = nextIndex,
                        currentItemRevealed = false,
                        timeRemaining = calculateTimeRemaining()
                    )
                    currentItemStartTime = System.currentTimeMillis()
                }
            } catch (e: Exception) {
                _mixedLearningUiState.value = _mixedLearningUiState.value.copy(
                    screenState = MixedLearningUiState.ScreenState.ERROR,
                    errorMessage = "Failed to load next item: ${e.message}"
                )
            }
        }
    }

    fun endSession() { viewModelScope.launch { completeMixedLearningSession() } }
    fun startNewSession() { loadMixedLearningSession() }

    // Aliases for other callers
    fun nextLearningItem() = nextItem()
    fun endMixedLearningSession() = endSession()
    fun startNewMixedSession() = startNewSession()

    private suspend fun updateVocabularyProgress(vocabProgress: UserVocabularyProgress, difficultyRating: Int) {
        try {
            val quality = when (difficultyRating) {
                5 -> SpacedRepetitionHelper.ReviewQuality.EASY
                4, 3 -> SpacedRepetitionHelper.ReviewQuality.GOOD
                else -> SpacedRepetitionHelper.ReviewQuality.HARD
            }
            val (newReviewCount, nextReviewTime) = SpacedRepetitionHelper.computeNextWithQuality(
                lastReviewDate = vocabProgress.lastReviewedAt,
                reviewCount = vocabProgress.reviewCount,
                quality = quality
            )

            val confidenceDelta = when (difficultyRating) {
                5 -> 2
                4 -> 1
                3 -> 0
                2 -> -1
                1 -> -2
                else -> 0
            }

            val updatedProgress = vocabProgress.copy(
                confidenceLevel = (vocabProgress.confidenceLevel + confidenceDelta).coerceIn(0, 5),
                lastReviewedAt = System.currentTimeMillis(),
                nextReviewAt = nextReviewTime,
                reviewCount = newReviewCount,
                updatedAt = System.currentTimeMillis()
            )

            userVocabularyProgressDao.updateProgress(updatedProgress)

            val review = VocabularyReview(
                userVocabularyProgressId = updatedProgress.id,
                sessionId = currentSession?.id,
                difficultyRating = difficultyRating,
                userAnswer = null,
                correctAnswer = null,
                wasCorrect = difficultyRating >= 3,
                timeSpentSeconds = ((System.currentTimeMillis() - currentItemStartTime) / 1000L).toInt()
            )
            vocabularyReviewDao.insertReview(review)
        } catch (_: Exception) { }
    }

    private suspend fun updateConceptProgress(conceptProgress: UserConceptProgress, difficultyRating: Int) {
        try {
            val quality = when (difficultyRating) {
                5 -> SpacedRepetitionHelper.ReviewQuality.EASY
                4, 3 -> SpacedRepetitionHelper.ReviewQuality.GOOD
                else -> SpacedRepetitionHelper.ReviewQuality.HARD
            }
            val (newReviewCount, nextReviewTime) = SpacedRepetitionHelper.computeNextWithQuality(
                lastReviewDate = conceptProgress.lastReviewedAt,
                reviewCount = conceptProgress.reviewCount,
                quality = quality
            )

            val confidenceDelta = when (difficultyRating) {
                5 -> 2
                4 -> 1
                3 -> 0
                2 -> -1
                1 -> -2
                else -> 0
            }

            val updatedProgress = conceptProgress.copy(
                confidenceLevel = (conceptProgress.confidenceLevel + confidenceDelta).coerceIn(0, 5),
                lastReviewedAt = System.currentTimeMillis(),
                nextReviewAt = nextReviewTime,
                reviewCount = newReviewCount,
                updatedAt = System.currentTimeMillis()
            )

            userConceptProgressDao.updateProgress(updatedProgress)

            val review = ConceptReview(
                userId = getCurrentUserId(),
                conceptId = updatedProgress.conceptId,
                topicId = updatedProgress.topicId,
                sessionId = currentSession?.id,
                difficultyRating = difficultyRating,
                confidenceBefore = conceptProgress.confidenceLevel,
                confidenceAfter = updatedProgress.confidenceLevel,
                timeSpentSeconds = ((System.currentTimeMillis() - currentItemStartTime) / 1000L).toInt(),
                userNotes = null,
                reviewedAt = System.currentTimeMillis()
            )
            conceptReviewDao.insertReview(review)
        } catch (_: Exception) { }
    }

    private suspend fun completeMixedLearningSession() {
        try {
            val session = currentSession ?: return

            val totalAnswers = sessionAnswers.size
            val correctAnswers = sessionAnswers.count { it.wasCorrect }
            val averageResponseTime = if (sessionAnswers.isNotEmpty()) sessionAnswers.map { it.timeSpent }.average().toLong() else 0L

            val completedSession = session.copy(
                endTime = System.currentTimeMillis(),
                correctAnswers = correctAnswers,
                completionPercentage = if (mixedLearningItems.isNotEmpty()) {
                    (sessionAnswers.size.toFloat() / mixedLearningItems.size) * 100f
                } else 0f,
                averageResponseTime = averageResponseTime
            )
            studySessionRepository.updateStudySession(completedSession)

            val vocabAnswers = sessionAnswers.filter { it.itemType == "vocabulary" }
            val conceptAnswers = sessionAnswers.filter { it.itemType == "concept" }
            val vocabAccuracy = if (vocabAnswers.isNotEmpty()) vocabAnswers.count { it.wasCorrect }.toFloat() / vocabAnswers.size else 0f
            val conceptAccuracy = if (conceptAnswers.isNotEmpty()) conceptAnswers.count { it.wasCorrect }.toFloat() / conceptAnswers.size else 0f

            _mixedLearningUiState.value = _mixedLearningUiState.value.copy(
                screenState = MixedLearningUiState.ScreenState.COMPLETED,
                accuracy = calculateCurrentAccuracy(),
                sessionPerformance = MixedLearningAlgorithm.SessionPerformance(
                    totalItems = totalAnswers,
                    correctItems = correctAnswers,
                    timeSpent = System.currentTimeMillis() - sessionStartTime,
                    vocabularyAccuracy = vocabAccuracy,
                    topicAccuracy = conceptAccuracy
                )
            )
        } catch (e: Exception) {
            _mixedLearningUiState.value = _mixedLearningUiState.value.copy(
                screenState = MixedLearningUiState.ScreenState.ERROR,
                errorMessage = "Failed to complete session: ${e.message}"
            )
        }
    }

    private fun calculateCurrentAccuracy(): Int {
        val totalAnswered = _mixedLearningUiState.value.vocabCompleted + _mixedLearningUiState.value.conceptsCompleted
        val totalCorrect = sessionAnswers.count { it.wasCorrect }
        return if (totalAnswered > 0) ((totalCorrect * 100f) / totalAnswered).toInt() else 0
    }

    private fun calculateTimeRemaining(): Int {
        // Placeholder for countdown/timer remaining; implement based on session config
        return 0
    }

    private fun getCurrentUserId(): String = "current_user"

    data class SessionAnswer(
        val itemId: String,
        val itemType: String,
        val difficultyRating: Int,
        val timeSpent: Long,
        val wasCorrect: Boolean,
        val timestamp: Long
    )
}