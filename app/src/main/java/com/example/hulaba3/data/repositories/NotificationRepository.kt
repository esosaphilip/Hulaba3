package com.example.hulaba3.data.repositories

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.hulaba3.R
import com.example.hulaba3.data.database.NotificationDao
import com.example.hulaba3.data.database.Notification
import com.example.hulaba3.data.database.NotificationType
import com.example.hulaba3.receivers.NotificationReceiver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.UUID

class NotificationRepository(
    private val context: Context,
    private val notificationDao: NotificationDao
) {
    
    companion object {
        const val CHANNEL_ID = "hulaba_learning_channel"
        const val CHANNEL_NAME = "Hulaba Learning Notifications"
        const val CHANNEL_DESCRIPTION = "Notifications for learning reminders and progress updates"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    suspend fun scheduleLearningReminder(
        title: String,
        message: String,
        delayMinutes: Int,
        notificationType: String,
        contextData: Map<String, String> = emptyMap()
    ): String {
        val notificationId = UUID.randomUUID().toString()
        val triggerTime = System.currentTimeMillis() + (delayMinutes * 60 * 1000L)

        // Save to database using Room Notification entity
        val entity = Notification(
            userId = "local",
            title = title,
            message = message,
            type = NotificationType.STUDY_SUGGESTION,
            isActive = true,
            isScheduled = true,
            scheduledAt = triggerTime
        )

        notificationDao.insertNotification(entity)

        // Schedule with AlarmManager
        scheduleAlarm(notificationId, triggerTime, title, message, notificationType, contextData)

        return notificationId
    }
    
    suspend fun scheduleDailyLearningReminder(
        hour: Int,
        minute: Int,
        title: String = "Time to Learn!",
        message: String = "Ready for your daily German lesson?"
    ): String {
        val notificationId = "daily_reminder_${System.currentTimeMillis()}"

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        // Save to database
        val entity = Notification(
            userId = "local",
            title = title,
            message = message,
            type = NotificationType.STUDY_SUGGESTION,
            isActive = true,
            isScheduled = true,
            scheduledAt = calendar.timeInMillis
        )

        notificationDao.insertNotification(entity)
        scheduleRepeatingAlarm(notificationId, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, title, message, "daily_reminder")
        
        return notificationId
    }
    
    suspend fun scheduleAt(
        title: String,
        message: String,
        scheduledTime: Long,
        notificationType: String,
        contextData: Map<String, String> = emptyMap()
    ): String {
        val notificationId = "smart_" + System.currentTimeMillis()
        val entity = Notification(
            userId = "local",
            title = title,
            message = message,
            type = NotificationType.STUDY_SUGGESTION,
            isActive = true,
            isScheduled = true,
            scheduledAt = scheduledTime
        )
        notificationDao.insertNotification(entity)
        scheduleAlarm(notificationId, scheduledTime, title, message, notificationType, contextData)
        return notificationId
    }

    suspend fun scheduleSpacedRepetitionNotification(
        wordId: String,
        wordText: String,
        reviewTime: Long
    ): String {
        val notificationId = "spaced_rep_${wordId}_${System.currentTimeMillis()}"

        val entity = Notification(
            userId = "local",
            title = "Review Time!",
            message = "Remember: $wordText",
            type = NotificationType.REVIEW_REMINDER,
            isActive = true,
            isScheduled = true,
            scheduledAt = reviewTime
        )

        notificationDao.insertNotification(entity)
        scheduleAlarm(notificationId, reviewTime, "Review Time!", "Remember: $wordText", "spaced_repetition", mapOf("wordId" to wordId))
        
        return notificationId
    }
    
    suspend fun scheduleProgressMilestoneNotification(
        milestoneType: String,
        value: Int
    ): String {
        val notificationId = "milestone_${milestoneType}_${System.currentTimeMillis()}"

        val (title, message) = when (milestoneType) {
            "words_learned" -> "🎉 Milestone Reached!" to "You've learned $value new words!"
            "streak_days" -> "🔥 Streak Alert!" to "$value day learning streak! Keep it up!"
            "topics_completed" -> "📚 Topic Master!" to "You've completed $value topics!"
            else -> "🎯 Progress Update!" to "Great progress on your learning journey!"
        }

        // Save immediate (delivered) notification to DB
        val entity = Notification(
            userId = "local",
            title = title,
            message = message,
            type = NotificationType.ACHIEVEMENT,
            isActive = true,
            isScheduled = false
        )

        notificationDao.insertNotification(entity)
        showImmediateNotification(notificationId.hashCode(), title, message, "milestone")
        
        return notificationId
    }
    
    private fun scheduleAlarm(
        notificationId: String,
        triggerTime: Long,
        title: String,
        message: String,
        notificationType: String,
        contextData: Map<String, String>
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_id", notificationId)
            putExtra("title", title)
            putExtra("message", message)
            putExtra("type", notificationType)
            contextData.forEach { (key, value) ->
                putExtra("context_$key", value)
            }
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }
    
    private fun scheduleRepeatingAlarm(
        notificationId: String,
        triggerTime: Long,
        interval: Long,
        title: String,
        message: String,
        notificationType: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_id", notificationId)
            putExtra("title", title)
            putExtra("message", message)
            putExtra("type", notificationType)
            putExtra("repeating", true)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingIntent)
    }
    
    fun showImmediateNotification(
        notificationId: Int,
        title: String,
        message: String,
        notificationType: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        notificationManager.notify(notificationId, builder.build())
    }
    
    suspend fun cancelNotification(notificationId: String) {
        // Cancel alarm
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        // TODO: Optionally update database to mark as unscheduled/deactivated
    }
    
    suspend fun cancelAllNotifications() {
        // Cancel any repeating or scheduled alarms by best-effort approach
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Since we used dynamic UUIDs for request codes, we cannot enumerate all easily;
        // rely on DB if needed in future. For now, this is a no-op for alarms.
        
        // TODO: Optionally update DB records to unschedule/deactivate using NotificationDao
    }
    
    fun getAllNotifications(): Flow<List<AppNotification>> {
        return notificationDao.getNotificationsByUserFlow("local").map { entities ->
            entities.map { entity ->
                AppNotification(
                    id = entity.id.toString(),
                    title = entity.title,
                    message = entity.message,
                    scheduledTime = entity.scheduledAt ?: 0L,
                    notificationType = entity.type.name,
                    contextData = emptyMap(),
                    isActive = entity.isActive,
                    createdAt = entity.createdAt
                )
            }
        }
    }
    
    fun getActiveNotifications(): Flow<List<AppNotification>> {
        return notificationDao.getNotificationsByUserFlow("local").map { entities ->
            entities.filter { it.isActive }.map { entity ->
                AppNotification(
                    id = entity.id.toString(),
                    title = entity.title,
                    message = entity.message,
                    scheduledTime = entity.scheduledAt ?: 0L,
                    notificationType = entity.type.name,
                    contextData = emptyMap(),
                    isActive = entity.isActive,
                    createdAt = entity.createdAt
                )
            }
        }
    }
    
    suspend fun updateNotificationSettings(
        dailyReminderEnabled: Boolean,
        dailyReminderTime: Pair<Int, Int>,
        spacedRepetitionEnabled: Boolean,
        milestoneNotificationsEnabled: Boolean
    ) {
        // Cancel existing daily reminder if disabled
        if (!dailyReminderEnabled) {
            cancelAllNotifications()
        } else {
            // Schedule new daily reminder
            scheduleDailyLearningReminder(
                dailyReminderTime.first,
                dailyReminderTime.second
            )
        }
        
        // Store settings in SharedPreferences
        val prefs = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("daily_reminder_enabled", dailyReminderEnabled)
            putInt("daily_reminder_hour", dailyReminderTime.first)
            putInt("daily_reminder_minute", dailyReminderTime.second)
            putBoolean("spaced_repetition_enabled", spacedRepetitionEnabled)
            putBoolean("milestone_notifications_enabled", milestoneNotificationsEnabled)
            apply()
        }
    }
    
    fun getNotificationSettings(): NotificationSettings {
        val prefs = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        return NotificationSettings(
            dailyReminderEnabled = prefs.getBoolean("daily_reminder_enabled", true),
            dailyReminderTime = Pair(
                prefs.getInt("daily_reminder_hour", 9),
                prefs.getInt("daily_reminder_minute", 0)
            ),
            spacedRepetitionEnabled = prefs.getBoolean("spaced_repetition_enabled", true),
            milestoneNotificationsEnabled = prefs.getBoolean("milestone_notifications_enabled", true)
        )
    }
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val scheduledTime: Long,
    val notificationType: String,
    val contextData: Map<String, String>,
    val isActive: Boolean,
    val createdAt: Long
)

data class NotificationSettings(
    val dailyReminderEnabled: Boolean,
    val dailyReminderTime: Pair<Int, Int>,
    val spacedRepetitionEnabled: Boolean,
    val milestoneNotificationsEnabled: Boolean
)
