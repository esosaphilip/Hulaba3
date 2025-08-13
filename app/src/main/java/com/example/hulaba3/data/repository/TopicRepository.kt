package com.example.hulaba3.data.repository

import com.example.hulaba3.data.database.Topic
import com.example.hulaba3.data.database.TopicDao
import kotlinx.coroutines.flow.Flow
class TopicRepository(private val topicDao: TopicDao) {

    fun getAllTopics(): Flow<List<Topic>> = topicDao.getAllTopics()

    suspend fun insertTopic(topic: Topic) {
        topicDao.insertTopic(topic)
    }

    suspend fun updateTopic(topic: Topic) {
        topicDao.updateTopic(topic) // New method for updates
    }

    suspend fun deleteTopic(topic: Topic) {
        topicDao.deleteTopic(topic)
    }
}
