package com.example.hulaba3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    // Basic reads
    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :wordId")
    suspend fun getWordById(wordId: Long): Word?

    @Query("SELECT * FROM words WHERE id = :wordId")
    fun getWordByIdFlow(wordId: Long): Flow<Word?>

    @Query("SELECT * FROM words WHERE germanWord = :germanWord LIMIT 1")
    suspend fun getWordByGermanWord(germanWord: String): Word?

    @Query("SELECT * FROM words WHERE englishTranslation = :englishTranslation")
    suspend fun getWordsByEnglishTranslation(englishTranslation: String): List<Word>

    // Queries aligned to actual Word columns
    @Query("SELECT * FROM words WHERE contextId = :contextId ORDER BY createdAt DESC")
    suspend fun getWordsByContextId(contextId: Long): List<Word>

    @Query("SELECT * FROM words WHERE difficultyLevel = :level ORDER BY createdAt DESC")
    suspend fun getWordsByDifficultyLevel(level: String): List<Word>

    @Query("SELECT * FROM words WHERE germanWord LIKE '%' || :query || '%' OR englishTranslation LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    suspend fun searchWords(query: String): List<Word>

    // Basic writes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>): List<Long>

    @Update
    suspend fun updateWord(word: Word)

    @Update
    suspend fun updateWords(words: List<Word>)

    @Delete
    suspend fun deleteWord(word: Word)

    @Delete
    suspend fun deleteWords(words: List<Word>)

    @Query("DELETE FROM words WHERE id = :wordId")
    suspend fun deleteWordById(wordId: Long)
}