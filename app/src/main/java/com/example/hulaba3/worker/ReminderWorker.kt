package com.example.hulaba3.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hulaba3.data.database.AppDatabase
import com.example.hulaba3.utils.NotificationHelper
import com.example.hulaba3.utils.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val database = AppDatabase.getDatabase(appContext)
    private val wordDao = database.wordDao()
    private val topicDao = database.topicDao()

    override suspend fun doWork(): Result {
        Log.d("ReminderWorker", "Started")
        val wordId = inputData.getLong("wordId", -1L)
        val topicId = inputData.getLong("topicId", -1L)

        return when {
            wordId != -1L -> handleWordReminder(wordId)
            topicId != -1L -> handleTopicReminder(topicId)
            else -> {
                Log.e("ReminderWorker", "No valid IDs")
                Result.success() // don't fail the chain
            }
        }
    }

    private suspend fun handleWordReminder(wordId: Long): Result {
        val word = withContext(Dispatchers.IO) { wordDao.getWordById(wordId) } ?: return Result.success()

        NotificationHelper.showNotification(
            applicationContext,
            "Review Word: ${word.word}",
            "Meaning: ${word.meaning}"
        )

        // Update lastReviewed and increment reviewCount
        val updated = word.copy(
            lastReviewed = System.currentTimeMillis(),
            reviewCount = word.reviewCount + 1
        )
        withContext(Dispatchers.IO) { wordDao.updateWord(updated) }

        // Schedule next review
        NotificationScheduler.scheduleWordReminder(applicationContext, updated)

        Log.d("ReminderWorker", "Rescheduled for '${word.word}'")
        return Result.success()
    }

    private suspend fun handleTopicReminder(topicId: Long): Result {
        // FIX: pass Long, not String
        val topic = withContext(Dispatchers.IO) { topicDao.getTopicById(topicId) } ?: return Result.success()

        NotificationHelper.showNotification(
            applicationContext,
            "Review Topic: ${topic.title}",
            "Tap to open your PDF."
        )
        return Result.success()
    }
}
