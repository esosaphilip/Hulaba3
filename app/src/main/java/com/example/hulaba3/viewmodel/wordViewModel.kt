package com.example.hulaba3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.data.repository.WordRepository
import com.example.hulaba3.utils.SpacedRepetitionHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WordViewModel(private val repo: WordRepository) : ViewModel() {

    val allWords: StateFlow<List<Word>> = repo.getAllWords()
        .map { it.sortedBy { w -> w.word } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun insertWord(word: Word): Long = repo.insertWord(word)

    suspend fun getWordById(id: Long): Word? = repo.getWordById(id)

    fun updateWord(word: Word) = viewModelScope.launch { repo.updateWord(word) }

    fun deleteWord(word: Word) = viewModelScope.launch { repo.deleteWord(word) }

    // --- Updates a word when it is reviewed ---
    fun updateLastReviewed(word: Word) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val newReviewCount = word.reviewCount + 1
            val nextReviewTime = SpacedRepetitionHelper.getNextReviewTime(currentTime, newReviewCount)

            val updatedWord = word.copy(
                lastReviewed = currentTime,
                reviewCount = newReviewCount,
                nextReviewTime = nextReviewTime
            )

            repo.updateWord(updatedWord)
        }
    }
}
