package com.example.hulaba3.data.repository

import com.example.hulaba3.data.database.Word
import com.example.hulaba3.data.database.WordDao
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {
    fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()
    suspend fun insertWord(word: Word): Long = wordDao.insertWord(word)
    suspend fun getWordById(id: Long) = wordDao.getWordById(id)
    suspend fun updateWord(word: Word) = wordDao.updateWord(word)
    suspend fun deleteWord(word: Word) = wordDao.deleteWord(word)
}
