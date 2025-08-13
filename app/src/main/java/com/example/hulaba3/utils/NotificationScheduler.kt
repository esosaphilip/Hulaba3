package com.example.hulaba3.utils

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.hulaba3.data.database.Topic
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.notifications.ReminderWorker
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleWordReminder(context: Context, word: Word) {
        val nextAt = SpacedRepetitionHelper.getNextReviewTime(word.lastReviewed, word.reviewCount)
        val initialDelay = (nextAt - System.currentTimeMillis()).coerceAtLeast(0)

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("wordId" to word.id))
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        Log.d("NotificationScheduler", "Word '${word.word}' scheduled in ${initialDelay / 1000 / 60} min")
    }

    fun scheduleTopicReminder(context: Context, topic: Topic) {
        // Simple 1,4,8,14,21 day plan for topics
        val intervalsDays = listOf(1L, 4L, 8L, 14L, 21L)
        val now = System.currentTimeMillis()

        intervalsDays.forEach { days ->
            val initialDelay = (now + days * 24 * 60 * 60 * 1000L - System.currentTimeMillis()).coerceAtLeast(0)
            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("topicId" to topic.id))
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
            Log.d("NotificationScheduler", "Topic '${topic.title}' scheduled in $days days")
        }
    }
}
