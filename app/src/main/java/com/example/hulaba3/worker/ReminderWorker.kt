package com.example.hulaba3.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hulaba3.data.database.AppDatabase
import com.example.hulaba3.utils.NotificationHelper
import com.example.hulaba3.utils.NotificationScheduler
import com.example.hulaba3.utils.SpacedRepetitionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val database = AppDatabase.getDatabase(appContext)
    private val wordDao = database.wordDao()
    private val topicDao = database.topicDao()

    override suspend fun doWork(): Result {
        Log.d("ReminderWorker", "Worker started")

        return try {
            val wordId = inputData.getLong("wordId", -1L)
            val topicId = inputData.getString("topicId") ?: "" // Fixed: getString instead of getLong

            when {
                wordId != -1L -> handleWordReminder(wordId)
                topicId.isNotEmpty() -> handleTopicReminder(topicId)
                else -> {
                    Log.e("ReminderWorker", "No valid IDs provided")
                    Result.success()
                }
            }
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Worker failed: ${e.localizedMessage}")
            Result.failure()
        }
    }

    private suspend fun handleWordReminder(wordId: Long): Result {
        return try {
            val word = withContext(Dispatchers.IO) {
                wordDao.getWordById(wordId)
            } ?: run {
                Log.w("ReminderWorker", "Word with ID $wordId not found")
                return Result.success()
            }

            // Show notification
            NotificationHelper.showNotification(
                applicationContext,
                "Review Word: ${word.word}",
                "Meaning: ${word.meaning}"
            )

            Log.d("ReminderWorker", "Notification sent for word: ${word.word}")
            Result.success()

        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error handling word reminder: ${e.localizedMessage}")
            Result.failure()
        }
    }

    private suspend fun handleTopicReminder(topicId: String): Result { // Fixed: String parameter
        return try {
            val topic = withContext(Dispatchers.IO) {
                topicDao.getTopicById(topicId) // Now matches String type
            } ?: run {
                Log.w("ReminderWorker", "Topic with ID $topicId not found")
                return Result.success()
            }

            // Show notification
            NotificationHelper.showNotification(
                applicationContext,
                "Review Topic: ${topic.title}",
                "Tap to open your PDF and review this topic."
            )

            Log.d("ReminderWorker", "Notification sent for topic: ${topic.title}")
            Result.success()

        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error handling topic reminder: ${e.localizedMessage}")
            Result.failure()
        }
    }
}
