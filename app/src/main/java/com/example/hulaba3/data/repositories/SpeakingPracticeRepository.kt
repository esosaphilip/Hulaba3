package com.example.hulaba3.data.repositories

import android.content.Context
import android.media.MediaRecorder
import android.speech.tts.TextToSpeech
import com.example.hulaba3.data.database.SpeakingPracticeDao
import com.example.hulaba3.data.database.SpeakingPracticeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume

class SpeakingPracticeRepository(
    private val context: Context,
    private val speakingPracticeDao: SpeakingPracticeDao
) {
    
    private var textToSpeech: TextToSpeech? = null
    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null
    
    suspend fun initializeTextToSpeech() {
        if (textToSpeech == null) {
            textToSpeech = suspendCancellableCoroutine<TextToSpeech?> { continuation ->
                var createdTts: TextToSpeech? = null
                val listener = TextToSpeech.OnInitListener { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        continuation.resume(createdTts)
                    } else {
                        continuation.resume(null)
                    }
                }
                createdTts = TextToSpeech(context, listener)
            }
        }
    }
    
    suspend fun speakText(text: String, language: String = "de-DE") {
        initializeTextToSpeech()
        
        textToSpeech?.let { tts ->
            val locale = when (language) {
                "de-DE" -> Locale.GERMAN
                "en-US" -> Locale.US
                "es-ES" -> Locale("es", "ES")
                "fr-FR" -> Locale.FRENCH
                else -> Locale.GERMAN
            }
            
            tts.language = locale
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
    
    fun startRecording(wordId: String): String {
        val recordingId = UUID.randomUUID().toString()
        val recordingFile = File(context.filesDir, "recordings/$recordingId.3gp")
        recordingFile.parentFile?.mkdirs()
        
        currentRecordingFile = recordingFile
        
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFile.absolutePath)
            prepare()
            start()
        }
        
        return recordingId
    }
    
    fun stopRecording(): File? {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        
        return currentRecordingFile
    }
    
    suspend fun saveSpeakingPractice(
        wordId: String,
        recordingFilePath: String,
        targetText: String,
        userText: String,
        accuracyScore: Float,
        fluencyScore: Float,
        pronunciationScore: Float,
        feedback: String
    ): String {
        val practiceId = UUID.randomUUID().toString()
        
        val entity = SpeakingPracticeEntity(
            id = practiceId,
            wordId = wordId,
            recordingFilePath = recordingFilePath,
            targetText = targetText,
            userText = userText,
            accuracyScore = accuracyScore,
            fluencyScore = fluencyScore,
            pronunciationScore = pronunciationScore,
            feedback = feedback,
            createdAt = System.currentTimeMillis()
        )
        
        speakingPracticeDao.insertPractice(entity)
        return practiceId
    }
    
    fun getAllPractices(): Flow<List<SpeakingPractice>> {
        return speakingPracticeDao.getAllPractices().map { entities ->
            entities.map { entity ->
                SpeakingPractice(
                    id = entity.id,
                    wordId = entity.wordId,
                    recordingFilePath = entity.recordingFilePath,
                    targetText = entity.targetText,
                    userText = entity.userText,
                    accuracyScore = entity.accuracyScore,
                    fluencyScore = entity.fluencyScore,
                    pronunciationScore = entity.pronunciationScore,
                    feedback = entity.feedback,
                    createdAt = entity.createdAt
                )
            }
        }
    }
    
    suspend fun getPracticeById(practiceId: String): SpeakingPractice? {
        return speakingPracticeDao.getPracticeById(practiceId)?.let { entity ->
            SpeakingPractice(
                id = entity.id,
                wordId = entity.wordId,
                recordingFilePath = entity.recordingFilePath,
                targetText = entity.targetText,
                userText = entity.userText,
                accuracyScore = entity.accuracyScore,
                fluencyScore = entity.fluencyScore,
                pronunciationScore = entity.pronunciationScore,
                feedback = entity.feedback,
                createdAt = entity.createdAt
            )
        }
    }
    
    fun getPracticesForWord(wordId: String): Flow<List<SpeakingPractice>> {
        return speakingPracticeDao.getPracticesForWord(wordId).map { entities ->
            entities.map { entity ->
                SpeakingPractice(
                    id = entity.id,
                    wordId = entity.wordId,
                    recordingFilePath = entity.recordingFilePath,
                    targetText = entity.targetText,
                    userText = entity.userText,
                    accuracyScore = entity.accuracyScore,
                    fluencyScore = entity.fluencyScore,
                    pronunciationScore = entity.pronunciationScore,
                    feedback = entity.feedback,
                    createdAt = entity.createdAt
                )
            }
        }
    }
    
    suspend fun deletePractice(practiceId: String) {
        val practice = speakingPracticeDao.getPracticeById(practiceId)
        practice?.let {
            // Delete the recording file
            File(it.recordingFilePath).delete()
            // Delete from database
            speakingPracticeDao.deletePractice(practiceId)
        }
    }
    
    suspend fun getPracticeStats(): SpeakingPracticeStats {
        val allPractices = speakingPracticeDao.getAllPractices().first()
        val totalPractices = allPractices.size
        
        if (totalPractices == 0) {
            return SpeakingPracticeStats(
                totalPractices = 0,
                averageAccuracy = 0f,
                averageFluency = 0f,
                averagePronunciation = 0f,
                bestScore = 0f,
                improvement = 0f,
                practicesThisWeek = 0,
                practicesThisMonth = 0
            )
        }
        
        val averageAccuracy = allPractices.map { it.accuracyScore }.average().toFloat()
        val averageFluency = allPractices.map { it.fluencyScore }.average().toFloat()
        val averagePronunciation = allPractices.map { it.pronunciationScore }.average().toFloat()
        val bestScore = allPractices.map { 
            (it.accuracyScore + it.fluencyScore + it.pronunciationScore) / 3 
        }.maxOrNull() ?: 0f
        
        val currentTime = System.currentTimeMillis()
        val weekAgo = currentTime - (7 * 24 * 60 * 60 * 1000L)
        val monthAgo = currentTime - (30 * 24 * 60 * 60 * 1000L)
        
        val practicesThisWeek = allPractices.count { it.createdAt > weekAgo }
        val practicesThisMonth = allPractices.count { it.createdAt > monthAgo }
        
        // Calculate improvement (last week vs previous week)
        val lastWeekPractices = allPractices.filter { it.createdAt > weekAgo }
        val previousWeekPractices = allPractices.filter { 
            it.createdAt in (weekAgo - (7 * 24 * 60 * 60 * 1000L))..weekAgo 
        }
        
        val improvement = if (lastWeekPractices.isNotEmpty() && previousWeekPractices.isNotEmpty()) {
            val lastWeekAvg = lastWeekPractices.map { it.accuracyScore }.average()
            val previousWeekAvg = previousWeekPractices.map { it.accuracyScore }.average()
            ((lastWeekAvg - previousWeekAvg) / previousWeekAvg * 100).toFloat()
        } else {
            0f
        }
        
        return SpeakingPracticeStats(
            totalPractices = totalPractices,
            averageAccuracy = averageAccuracy,
            averageFluency = averageFluency,
            averagePronunciation = averagePronunciation,
            bestScore = bestScore,
            improvement = improvement,
            practicesThisWeek = practicesThisWeek,
            practicesThisMonth = practicesThisMonth
        )
    }
    
    fun cleanup() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        
        mediaRecorder?.release()
        mediaRecorder = null
    }
}

data class SpeakingPractice(
    val id: String,
    val wordId: String,
    val recordingFilePath: String,
    val targetText: String,
    val userText: String,
    val accuracyScore: Float,
    val fluencyScore: Float,
    val pronunciationScore: Float,
    val feedback: String,
    val createdAt: Long
)

data class SpeakingPracticeStats(
    val totalPractices: Int,
    val averageAccuracy: Float,
    val averageFluency: Float,
    val averagePronunciation: Float,
    val bestScore: Float,
    val improvement: Float,
    val practicesThisWeek: Int,
    val practicesThisMonth: Int
)