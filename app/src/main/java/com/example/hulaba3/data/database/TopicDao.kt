package com.example.hulaba3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Insert
    suspend fun insertTopic(topic: Topic)

    @Query("SELECT * FROM topics")
    fun getAllTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE id = :topicId")
    suspend fun getTopicById(topicId: String): Topic? // Fixed: Changed from Long to String

    @Update
    suspend fun updateTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)
}
