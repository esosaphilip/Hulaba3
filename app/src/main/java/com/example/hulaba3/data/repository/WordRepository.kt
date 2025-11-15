package com.example.hulaba3.data.repository

import com.example.hulaba3.data.database.Word
import com.example.hulaba3.data.database.WordDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WordRepository(private val wordDao: WordDao) {
    fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()
    suspend fun insertWord(word: Word): Long = wordDao.insertWord(word)
    suspend fun getWordById(id: Long) = wordDao.getWordById(id)
    suspend fun updateWord(word: Word) = wordDao.updateWord(word)
    suspend fun deleteWord(word: Word) = wordDao.deleteWord(word)

    // Provides a curated list of words suitable for speaking practice.
    // Filters words that have either an audio reference or a pronunciation hint.
    // Falls back gracefully if none are available.
    suspend fun getWordsForSpeakingPractice(limit: Int): List<Word> {
        return try {
            val all = wordDao.getAllWords().first()
            val candidates = all.filter { it.audioUrl != null || !it.pronunciation.isNullOrBlank() }
            (if (candidates.isNotEmpty()) candidates else all).take(limit)
        } catch (e: Exception) {
            // In case of any DB/Flow issues, return an empty list to avoid crashes
            emptyList()
        }
    }
}
