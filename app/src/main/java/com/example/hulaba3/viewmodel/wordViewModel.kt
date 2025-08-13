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
        .map { it.sortedBy { w -> w.word } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // FIXED: Insert word with proper notification scheduling
    fun insertWordWithNotification(context: Context, word: Word) {
        viewModelScope.launch {
            try {
                val wordId = repo.insertWord(word)
                val insertedWord = word.copy(id = wordId)

                // Schedule the first notification
                NotificationScheduler.scheduleWordReminder(context, insertedWord)
                Log.d("WordViewModel", "Word '${word.word}' inserted and notification scheduled")
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
                val currentTime = System.currentTimeMillis()
                val newReviewCount = word.reviewCount + 1
                val nextReviewTime = SpacedRepetitionHelper.getNextReviewTime(currentTime, newReviewCount)

                val updatedWord = word.copy(
                    lastReviewed = currentTime,
                    reviewCount = newReviewCount,
                    nextReviewTime = nextReviewTime
                )

                repo.updateWord(updatedWord)

                // CRITICAL: Schedule the next notification
                NotificationScheduler.scheduleWordReminder(context, updatedWord)
                Log.d("WordViewModel", "Word '${word.word}' reviewed and next notification scheduled")
            } catch (e: Exception) {
                Log.e("WordViewModel", "Error updating word: ${e.localizedMessage}")
            }
        }
    }
}