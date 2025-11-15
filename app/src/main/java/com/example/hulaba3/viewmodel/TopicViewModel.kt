package com.example.hulaba3.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.database.StudyMaterial
import com.example.hulaba3.data.database.Topic
import com.example.hulaba3.data.repository.StudyMaterialRepository
import com.example.hulaba3.data.repository.TopicRepository
import com.example.hulaba3.utils.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TopicViewModel(
    private val topicRepository: TopicRepository,
    private val studyMaterialRepository: StudyMaterialRepository
) : ViewModel() { // Removed NotificationScheduler parameter since it's an object

    val allTopics: StateFlow<List<Topic>> = topicRepository.getAllTopics()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insertTopic(context: Context, topic: Topic) {
        viewModelScope.launch {
            try {
                topicRepository.insertTopic(topic)
                Log.d("TopicViewModel", "Topic inserted successfully: ${topic.title}")

                // Schedule topic notifications
                NotificationScheduler.scheduleTopicReminder(context, topic)
                Log.d("TopicViewModel", "Notification scheduled successfully for: ${topic.title}")
            } catch (e: Exception) {
                Log.e("TopicViewModel", "Error inserting topic: ${e.localizedMessage}")
            }
        }
    }

    fun updateTopic(topic: Topic) {
        viewModelScope.launch {
            try {
                topicRepository.updateTopic(topic)
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

    /**
     * Attach a PDF to a topic as a StudyMaterial and optionally set it as the primary material.
     */
    fun attachPdfToTopic(
        topicId: String,
        pdfUri: Uri,
        title: String? = null,
        makePrimary: Boolean = true
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val material = StudyMaterial(
                    topicId = topicId,
                    title = title ?: "Study PDF",
                    fileUrl = pdfUri.toString(),
                    fileType = "pdf",
                    isPrimary = makePrimary,
                    orderIndex = 0
                )

                if (makePrimary) {
                    studyMaterialRepository.clearPrimaryMaterials(topicId)
                }

                val id = studyMaterialRepository.insertMaterial(material)
                if (makePrimary) {
                    studyMaterialRepository.setAsPrimary(id)
                }
                Log.d("TopicViewModel", "Attached PDF to topic=$topicId as materialId=$id")
            } catch (e: Exception) {
                Log.e("TopicViewModel", "Failed to attach PDF: ${e.localizedMessage}")
            }
        }
    }
}
