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
    private const val CHANNEL_ID = "review_reminders"
    private const val CHANNEL_NAME = "Review Reminders"
    private const val CHANNEL_DESCRIPTION = "Reminders to review learned words and topics"

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
}