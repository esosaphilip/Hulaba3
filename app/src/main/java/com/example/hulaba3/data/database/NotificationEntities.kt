
package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType {
    REVIEW_REMINDER,
    STREAK_MAINTENANCE,
    ACHIEVEMENT,
    STUDY_SUGGESTION,
    SOCIAL,
    CHALLENGE,
    LOCATION_BASED
}

enum class NotificationPriority {
    HIGH,
    MEDIUM,
    LOW
}

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val priority: NotificationPriority = NotificationPriority.MEDIUM,
    val data: String? = null,
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
    val isActive: Boolean = true,
    val isScheduled: Boolean = false,
    val scheduledAt: Long? = null,
    val readAt: Long? = null,
    val deliveredAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
