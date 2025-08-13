package com.example.hulaba3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WordDao {
    @Insert
    suspend fun insertWord(word: Word): Long

    @Query("SELECT * FROM words")
    fun getAllWords(): kotlinx.coroutines.flow.Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)
}
