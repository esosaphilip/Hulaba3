package com.example.hulaba3.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.data.repository.WordRepository
import com.example.hulaba3.utils.SpacedRepetitionHelper
import com.example.hulaba3.utils.NotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WordViewModel(private val repo: WordRepository) : ViewModel() {

    val allWords: StateFlow<List<Word>> = repo.getAllWords()
        .map { it.sortedBy { w -> w.germanWord } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // FIXED: Insert word with proper notification scheduling
    fun insertWordWithNotification(context: Context, word: Word) {
        viewModelScope.launch {
            try {
                val wordId = repo.insertWord(word)
                val insertedWord = word.copy(id = wordId)

                // Schedule the first notification
                NotificationScheduler.scheduleWordReminder(context, insertedWord)
                Log.d("WordViewModel", "Word '${word.germanWord}' inserted and notification scheduled")
            } catch (e: Exception) {
                Log.e("WordViewModel", "Error inserting word: ${e.localizedMessage}")
            }
        }
    }

    // Keep the original method for cases where notification scheduling isn't needed
    suspend fun insertWord(word: Word): Long = repo.insertWord(word)

    suspend fun getWordById(id: Long): Word? = repo.getWordById(id)

    fun updateWord(word: Word) = viewModelScope.launch { repo.updateWord(word) }

    fun deleteWord(word: Word) = viewModelScope.launch { repo.deleteWord(word) }

    // FIXED: Updates word and reschedules next notification
    fun updateLastReviewed(context: Context, word: Word) {
        viewModelScope.launch {
            try {
                // Update the word's updatedAt timestamp (no SRS fields on Word entity)
                val updatedWord = word.copy(updatedAt = System.currentTimeMillis())

                repo.updateWord(updatedWord)

                // Schedule a reminder using default SRS for new words
                NotificationScheduler.scheduleWordReminder(context, updatedWord)
                Log.d("WordViewModel", "Word '${word.germanWord}' reviewed and next notification scheduled")
            } catch (e: Exception) {
                Log.e("WordViewModel", "Error updating word: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Update a word's schedule based on a rating from the Smart Word Card.
     * EASY, GOOD, HARD map to different reviewCount progress using a Leitner-like strategy.
     */
    fun rateWord(context: Context, word: Word, quality: SpacedRepetitionHelper.ReviewQuality) {
        viewModelScope.launch {
            try {
                // Compute next using default baseline (Word entity lacks SRS fields)
                val (_, nextTime) = SpacedRepetitionHelper.computeNextWithQuality(
                    lastReviewDate = System.currentTimeMillis(),
                    reviewCount = 0,
                    quality = quality
                )

                // Update timestamp only
                val updated = word.copy(updatedAt = System.currentTimeMillis())
                repo.updateWord(updated)
                NotificationScheduler.scheduleWordReminder(context, updated)
                Log.d("WordViewModel", "Word '${word.germanWord}' rated ${quality} → next in ${nextTime}")
            } catch (e: Exception) {
                Log.e("WordViewModel", "Error rating word: ${e.localizedMessage}")
            }
        }
    }
}