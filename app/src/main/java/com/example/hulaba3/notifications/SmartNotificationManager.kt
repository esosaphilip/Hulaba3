package com.example.hulaba3.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hulaba3.R
import com.example.hulaba3.data.database.AppDatabase
import com.example.hulaba3.data.database.StudySession
import com.example.hulaba3.receivers.NotificationActionReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

class SmartNotificationManager(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "study_channel"
        private const val CHANNEL_NAME = "Study Reminders"
        private const val CHANNEL_DESCRIPTION = "Smart notifications for Hulaba learning"
        private const val TAG = "SmartNotificationManager"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun scheduleSmartNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val wordDao = database.userVocabularyProgressDao()
                val conceptProgressDao = database.userConceptProgressDao()
                val topicProgressDao = database.userTopicProgressDao()
                val studySessionDao = database.studySessionDao()
                val userDao = database.userDao()
                
                val currentTime = System.currentTimeMillis()
                val dayInMillis = 24 * 60 * 60 * 1000
                
                // Resolve current user (fallback to a demo ID if none found)
                val currentUserId = try {
                    userDao.getAllUsers().firstOrNull()?.id ?: "user_123"
                } catch (_: Exception) { "user_123" }

                // Get items due for review
                val dueWords = wordDao.getWordsForReview(currentUserId, currentTime)

                // Concepts need a topic context; gather topics due and fetch concepts for those topics
                val topicsDue = topicProgressDao.getTopicsForReview(currentUserId, currentTime)
                val dueConcepts = topicsDue.flatMap { topicWithProgress ->
                    val topicId = topicWithProgress.topic.id.toString()
                    conceptProgressDao.getConceptsForReview(currentUserId, topicId, currentTime)
                }
                
                // Analyze learning patterns
                val recentSessions = studySessionDao.getRecentSessions()
                val learningPattern = analyzeLearningPattern(recentSessions)
                
                // Schedule notifications based on patterns and due items
                scheduleNotificationsForItems(dueWords, dueConcepts, learningPattern)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error scheduling notifications", e)
            }
        }
    }
    
    private fun analyzeLearningPattern(sessions: List<StudySession>): LearningPattern {
        if (sessions.isEmpty()) {
            return LearningPattern(
                optimalTime = 9 * 60, // 9 AM
                frequency = 3,
                preferredMode = "mixed",
                averageSessionDuration = 15
            )
        }
        
        // Analyze session times
        val hourFrequencies = mutableMapOf<Int, Int>()
        val modeFrequencies = mutableMapOf<String, Int>()
        var totalDuration = 0L
        
        sessions.forEach { session ->
            val calendar = Calendar.getInstance().apply { timeInMillis = session.startTime }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            hourFrequencies[hour] = hourFrequencies.getOrDefault(hour, 0) + 1
            
            val mode = when (session.sessionType.uppercase(Locale.getDefault())) {
                "QUIZ" -> "quiz"
                "REVIEW" -> "review"
                "PRACTICE" -> "practice"
                else -> "mixed"
            }
            modeFrequencies[mode] = modeFrequencies.getOrDefault(mode, 0) + 1
            val duration = (session.endTime ?: session.startTime) - session.startTime
            totalDuration += duration
        }
        
        // Find optimal time (most frequent hour)
        val optimalHour = hourFrequencies.maxByOrNull { it.value }?.key ?: 9
        
        // Find preferred mode
        val preferredMode = modeFrequencies.maxByOrNull { it.value }?.key ?: "mixed"
        
        // Calculate average session duration
        val avgDuration = if (sessions.isNotEmpty()) (totalDuration / sessions.size / 60000).toInt() else 15
        
        return LearningPattern(
            optimalTime = optimalHour * 60,
            frequency = min(5, sessions.size / 7 + 1), // Max 5 notifications per day
            preferredMode = preferredMode,
            averageSessionDuration = avgDuration
        )
    }
    
    private fun scheduleNotificationsForItems(
        words: List<com.example.hulaba3.data.database.WordWithProgress>,
        concepts: List<com.example.hulaba3.data.database.ConceptWithProgress>,
        pattern: LearningPattern
    ) {
        // Compute a unified priority list using confidence (lower first) and next review (earlier first)
        data class Prioritized(
            val kind: String,
            val priorityKey: Pair<Int, Long>,
            val word: com.example.hulaba3.data.database.Word? = null,
            val concept: com.example.hulaba3.data.database.Concept? = null
        )

        val prioritizedWords = words.map {
            Prioritized(
                kind = "word",
                priorityKey = Pair(
                    it.progress.confidenceLevel,
                    it.progress.nextReviewAt
                ),
                word = it.word
            )
        }

        val prioritizedConcepts = concepts.map {
            Prioritized(
                kind = "concept",
                priorityKey = Pair(
                    it.progress.confidenceLevel,
                    it.progress.nextReviewAt ?: Long.MAX_VALUE
                ),
                concept = it.concept
            )
        }

        val all = (prioritizedWords + prioritizedConcepts)
            .sortedWith(compareBy({ it.priorityKey.first }, { it.priorityKey.second }))
            .take(pattern.frequency)

        all.forEachIndexed { index, item ->
            val notificationTime = calculateOptimalNotificationTime(pattern, index)
            when (item.kind) {
                "word" -> item.word?.let { scheduleWordNotification(it, notificationTime) }
                "concept" -> item.concept?.let { scheduleConceptNotification(it, notificationTime) }
            }
        }

        // Schedule general motivation notifications
        scheduleMotivationNotifications(pattern)
    }
    
    private fun calculateOptimalNotificationTime(pattern: LearningPattern, index: Int): Long {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute
        
        // If optimal time has passed today, schedule for tomorrow
        if (currentTimeInMinutes > pattern.optimalTime) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        // Set optimal hour
        calendar.set(Calendar.HOUR_OF_DAY, pattern.optimalTime / 60)
        calendar.set(Calendar.MINUTE, pattern.optimalTime % 60)
        calendar.set(Calendar.SECOND, 0)
        
        // Add offset for multiple notifications
        calendar.add(Calendar.MINUTE, index * 120) // 2 hours apart
        
        return calendar.timeInMillis
    }
    
    private fun scheduleWordNotification(word: com.example.hulaba3.data.database.Word, notificationTime: Long) {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "REVIEW_NOW"
            putExtra("notification_id", word.id.toInt())
            putExtra("item_id", word.id)
            putExtra("item_type", "word")
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            word.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val markKnownIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "MARK_KNOWN"
            putExtra("notification_id", word.id.toInt() + 10000)
            putExtra("item_id", word.id)
            putExtra("item_type", "word")
        }
        
        val markKnownPendingIntent = PendingIntent.getBroadcast(
            context,
            word.id.toInt() + 10000,
            markKnownIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val markUnknownIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "MARK_UNKNOWN"
            putExtra("notification_id", word.id.toInt() + 20000)
            putExtra("item_id", word.id)
            putExtra("item_type", "word")
        }
        
        val markUnknownPendingIntent = PendingIntent.getBroadcast(
            context,
            word.id.toInt() + 20000,
            markUnknownIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = "Review German Word: ${word.germanWord}"
        val message = "${word.englishTranslation} - Tap to practice pronunciation"
        scheduleNotification(
            id = word.id.toInt(),
            title = title,
            message = message,
            type = "spaced_repetition",
            scheduledAt = notificationTime,
            extras = {
                putExtra("context_wordId", word.id.toString())
            }
        )
    }
    
    private fun scheduleConceptNotification(concept: com.example.hulaba3.data.database.Concept, notificationTime: Long) {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "REVIEW_NOW"
            putExtra("notification_id", concept.id.toInt())
            putExtra("item_id", concept.id)
            putExtra("item_type", "concept")
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            concept.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val markKnownIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "MARK_KNOWN"
            putExtra("notification_id", concept.id.toInt() + 30000)
            putExtra("item_id", concept.id)
            putExtra("item_type", "concept")
        }
        
        val markKnownPendingIntent = PendingIntent.getBroadcast(
            context,
            concept.id.toInt() + 30000,
            markKnownIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = "Review Tech Concept: ${concept.title}"
        val msgBody = concept.description?.take(100) ?: "Open to review"
        scheduleNotification(
            id = concept.id.toInt() + 1000,
            title = title,
            message = "$msgBody...",
            type = "context_aware",
            scheduledAt = notificationTime,
            extras = {
                putExtra("context_type", "concept")
                putExtra("context_value", concept.id.toString())
            }
        )
    }
    
    private fun scheduleMotivationNotifications(pattern: LearningPattern) {
        val motivationalMessages = listOf(
            "Ready to learn something new today? 🚀",
            "Your German + Tech journey continues! 📚",
            "Mix it up with vocabulary and concepts! 🎯",
            "Learning streak incoming! 🔥",
            "New challenges await! 💪"
        )
        
        motivationalMessages.forEachIndexed { index, message ->
            val intent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = "START_LEARNING"
                putExtra("notification_id", 90000 + index)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                90000 + index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val tryMixedIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = "TRY_LEARNING_MODE"
                putExtra("notification_id", 95000 + index)
            }
            
            val tryMixedPendingIntent = PendingIntent.getBroadcast(
                context,
                95000 + index,
                tryMixedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notificationTime = calculateOptimalNotificationTime(pattern, index + 10)
            scheduleNotification(
                id = 90000 + index,
                title = "Hulaba Learning Time!",
                message = message,
                type = "daily_reminder",
                scheduledAt = notificationTime
            )
        }
    }
    
    private fun scheduleNotification(
        id: Int,
        title: String,
        message: String,
        type: String,
        scheduledAt: Long? = null,
        extras: (Intent.() -> Unit)? = null
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, com.example.hulaba3.receivers.NotificationReceiver::class.java).apply {
            putExtra("notification_id", id.toString())
            putExtra("title", title)
            putExtra("message", message)
            putExtra("type", type)
            extras?.invoke(this)
        }
        val triggerTime = scheduledAt ?: (System.currentTimeMillis() + 60_000) // default to 1 minute
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
        Log.d(TAG, "Scheduled notification $id for ${Date(triggerTime)}")
    }
    
    fun rescheduleNotification(itemId: Long, itemType: String?, delayMinutes: Int) {
        val newTime = System.currentTimeMillis() + (delayMinutes * 60 * 1000)
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                
                when (itemType) {
                    "word" -> {
                        val word = database.wordDao().getWordById(itemId)
                        word?.let {
                            scheduleWordNotification(it, newTime)
                        }
                    }
                    "concept" -> {
                        val concept = database.conceptDao().getConceptById(itemId)
                        concept?.let {
                            scheduleConceptNotification(it, newTime)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error rescheduling notification", e)
            }
        }
    }
    
    fun cancelAllNotifications() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Cancel all scheduled alarms
        for (id in 1..100000) {
            val intent = Intent(context, com.example.hulaba3.receivers.NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
        
        // Cancel all displayed notifications
        notificationManager.cancelAll()
    }

    // Public API to reschedule all notifications, used by BootReceiver
    fun rescheduleAllNotifications() {
        try {
            // Optionally cancel any existing notifications to avoid duplicates
            cancelAllNotifications()
        } catch (_: Exception) {
            // Safe-guard: continue scheduling even if cancel fails
        }
        // Re-run the smart scheduling based on latest study sessions and items
        scheduleSmartNotifications()
    }
    
    data class LearningPattern(
        val optimalTime: Int, // Minutes from midnight
        val frequency: Int, // Notifications per day
        val preferredMode: String,
        val averageSessionDuration: Int // Minutes
    )
}