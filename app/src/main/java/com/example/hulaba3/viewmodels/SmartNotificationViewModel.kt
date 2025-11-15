package com.example.hulaba3.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hulaba3.data.repositories.NotificationRepository
import com.example.hulaba3.data.repository.WordRepository
import com.example.hulaba3.data.repository.StudySessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Calendar

data class NotificationSettings(
    val enabled: Boolean = true,
    val studyReminders: Boolean = true,
    val practiceReminders: Boolean = true,
    val dailyGoal: Boolean = true,
    val streakReminders: Boolean = true,
    val quietHours: Boolean = true,
    val quietStartHour: Int = 22, // 10 PM
    val quietEndHour: Int = 8,    // 8 AM
    val maxDailyNotifications: Int = 5,
    val contextAware: Boolean = true
)

data class SmartNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val priority: Int,
    val scheduledTime: Long,
    val context: NotificationContext,
    val isRead: Boolean = false
)

enum class NotificationType {
    STUDY_REMINDER,
    PRACTICE_REMINDER,
    DAILY_GOAL,
    STREAK_REMINDER,
    WORD_REVIEW,
    CONTEXT_AWARE,
    MOTIVATIONAL
}

data class NotificationContext(
    val userActivity: UserActivity,
    val learningProgress: LearningProgress,
    val timeOfDay: TimeOfDay,
    val weather: Weather = Weather.UNKNOWN,
    val location: Location = Location.UNKNOWN
)

enum class UserActivity {
    IDLE,
    STUDYING,
    WORKING,
    RELAXING,
    COMMUTING,
    EXERCISING
}

enum class LearningProgress {
    BEHIND_SCHEDULE,
    ON_TRACK,
    AHEAD_SCHEDULE,
    MAINTAINING
}

enum class TimeOfDay {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT
}

enum class Weather {
    SUNNY,
    CLOUDY,
    RAINY,
    SNOWY,
    UNKNOWN
}

enum class Location {
    HOME,
    WORK,
    SCHOOL,
    COMMUTE,
    OUTDOOR,
    UNKNOWN
}

