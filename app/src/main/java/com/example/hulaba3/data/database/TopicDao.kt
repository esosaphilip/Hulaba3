package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY createdAt DESC")
    fun getAllTopics(): Flow<List<Topic>>

    // Topic operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: Topic)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<Topic>)

    @Update
    suspend fun updateTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @Query("DELETE FROM topics WHERE id = :topicId")
    suspend fun deleteTopicById(topicId: String)

    @Query("SELECT * FROM topics WHERE id = :topicId")
    suspend fun getTopicById(topicId: String): Topic?

    @Query("SELECT * FROM topics WHERE id = :topicId")
    fun getTopicByIdFlow(topicId: String): Flow<Topic?>

    @Query("SELECT * FROM topics WHERE title = :title LIMIT 1")
    suspend fun getTopicByTitle(title: String): Topic?

    // Simple topic queries aligned to current Topic schema
    @Query("SELECT * FROM topics WHERE categoryId = :categoryId ORDER BY title ASC")
    suspend fun getTopicsByCategory(categoryId: Long): List<Topic>

    @Query("SELECT * FROM topics WHERE categoryId = :categoryId ORDER BY title ASC")
    fun getTopicsByCategoryFlow(categoryId: Long): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') ORDER BY title ASC")
    suspend fun searchTopics(query: String): List<Topic>

    @Query("SELECT * FROM topics WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') ORDER BY title ASC")
    fun searchTopicsFlow(query: String): Flow<List<Topic>>

    @Query("SELECT * FROM topics ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomTopics(limit: Int): List<Topic>

    @Query("SELECT COUNT(*) FROM topics")
    suspend fun getTopicsCount(): Int

    @Query("SELECT COUNT(*) FROM topics WHERE categoryId = :categoryId")
    suspend fun getTopicsCountByCategory(categoryId: Long): Int
}