package com.example.hulaba3.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hulaba3.MainActivity
import com.example.hulaba3.R

object NotificationHelper {
    private const val CHANNEL_ID = "smart_review"
    private const val CHANNEL_NAME = "Smart Review"
    private const val CHANNEL_DESCRIPTION = "Smart notifications for quick review and scheduling"

    fun createNotificationChannel(context: Context) {
        try {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH // Changed to HIGH for better visibility
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("NotificationHelper", "Notification channel created successfully")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error creating notification channel: ${e.localizedMessage}")
        }
    }

    fun showNotification(context: Context, title: String, message: String) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(), // Unique request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build()

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notification)

            Log.d("NotificationHelper", "Notification sent: $title")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error showing notification: ${e.localizedMessage}")
        }
    }

    // New: Smart Word notification with actions and deep link
    fun showSmartWordNotification(
        context: Context,
        wordId: Long,
        wordText: String,
        streakDays: Int? = null,
        nextReviewMinutes: Long? = null
    ) {
        try {
            // Content deep link to Smart Word Card
            val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                data = android.net.Uri.parse("hulaba://smartWord/$wordId")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                (wordId xor System.currentTimeMillis()).toInt(),
                deepLinkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Quick Review action
            val quickReviewIntent = Intent(context, com.example.hulaba3.notifications.SmartNotificationReceiver::class.java).apply {
                action = com.example.hulaba3.notifications.SmartNotificationReceiver.ACTION_QUICK_REVIEW
                putExtra(com.example.hulaba3.notifications.SmartNotificationReceiver.EXTRA_WORD_ID, wordId)
            }
            val quickReviewPending = PendingIntent.getBroadcast(
                context,
                (wordId * 31 + 1).toInt(),
                quickReviewIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Save Later action
            val saveLaterIntent = Intent(context, com.example.hulaba3.notifications.SmartNotificationReceiver::class.java).apply {
                action = com.example.hulaba3.notifications.SmartNotificationReceiver.ACTION_SAVE_LATER
                putExtra(com.example.hulaba3.notifications.SmartNotificationReceiver.EXTRA_WORD_ID, wordId)
            }
            val saveLaterPending = PendingIntent.getBroadcast(
                context,
                (wordId * 31 + 2).toInt(),
                saveLaterIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val subtitle = buildString {
                append("🧠 Time for a brain snack!\n\nWord to review: \"")
                append(wordText)
                append("\"\n")
                if (streakDays != null) {
                    append("Streak: 🔥 ")
                    append(streakDays)
                    append(" days\n")
                }
                if (nextReviewMinutes != null) {
                    append("Next review: ")
                    append(nextReviewMinutes)
                    append(" minutes\n")
                }
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Hulaba3")
                .setContentText("Tap to review \"$wordText\"")
                .setStyle(NotificationCompat.BigTextStyle().bigText(subtitle))
                .setContentIntent(contentPendingIntent)
                .addAction(0, "Quick Review", quickReviewPending)
                .addAction(0, "📍 Save Later", saveLaterPending)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            val notification = builder.build()
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            val notificationId = (wordId % Int.MAX_VALUE).toInt()
            notificationManager.notify(notificationId, notification)
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error showing smart word notification: ${e.localizedMessage}")
        }
    }
}