data class SmartNotificationUiState(
    val settings: NotificationSettings = NotificationSettings(),
    val notifications: List<SmartNotification> = emptyList(),
    val unreadCount: Int = 0,
    val nextNotification: SmartNotification? = null,
    val dailyNotificationCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class SmartNotificationViewModel(
    application: Application,
    private val notificationRepository: NotificationRepository,
    private val wordRepository: WordRepository,
    private val studySessionRepository: StudySessionRepository
) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(SmartNotificationUiState())
    val uiState: StateFlow<SmartNotificationUiState> = _uiState.asStateFlow()
    
    private val notificationTemplates = mapOf(
        NotificationType.STUDY_REMINDER to listOf(
            "Time for your German practice! 📚",
            "Ready to learn some new German words?",
            "Your daily German session awaits!",
            "Let's practice German together! 🎯"
        ),
        NotificationType.PRACTICE_REMINDER to listOf(
            "Practice speaking German today! 🗣️",
            "Time to work on your pronunciation",
            "Let's practice those tricky German sounds",
            "Speaking practice makes perfect!"
        ),
        NotificationType.DAILY_GOAL to listOf(
            "You're close to your daily goal! 💪",
            "Just a few more minutes to reach your target",
            "Daily goal almost complete!",
            "You're doing great, keep going!"
        ),
        NotificationType.STREAK_REMINDER to listOf(
            "Don't break your streak! 🔥",
            "Your learning streak is at risk",
            "Keep the momentum going!",
            "You're on fire! Don't stop now!"
        ),
        NotificationType.WORD_REVIEW to listOf(
            "Time to review: {word}",
            "Remember this word? {word}",
            "Quick review: {word} = {translation}",
            "Test yourself: What does {word} mean?"
        ),
        NotificationType.CONTEXT_AWARE to listOf(
            "Perfect time for German while commuting! 🚗",
            "Quick German session during your break?",
            "Learn German while you have a moment",
            "Make the most of your free time"
        ),
        NotificationType.MOTIVATIONAL to listOf(
            "Every word learned is progress! 🌟",
            "You're improving every day!",
            "German mastery is within reach",
            "Keep going, you're doing amazing!"
        )
    )
    
    init {
        loadNotificationSettings()
        loadNotifications()
        scheduleSmartNotifications()
    }
    
    private fun loadNotificationSettings() {
        viewModelScope.launch {
            val repoSettings = notificationRepository.getNotificationSettings()
            val current = _uiState.value.settings
            _uiState.value = _uiState.value.copy(
                settings = current.copy(enabled = repoSettings.dailyReminderEnabled)
            )
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            notificationRepository.getAllNotifications().collect { appNotifications ->
                val smart = appNotifications.map { app ->
                    val type = when {
                        app.notificationType.equals("daily_reminder", true) -> NotificationType.DAILY_GOAL
                        app.notificationType.equals("spaced_repetition", true) -> NotificationType.WORD_REVIEW
                        runCatching { NotificationType.valueOf(app.notificationType) }.getOrNull() != null -> NotificationType.valueOf(app.notificationType)
                        else -> NotificationType.MOTIVATIONAL
                    }
                    createNotification(
                        type = type,
                        title = app.title,
                        message = app.message,
                        priority = 5,
                        scheduledTime = app.scheduledTime
                    )
                }
                val unreadCount = smart.count { !it.isRead }
                val nextNotification = smart.filter { it.scheduledTime > System.currentTimeMillis() }
                    .minByOrNull { it.scheduledTime }
                val dailyCount = smart.count { isToday(it.scheduledTime) }
                _uiState.value = _uiState.value.copy(
                    notifications = smart,
                    unreadCount = unreadCount,
                    nextNotification = nextNotification,
                    dailyNotificationCount = dailyCount
                )
            }
        }
    }
    
    fun updateSettings(newSettings: NotificationSettings) {
        viewModelScope.launch {
            // Map local settings to repository's configuration signature
            val dailyReminderEnabled = newSettings.studyReminders || newSettings.dailyGoal
            val dailyReminderTime = Pair(20, 0) // Default 8:00 PM
            val spacedRepetitionEnabled = true
            val milestoneNotificationsEnabled = newSettings.streakReminders
            notificationRepository.updateNotificationSettings(
                dailyReminderEnabled = dailyReminderEnabled,
                dailyReminderTime = dailyReminderTime,
                spacedRepetitionEnabled = spacedRepetitionEnabled,
                milestoneNotificationsEnabled = milestoneNotificationsEnabled
            )
            if (newSettings.enabled) {
                scheduleSmartNotifications()
            } else {
                cancelAllNotifications()
            }
        }
    }
    
    fun markNotificationRead(notificationId: String) {
        viewModelScope.launch {
            // Repository does not support 'mark as read'; update UI state locally
            val updated = _uiState.value.notifications.map { n ->
                if (n.id == notificationId) n.copy(isRead = true) else n
            }
            val unreadCount = updated.count { !it.isRead }
            _uiState.value = _uiState.value.copy(
                notifications = updated,
                unreadCount = unreadCount
            )
        }
    }
    
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.cancelNotification(notificationId)
        }
    }
    
    private fun scheduleSmartNotifications() {
        if (!_uiState.value.settings.enabled) return
        
        viewModelScope.launch {
            val context = analyzeCurrentContext()
            val notifications = generateSmartNotifications(context)
            
            notifications.forEach { notification ->
                notificationRepository.scheduleAt(notification.title, notification.message, notification.scheduledTime, notification.type.name)
            }
        }
    }
    
    private suspend fun analyzeCurrentContext(): NotificationContext {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        
        val timeOfDay = when (hour) {
            in 6..11 -> TimeOfDay.MORNING
            in 12..17 -> TimeOfDay.AFTERNOON
            in 18..21 -> TimeOfDay.EVENING
            else -> TimeOfDay.NIGHT
        }
        
        val userActivity = detectUserActivity()
        val learningProgress = calculateLearningProgress()
        
        return NotificationContext(
            userActivity = userActivity,
            learningProgress = learningProgress,
            timeOfDay = timeOfDay,
            weather = detectWeather(),
            location = detectLocation()
        )
    }
    
    private fun detectUserActivity(): UserActivity {
        // Simulate activity detection
        // In a real app, this would use sensors and context
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 9..17 -> UserActivity.WORKING
            in 7..8, in 18..20 -> UserActivity.RELAXING
            in 12..13 -> UserActivity.RELAXING
            else -> UserActivity.IDLE
        }
    }
    
    private suspend fun calculateLearningProgress(): LearningProgress {
        val sessions = studySessionRepository.getRecentSessions()
        val targetSessionsPerWeek = 7
        val actualSessions = sessions.size
        
        return when {
            actualSessions < targetSessionsPerWeek * 0.7 -> LearningProgress.BEHIND_SCHEDULE
            actualSessions > targetSessionsPerWeek * 1.3 -> LearningProgress.AHEAD_SCHEDULE
            actualSessions >= targetSessionsPerWeek -> LearningProgress.ON_TRACK
            else -> LearningProgress.MAINTAINING
        }
    }
    
    private fun detectWeather(): Weather {
        // Simulate weather detection
        return Weather.UNKNOWN
    }
    
    private fun detectLocation(): Location {
        // Simulate location detection
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 9..17 -> Location.WORK
            in 22..23, in 0..6 -> Location.HOME
            else -> Location.HOME
        }
    }
    
    private suspend fun generateSmartNotifications(context: NotificationContext): List<SmartNotification> {
        val notifications = mutableListOf<SmartNotification>()
        val settings = _uiState.value.settings
        
        if (!shouldSendNotification(context, settings)) {
            return notifications
        }
        
        // Generate context-aware notifications
        if (settings.contextAware) {
            notifications.addAll(generateContextAwareNotifications(context))
        }
        
        // Generate regular study reminders
        if (settings.studyReminders) {
            notifications.addAll(generateStudyReminders(context))
        }
        
        // Generate practice reminders
        if (settings.practiceReminders) {
            notifications.addAll(generatePracticeReminders(context))
        }
        
        // Generate daily goal notifications
        if (settings.dailyGoal) {
            notifications.addAll(generateDailyGoalNotifications(context))
        }
        
        // Generate streak reminders
        if (settings.streakReminders) {
            notifications.addAll(generateStreakReminders(context))
        }
        
        // Limit notifications based on settings
        return notifications.take(settings.maxDailyNotifications)
    }
    
    private fun shouldSendNotification(context: NotificationContext, settings: NotificationSettings): Boolean {
        // Check quiet hours
        if (settings.quietHours) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (hour >= settings.quietStartHour || hour < settings.quietEndHour) {
                return false
            }
        }
        
        // Check daily limit
        if (_uiState.value.dailyNotificationCount >= settings.maxDailyNotifications) {
            return false
        }
        
        // Check user activity
        if (context.userActivity == UserActivity.STUDYING) {
            return false // Don't interrupt active studying
        }
        
        return true
    }
    
    private fun generateContextAwareNotifications(context: NotificationContext): List<SmartNotification> {
        val notifications = mutableListOf<SmartNotification>()
        
        when {
            context.userActivity == UserActivity.COMMUTING -> {
                notifications.add(createNotification(
                    type = NotificationType.CONTEXT_AWARE,
                    title = "Perfect time for German!",
                    message = "Make your commute productive with a quick German session",
                    priority = 8,
                    scheduledTime = System.currentTimeMillis() + 5 * 60 * 1000 // 5 minutes
                ))
            }
            context.timeOfDay == TimeOfDay.MORNING && context.userActivity == UserActivity.RELAXING -> {
                notifications.add(createNotification(
                    type = NotificationType.CONTEXT_AWARE,
                    title = "Good morning!",
                    message = "Start your day with some German vocabulary",
                    priority = 7,
                    scheduledTime = System.currentTimeMillis() + 10 * 60 * 1000 // 10 minutes
                ))
            }
            context.timeOfDay == TimeOfDay.EVENING && context.userActivity == UserActivity.RELAXING -> {
                notifications.add(createNotification(
                    type = NotificationType.CONTEXT_AWARE,
                    title = "Evening practice time!",
                    message = "Wind down with some German learning",
                    priority = 6,
                    scheduledTime = System.currentTimeMillis() + 15 * 60 * 1000 // 15 minutes
                ))
            }
        }
        
        return notifications
    }
    
    private fun generateStudyReminders(context: NotificationContext): List<SmartNotification> {
        val notifications = mutableListOf<SmartNotification>()
        val templates = notificationTemplates[NotificationType.STUDY_REMINDER] ?: return notifications
        
        val scheduledTimes = when (context.timeOfDay) {
            TimeOfDay.MORNING -> listOf(8, 9, 10)
            TimeOfDay.AFTERNOON -> listOf(14, 15, 16)
            TimeOfDay.EVENING -> listOf(19, 20, 21)
            TimeOfDay.NIGHT -> emptyList()
        }
        
        scheduledTimes.forEach { hour ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            
            notifications.add(createNotification(
                type = NotificationType.STUDY_REMINDER,
                title = "German Learning",
                message = templates.random(),
                priority = 7,
                scheduledTime = calendar.timeInMillis
            ))
        }
        
        return notifications
    }
    
    private fun generatePracticeReminders(context: NotificationContext): List<SmartNotification> {
        val notifications = mutableListOf<SmartNotification>()
        val templates = notificationTemplates[NotificationType.PRACTICE_REMINDER] ?: return notifications
        
        // Schedule practice reminders 2-3 times per week
        for (i in 0..2) {
            val scheduledTime = System.currentTimeMillis() + (i * 2 + 1) * 24 * 60 * 60 * 1000L
            notifications.add(createNotification(
                type = NotificationType.PRACTICE_REMINDER,
                title = "Speaking Practice",
                message = templates.random(),
                priority = 6,
                scheduledTime = scheduledTime
            ))
        }
        
        return notifications
    }
    
    private suspend fun generateDailyGoalNotifications(context: NotificationContext): List<SmartNotification> {
        val notifications = mutableListOf<SmartNotification>()
        val templates = notificationTemplates[NotificationType.DAILY_GOAL] ?: return notifications
        
        val todaySessions = studySessionRepository.getRecentSessions()
        val targetMinutes = 30
        val currentMinutes = todaySessions.sumOf { ((it.endTime ?: System.currentTimeMillis()) - it.startTime) / 60000L }
        
        if (currentMinutes < targetMinutes) {
            val remainingMinutes = targetMinutes - currentMinutes
            notifications.add(createNotification(
                type = NotificationType.DAILY_GOAL,
                title = "Daily Goal",
                message = templates.random().replace("{remaining}", remainingMinutes.toString()),
                priority = 8,
                scheduledTime = System.currentTimeMillis() + 30 * 60 * 1000 // 30 minutes
            ))
        }
        
        return notifications
    }
    
    private fun generateStreakReminders(context: NotificationContext): List<SmartNotification> {
        val notifications = mutableListOf<SmartNotification>()
        val templates = notificationTemplates[NotificationType.STREAK_REMINDER] ?: return notifications
        
        // Check if user hasn't studied today
        viewModelScope.launch {
            val todaySessions = studySessionRepository.getRecentSessions()
            if (todaySessions.isEmpty()) {
                notifications.add(createNotification(
                    type = NotificationType.STREAK_REMINDER,
                    title = "Don't break your streak!",
                    message = templates.random(),
                    priority = 9,
                    scheduledTime = System.currentTimeMillis() + 60 * 60 * 1000 // 1 hour
                ))
            }
        }
        
        return notifications
    }
    
    private fun createNotification(
        type: NotificationType,
        title: String,
        message: String,
        priority: Int,
        scheduledTime: Long,
        context: NotificationContext? = null
    ): SmartNotification {
        return SmartNotification(
            id = "${type.name}_${System.currentTimeMillis()}",
            title = title,
            message = message,
            type = type,
            priority = priority,
            scheduledTime = scheduledTime,
            context = context ?: NotificationContext(
                userActivity = UserActivity.IDLE,
                learningProgress = LearningProgress.ON_TRACK,
                timeOfDay = TimeOfDay.MORNING
            )
        )
    }
    
    fun cancelAllNotifications() {
        viewModelScope.launch {
            notificationRepository.cancelAllNotifications()
        }
    }
    
    fun testNotification(type: NotificationType) {
        viewModelScope.launch {
            val template = notificationTemplates[type]?.random() ?: "Test notification"
            val testNotification = createNotification(
                type = type,
                title = "Test: ${type.name}",
                message = template,
                priority = 10,
                scheduledTime = System.currentTimeMillis() + 5000 // 5 seconds
            )
            notificationRepository.scheduleAt(testNotification.title, testNotification.message, testNotification.scheduledTime, testNotification.type.name)
        }
    }
    
    private fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val notificationDate = Calendar.getInstance().apply { timeInMillis = timestamp }
        return today.get(Calendar.YEAR) == notificationDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == notificationDate.get(Calendar.DAY_OF_YEAR)
    }
}