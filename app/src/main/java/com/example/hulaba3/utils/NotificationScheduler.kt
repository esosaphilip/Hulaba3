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
        try {
            val currentTime = System.currentTimeMillis()
            val initialDelay = (word.nextReviewTime - currentTime).coerceAtLeast(0)

            if (initialDelay == 0L) {
                Log.w("NotificationScheduler", "Word '${word.word}' review time is in the past, scheduling for 1 minute from now")
                val delayMinutes = 1L // Schedule for 1 minute from now for testing

                val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                    .setInputData(workDataOf("wordId" to word.id))
                    .addTag("word_reminder_${word.id}")
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
                Log.d("NotificationScheduler", "Word '${word.word}' scheduled in $delayMinutes minutes")
            } else {
                val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf("wordId" to word.id))
                    .addTag("word_reminder_${word.id}")
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
                Log.d("NotificationScheduler", "Word '${word.word}' scheduled in ${initialDelay / 1000 / 60} minutes")
            }
        } catch (e: Exception) {
            Log.e("NotificationScheduler", "Error scheduling word reminder: ${e.localizedMessage}")
        }
    }

    fun scheduleTopicReminder(context: Context, topic: Topic) {
        try {
            val intervalsDays = listOf(1L, 4L, 8L, 14L, 21L)
            val now = System.currentTimeMillis()

            intervalsDays.forEach { days ->
                val initialDelay = (days * 24 * 60 * 60 * 1000L).coerceAtLeast(60000) // At least 1 minute

                val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf("topicId" to topic.id)) // topic.id is String
                    .addTag("topic_reminder_${topic.id}_${days}days")
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
                Log.d("NotificationScheduler", "Topic '${topic.title}' scheduled in $days days")
            }
        } catch (e: Exception) {
            Log.e("NotificationScheduler", "Error scheduling topic reminder: ${e.localizedMessage}")
        }
    }

    // Utility method to cancel all reminders for a word
    fun cancelWordReminders(context: Context, wordId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag("word_reminder_$wordId")
        Log.d("NotificationScheduler", "Cancelled reminders for word ID: $wordId")
    }

    // Utility method to cancel all reminders for a topic
    fun cancelTopicReminders(context: Context, topicId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag("topic_reminder_$topicId")
        Log.d("NotificationScheduler", "Cancelled reminders for topic ID: $topicId")
    }
}