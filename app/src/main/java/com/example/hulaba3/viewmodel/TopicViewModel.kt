package com.example.hulaba3.viewmodel

import com.example.hulaba3.utils.NotificationScheduler
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.Topic
import com.example.hulaba3.data.repository.TopicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TopicViewModel(
    private val topicRepository: TopicRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    val allTopics: StateFlow<List<Topic>> = topicRepository.getAllTopics()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Function to insert a topic and schedule notifications
    fun insertTopic(context: Context, topic: Topic) {
        viewModelScope.launch {
            try {
                topicRepository.insertTopic(topic)  // Save the topic
                Log.d("TopicViewModel", "Topic inserted successfully: ${topic.title}")

                notificationScheduler.scheduleTopicReminder(context, topic)
                Log.d("TopicViewModel", "Notification scheduled successfully for: ${topic.title}")

            } catch (e: Exception) {
                Log.e("TopicViewModel", "Error inserting topic: ${e.localizedMessage}")
            }
        }
    }

    fun updateTopic(topic: Topic) {
        viewModelScope.launch {
            try {
                topicRepository.updateTopic(topic) // Update topic (with review time)
                Log.d("TopicViewModel", "Topic updated successfully: ${topic.title}")
            } catch (e: Exception) {
                Log.e("TopicViewModel", "Error updating topic: ${e.localizedMessage}")
            }
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch(Dispatchers.IO) {
            topicRepository.deleteTopic(topic)
        }
    }
}
