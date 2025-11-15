package com.example.hulaba3.uilayer.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.AppDatabase
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.data.repositories.SpeakingPracticeRepository
import com.example.hulaba3.data.repository.WordRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.IOException
import java.util.*
import android.os.Bundle

sealed class SpeakingPracticeUiState {
    data object Loading : SpeakingPracticeUiState()
    
    data class Practicing(
        val currentWord: Word? = null,
        val currentWordIndex: Int = 0,
        val totalWords: Int = 0,
        val isRecording: Boolean = false,
        val recordingTime: Int = 0,
        val recordingComplete: Boolean = false,
        val recordingConfidence: Float = 0f,
        val recordingTranscription: String = "",
        val recordingFeedback: String = "",
        val sessionPerformance: SessionPerformance = SessionPerformance()
    ) : SpeakingPracticeUiState()
    
    data class Completed(
        val sessionPerformance: SessionPerformance
    ) : SpeakingPracticeUiState()
    
    data class Error(
        val errorMessage: String
    ) : SpeakingPracticeUiState()
    
    enum class ScreenState {
        LOADING, PRACTICING, COMPLETED, ERROR
    }
    
    val screenState: ScreenState
        get() = when (this) {
            is Loading -> ScreenState.LOADING
            is Practicing -> ScreenState.PRACTICING
            is Completed -> ScreenState.COMPLETED
            is Error -> ScreenState.ERROR
        }
}

data class SessionPerformance(
    val wordsPracticed: Int = 0,
    val averageConfidence: Float = 0f,
    val timeSpent: Int = 0,
    val correctPronunciations: Int = 0,
    val totalAttempts: Int = 0
)

class SpeakingPracticeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val wordRepository = WordRepository(database.wordDao())
    private val speakingRepository = SpeakingPracticeRepository(getApplication(), database.speakingPracticeDao())
    
    private val _speakingUiState = MutableStateFlow<SpeakingPracticeUiState>(SpeakingPracticeUiState.Loading)
    val speakingUiState: StateFlow<SpeakingPracticeUiState> = _speakingUiState.asStateFlow()
    
    private var currentWords: List<Word> = emptyList()
    private var currentWordIndex: Int = 0
    private var sessionStartTime: Long = 0L
    private var recordingStartTime: Long = 0L
    private var recordingTimer: Job? = null
    
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    
    init {
        initializeTextToSpeech()
        initializeSpeechRecognizer()
        loadPracticeSession()
    }
    
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.GERMAN
                textToSpeech?.setSpeechRate(0.8f)
                textToSpeech?.setPitch(1.0f)
            }
        }
    }
    
    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
        speechRecognizer?.setRecognitionListener(createRecognitionListener())
    }
    
    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            handleSpeechError(error)
        }
        override fun onResults(results: Bundle?) {
            handleSpeechResults(results)
        }
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
    
    private fun handleSpeechResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val confidenceScores = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
        
        if (!matches.isNullOrEmpty() && confidenceScores != null && confidenceScores.isNotEmpty()) {
            val transcription = matches[0]
            val confidence = confidenceScores[0]
            
            val currentWord = (speakingUiState.value as? SpeakingPracticeUiState.Practicing)?.currentWord
            if (currentWord != null) {
                val feedback = generatePronunciationFeedback(currentWord, transcription, confidence)
                val similarity = calculatePronunciationSimilarity(currentWord.germanWord, transcription)
                val finalConfidence = (confidence + similarity) / 2f
                
                updateCurrentState { current ->
                    (current as? SpeakingPracticeUiState.Practicing)?.copy(
                        recordingComplete = true,
                        recordingTranscription = transcription,
                        recordingConfidence = finalConfidence,
                        recordingFeedback = feedback
                    ) ?: current
                }
                
                // Save speaking practice attempt using repository API
                viewModelScope.launch {
                    speakingRepository.saveSpeakingPractice(
                        wordId = currentWord.id.toString(),
                        recordingFilePath = audioFile?.absolutePath ?: "",
                        targetText = currentWord.germanWord,
                        userText = transcription,
                        accuracyScore = finalConfidence,
                        fluencyScore = finalConfidence,
                        pronunciationScore = finalConfidence,
                        feedback = feedback
                    )
                }
            }
        }
    }
    
    private fun handleSpeechError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
        
        updateCurrentState { current ->
            (current as? SpeakingPracticeUiState.Practicing)?.copy(
                recordingComplete = true,
                recordingFeedback = "Error: $errorMessage. Please try again."
            ) ?: current
        }
    }
    
    private fun calculatePronunciationSimilarity(expected: String, actual: String): Float {
        // Simple similarity calculation based on character matching
        val expectedNormalized = expected.lowercase(Locale.getDefault()).replace("[^a-zäöüß]".toRegex(), "")
        val actualNormalized = actual.lowercase(Locale.getDefault()).replace("[^a-zäöüß]".toRegex(), "")
        
        if (expectedNormalized == actualNormalized) return 1.0f
        
        // Levenshtein distance for similarity
        val maxLength = maxOf(expectedNormalized.length, actualNormalized.length)
        val distance = levenshteinDistance(expectedNormalized, actualNormalized)
        return 1.0f - (distance.toFloat() / maxLength.toFloat())
    }
    
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,     // deletion
                    dp[i][j - 1] + 1,     // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        
        return dp[s1.length][s2.length]
    }
    
    private fun generatePronunciationFeedback(word: Word, transcription: String, confidence: Float): String {
        val similarity = calculatePronunciationSimilarity(word.germanWord, transcription)
        val finalScore = (confidence + similarity) / 2f
        
        return when {
            finalScore >= 0.9f -> "Excellent! Your pronunciation is very close to native."
            finalScore >= 0.8f -> "Great job! Your pronunciation is clear and accurate."
            finalScore >= 0.7f -> "Good effort! Minor adjustments needed for better accuracy."
            finalScore >= 0.6f -> "Fair attempt. Focus on vowel sounds and stress patterns."
            finalScore >= 0.5f -> "Keep practicing! Work on individual sounds and rhythm."
            else -> "More practice needed. Listen to the audio and repeat slowly."
        }
    }
    
    fun loadPracticeSession() {
        viewModelScope.launch {
            try {
                _speakingUiState.value = SpeakingPracticeUiState.Loading
                sessionStartTime = System.currentTimeMillis()
                
                // Get words for speaking practice (prioritize words with audio and pronunciation guides)
                currentWords = wordRepository.getWordsForSpeakingPractice(limit = 10)
                currentWordIndex = 0
                
                if (currentWords.isEmpty()) {
                    _speakingUiState.value = SpeakingPracticeUiState.Error("No words available for speaking practice")
                    return@launch
                }
                
                _speakingUiState.value = SpeakingPracticeUiState.Practicing(
                    currentWord = currentWords.firstOrNull(),
                    currentWordIndex = 0,
                    totalWords = currentWords.size
                )
            } catch (e: Exception) {
                _speakingUiState.value = SpeakingPracticeUiState.Error("Failed to load practice session: ${e.message}")
            }
        }
    }
    
    fun startRecording() {
        viewModelScope.launch {
            try {
                // Initialize media recorder for audio recording
                val context = getApplication<Application>()
                audioFile = File.createTempFile("speech_recording", ".3gp", context.cacheDir)
                
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(audioFile?.absolutePath)
                    prepare()
                    start()
                }
                
                recordingStartTime = System.currentTimeMillis()
                startRecordingTimer()
                
                updateCurrentState { current ->
                    (current as? SpeakingPracticeUiState.Practicing)?.copy(
                        isRecording = true,
                        recordingTime = 0,
                        recordingComplete = false
                    ) ?: current
                }
            } catch (e: IOException) {
                updateCurrentState { current ->
                    (current as? SpeakingPracticeUiState.Practicing)?.copy(
                        recordingFeedback = "Failed to start recording: ${e.message}"
                    ) ?: current
                }
            }
        }
    }
    
    fun stopRecording() {
        viewModelScope.launch {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null
                recordingTimer?.cancel()
                
                // Start speech recognition
                startSpeechRecognition()
                
                updateCurrentState { current ->
                    (current as? SpeakingPracticeUiState.Practicing)?.copy(
                        isRecording = false
                    ) ?: current
                }
            } catch (e: Exception) {
                updateCurrentState { current ->
                    (current as? SpeakingPracticeUiState.Practicing)?.copy(
                        recordingFeedback = "Recording error: ${e.message}"
                    ) ?: current
                }
            }
        }
    }
    
    private fun startSpeechRecognition() {
        val context = getApplication<Application>()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE") // German language
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        speechRecognizer?.startListening(intent)
    }
    
    private fun startRecordingTimer() {
        recordingTimer = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                updateCurrentState { current ->
                    (current as? SpeakingPracticeUiState.Practicing)?.let { state ->
                        val newTime = state.recordingTime + 1
                        if (newTime >= 30) { // Max 30 seconds
                            stopRecording()
                            state.copy(recordingTime = newTime)
                        } else {
                            state.copy(recordingTime = newTime)
                        }
                    } ?: current
                }
            }
        }
    }
    
    fun playAudio() {
        viewModelScope.launch {
            val currentWord = (speakingUiState.value as? SpeakingPracticeUiState.Practicing)?.currentWord
            if (currentWord != null) {
                textToSpeech?.speak(currentWord.germanWord, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }
    
    fun nextWord() {
        viewModelScope.launch {
            currentWordIndex++
            
            if (currentWordIndex >= currentWords.size) {
                completeSession()
            } else {
                updateCurrentState { current ->
                    (current as? SpeakingPracticeUiState.Practicing)?.copy(
                        currentWord = currentWords.getOrNull(currentWordIndex),
                        currentWordIndex = currentWordIndex,
                        recordingComplete = false,
                        recordingTranscription = "",
                        recordingConfidence = 0f,
                        recordingFeedback = ""
                    ) ?: current
                }
            }
        }
    }
    
    fun skipWord() {
        viewModelScope.launch {
            nextWord()
        }
    }
    
    fun startNewSession() {
        loadPracticeSession()
    }
    
    private fun completeSession() {
        val sessionEndTime = System.currentTimeMillis()
        val timeSpent = ((sessionEndTime - sessionStartTime) / 1000).toInt()
        
        val performance = SessionPerformance(
            wordsPracticed = currentWords.size,
            averageConfidence = calculateAverageConfidence(),
            timeSpent = timeSpent,
            correctPronunciations = calculateCorrectPronunciations(),
            totalAttempts = currentWords.size
        )
        
        _speakingUiState.value = SpeakingPracticeUiState.Completed(performance)
        
        // TODO: Optionally persist session summary if/when repository supports it
    }
    
    private fun calculateAverageConfidence(): Float {
        // This would be calculated based on actual recording results
        return 0.75f // Placeholder
    }
    
    private fun calculateCorrectPronunciations(): Int {
        // This would be calculated based on confidence thresholds
        return 6 // Placeholder
    }
    
    private fun updateCurrentState(update: (SpeakingPracticeUiState) -> SpeakingPracticeUiState) {
        _speakingUiState.value = update(_speakingUiState.value)
    }
    
    override fun onCleared() {
        super.onCleared()
        textToSpeech?.shutdown()
        speechRecognizer?.destroy()
        mediaRecorder?.release()
        recordingTimer?.cancel()
    }
}
