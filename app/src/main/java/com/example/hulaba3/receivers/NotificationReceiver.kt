package com.example.hulaba3.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.hulaba3.MainActivity
import com.example.hulaba3.R
// Note: class-based NotificationScheduler under notifications package has been deprecated.
// Repeating scheduling is now handled by WorkManager within utils.NotificationScheduler as needed.
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {
    
    companion object {
        const val CHANNEL_ID = "hulaba_learning_channel"
        const val CHANNEL_NAME = "Hulaba Learning Notifications"
        const val CHANNEL_DESCRIPTION = "Notifications for German learning reminders and updates"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getStringExtra("notification_id") ?: return
        val title = intent.getStringExtra("title") ?: "Hulaba Learning"
        val message = intent.getStringExtra("message") ?: "Time to learn German!"
        val type = intent.getStringExtra("type") ?: "general"
        val isRepeating = intent.getBooleanExtra("repeating", false)
        
        // Create notification channel
        createNotificationChannel(context)
        
        // Handle notification based on type
        when (type) {
            "spaced_repetition" -> handleSpacedRepetitionNotification(context, intent, title, message)
            "daily_reminder" -> handleDailyReminderNotification(context, title, message)
            "milestone" -> handleMilestoneNotification(context, intent, title, message)
            "context_aware" -> handleContextAwareNotification(context, intent, title, message)
            "learning_mode_reminder" -> handleLearningModeReminder(context, intent, title, message)
            else -> handleGeneralNotification(context, notificationId, title, message)
        }
        
        // Reschedule repeating notifications if needed
        if (isRepeating) {
            rescheduleRepeatingNotification(context, intent)
        }
        
        // Track notification received
        trackNotificationReceived(context, notificationId, type)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
                )
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun handleSpacedRepetitionNotification(
        context: Context,
        intent: Intent,
        title: String,
        message: String
    ) {
        val wordId = intent.getStringExtra("context_wordId")?.toLongOrNull()
        
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "mixed_learning")
            putExtra("word_id", wordId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val reviewNowIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "REVIEW_NOW"
            putExtra("word_id", wordId)
        }
        
        val reviewNowPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            reviewNowIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "SNOOZE"
            putExtra("notification_id", intent.getStringExtra("notification_id"))
            putExtra("word_id", wordId)
        }
        
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_view, "Review Now", reviewNowPendingIntent)
            .addAction(android.R.drawable.ic_menu_recent_history, "Snooze", snoozePendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(intent.getStringExtra("notification_id")?.hashCode() ?: 0, notification)
        }
    }
    
    private fun handleDailyReminderNotification(context: Context, title: String, message: String) {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "home")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val startLearningIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "START_LEARNING"
        }
        
        val startLearningPendingIntent = PendingIntent.getBroadcast(
            context,
            3,
            startLearningIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_media_play, "Start Learning", startLearningPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify("daily_reminder".hashCode(), notification)
        }
    }
    
    private fun handleMilestoneNotification(
        context: Context,
        intent: Intent,
        title: String,
        message: String
    ) {
        val milestoneType = intent.getStringExtra("context_type")
        val milestoneValue = intent.getStringExtra("context_value")
        
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "progress")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(intent.getStringExtra("notification_id")?.hashCode() ?: 0, notification)
        }
    }
    
    private fun handleContextAwareNotification(
        context: Context,
        intent: Intent,
        title: String,
        message: String
    ) {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "home")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(intent.getStringExtra("notification_id")?.hashCode() ?: 0, notification)
        }
    }
    
    private fun handleLearningModeReminder(
        context: Context,
        intent: Intent,
        title: String,
        message: String
    ) {
        val mode = intent.getStringExtra("context_mode")
        
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", when (mode) {
                "mixed" -> "mixed_learning"
                "tech_german" -> "tech_german"
                "speaking" -> "speaking_practice"
                else -> "home"
            })
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val tryNowIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "TRY_LEARNING_MODE"
            putExtra("mode", mode)
        }
        
        val tryNowPendingIntent = PendingIntent.getBroadcast(
            context,
            4,
            tryNowIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_media_play, "Try Now", tryNowPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(intent.getStringExtra("notification_id")?.hashCode() ?: 0, notification)
        }
    }
    
    private fun handleGeneralNotification(
        context: Context,
        notificationId: String,
        title: String,
        message: String
    ) {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId.hashCode(), notification)
        }
    }
    
    private fun rescheduleRepeatingNotification(context: Context, intent: Intent) {
        // No-op: Repeating notifications are managed by WorkManager and app logic.
        // Leaving this method to avoid breaking existing flows, but not re-scheduling here.
        val type = intent.getStringExtra("type")
        android.util.Log.d("NotificationReceiver", "Repeating notification received for type=$type; rescheduling handled elsewhere")
    }
    
    private fun trackNotificationReceived(context: Context, notificationId: String, type: String) {
        // Track notification analytics
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log notification received event
                // This would typically go to your analytics service
                println("Notification received: $notificationId of type $type")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}