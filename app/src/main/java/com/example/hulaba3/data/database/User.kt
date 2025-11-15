package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "users")
@TypeConverters(DateConverter::class)
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val username: String,
    val avatarUrl: String? = null,
    val germanLevel: String = "A1", // A1, A2, B1, B2, C1, C2
    val targetGermanLevel: String = "C1",
    val dailyMixRatioWords: Int = 60,
    val dailyMixRatioTopics: Int = 40,
    val preferredSessionLength: Int = 10, // minutes
    val streakCount: Int = 0,
    val totalStudyTimeMinutes: Int = 0,
    val wordsLearnedCount: Int = 0,
    val topicsCompletedCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastStudyDate: Long? = null,
    val notificationEnabled: Boolean = true,
    val offlineModeEnabled: Boolean = true
)

// User settings and preferences
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: String,
    val userId: String,
    val theme: String = "auto", // auto, light, dark, oled
    val fontSizeScale: Float = 1.0f,
    val animationEnabled: Boolean = true,
    val highContrastMode: Boolean = false,
    val colorBlindMode: String? = null,
    val speechSpeed: Float = 1.0f,
    val autoPlayPronunciation: Boolean = true,
    val speechRecognitionEnabled: Boolean = true,
    val oneHandedMode: Boolean = false,
    val transitModeEnabled: Boolean = true,
    val quietHoursStart: Int? = null, // minutes from midnight
    val quietHoursEnd: Int? = null,
    val morningStartTime: Int = 480, // 8:00 AM in minutes
    val morningEndTime: Int = 600, // 10:00 AM in minutes
    val lunchStartTime: Int = 750, // 12:30 PM in minutes
    val lunchEndTime: Int = 780, // 1:00 PM in minutes
    val eveningStartTime: Int = 1140, // 7:00 PM in minutes
    val eveningEndTime: Int = 1260, // 9:00 PM in minutes
    val locationBasedTriggers: Boolean = true,
    val adaptToSchedule: Boolean = true,
    val weekendReminders: Boolean = false,
    val anonymousAnalytics: Boolean = true,
    val shareLearningData: Boolean = false,
    val participateInResearch: Boolean = false
)

// User achievements and badges
@Entity(tableName = "user_achievements")
data class UserAchievement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val achievementType: String, // streak, words_learned, topics_completed, etc.
    val achievementLevel: Int = 1,
    val title: String,
    val description: String,
    val icon: String? = null,
    val pointsAwarded: Int = 0,
    val unlockedAt: Long = System.currentTimeMillis(),
    val progressValue: Float? = null,
    val targetValue: Float? = null
)

// User learning goals and targets
@Entity(tableName = "user_goals")
data class UserGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val goalType: String, // german_level, topic_completion, speaking_practice, etc.
    val targetValue: String,
    val currentValue: String,
    val deadline: Long? = null,
    val priority: Int = 1, // 1-5 scale
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// User notification preferences and history
@Entity(tableName = "user_notifications")
data class UserNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val notificationType: String, // review_reminder, streak_maintenance, achievement, etc.
    val title: String,
    val message: String,
    val data: String? = null, // JSON payload
    val isRead: Boolean = false,
    val isActioned: Boolean = false,
    val scheduledFor: Long? = null,
    val sentAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// User study statistics and analytics
@Entity(tableName = "user_study_stats")
data class UserStudyStats(
    @PrimaryKey val id: String,
    val userId: String,
    val date: Long, // Date in milliseconds (start of day)
    val totalStudyTimeMinutes: Int = 0,
    val wordsReviewedCount: Int = 0,
    val conceptsReviewedCount: Int = 0,
    val sessionsCount: Int = 0,
    val averageSessionDurationMinutes: Float = 0f,
    val bestTimeOfDay: Int? = null, // minutes from midnight
    val retentionRate: Float? = null,
    val accuracyRate: Float? = null,
    val streakDay: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// User learning patterns and insights
@Entity(tableName = "user_learning_patterns")
data class UserLearningPattern(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val patternType: String, // optimal_time, preferred_session_length, difficulty_preference, etc.
    val patternValue: String,
    val confidence: Float = 0f, // 0-1 scale
    val evidenceCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)