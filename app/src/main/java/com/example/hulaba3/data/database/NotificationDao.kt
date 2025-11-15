package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    // Notification operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<Notification>)

    @Update
    suspend fun updateNotification(notification: Notification)

    @Delete
    suspend fun deleteNotification(notification: Notification)

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotificationById(notificationId: Long)

    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    suspend fun getNotificationById(notificationId: Long): Notification?

    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    fun getNotificationByIdFlow(notificationId: Long): Flow<Notification?>

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getNotificationsByUser(userId: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsByUserFlow(userId: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    suspend fun getUnreadNotificationsByUser(userId: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotificationsByUserFlow(userId: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 1 ORDER BY createdAt DESC")
    suspend fun getReadNotificationsByUser(userId: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 1 ORDER BY createdAt DESC")
    fun getReadNotificationsByUserFlow(userId: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND type = :type ORDER BY createdAt DESC")
    suspend fun getNotificationsByType(userId: String, type: NotificationType): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND type = :type ORDER BY createdAt DESC")
    fun getNotificationsByTypeFlow(userId: String, type: NotificationType): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND priority = :priority ORDER BY createdAt DESC")
    suspend fun getNotificationsByPriority(userId: String, priority: NotificationPriority): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND priority = :priority ORDER BY createdAt DESC")
    fun getNotificationsByPriorityFlow(userId: String, priority: NotificationPriority): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    suspend fun getNotificationsByDateRange(userId: String, startDate: Long, endDate: Long): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    fun getNotificationsByDateRangeFlow(userId: String, startDate: Long, endDate: Long): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND scheduledAt >= :startDate AND scheduledAt <= :endDate AND isScheduled = 1 ORDER BY scheduledAt ASC")
    suspend fun getScheduledNotificationsByDateRange(userId: String, startDate: Long, endDate: Long): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND scheduledAt >= :startDate AND scheduledAt <= :endDate AND isScheduled = 1 ORDER BY scheduledAt ASC")
    fun getScheduledNotificationsByDateRangeFlow(userId: String, startDate: Long, endDate: Long): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isScheduled = 1 AND scheduledAt <= :currentTime AND isDelivered = 0 ORDER BY scheduledAt ASC")
    suspend fun getDueScheduledNotifications(userId: String, currentTime: Long): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isScheduled = 1 AND scheduledAt <= :currentTime AND isDelivered = 0 ORDER BY scheduledAt ASC")
    fun getDueScheduledNotificationsFlow(userId: String, currentTime: Long): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isDelivered = 1 ORDER BY deliveredAt DESC")
    suspend fun getDeliveredNotificationsByUser(userId: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isDelivered = 1 ORDER BY deliveredAt DESC")
    fun getDeliveredNotificationsByUserFlow(userId: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentNotifications(userId: String, limit: Int = 50): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentNotificationsFlow(userId: String, limit: Int = 50): Flow<List<Notification>>

    @Query("UPDATE notifications SET isRead = 1, readAt = :readAt WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Long, readAt: Long)

    @Query("UPDATE notifications SET isRead = 1, readAt = :readAt WHERE userId = :userId")
    suspend fun markAllNotificationsAsReadForUser(userId: String, readAt: Long)

    @Query("UPDATE notifications SET isDelivered = 1, deliveredAt = :deliveredAt WHERE id = :notificationId")
    suspend fun markNotificationAsDelivered(notificationId: Long, deliveredAt: Long)

    @Query("UPDATE notifications SET isDelivered = 1, deliveredAt = :deliveredAt WHERE userId = :userId AND isScheduled = 1 AND scheduledAt <= :currentTime")
    suspend fun markDueNotificationsAsDelivered(userId: String, currentTime: Long, deliveredAt: Long)

    @Query("UPDATE notifications SET isRead = 0, readAt = NULL WHERE id = :notificationId")
    suspend fun markNotificationAsUnread(notificationId: Long)

    @Query("UPDATE notifications SET isScheduled = 0, scheduledAt = NULL WHERE id = :notificationId")
    suspend fun unscheduleNotification(notificationId: Long)

    @Query("UPDATE notifications SET isScheduled = 1, scheduledAt = :scheduledAt WHERE id = :notificationId")
    suspend fun scheduleNotification(notificationId: Long, scheduledAt: Long)

    @Query("UPDATE notifications SET isActive = 0 WHERE id = :notificationId")
    suspend fun deactivateNotification(notificationId: Long)

    @Query("UPDATE notifications SET isActive = 1 WHERE id = :notificationId")
    suspend fun activateNotification(notificationId: Long)

    // Notification statistics operations
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId")
    suspend fun getTotalNotificationsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    suspend fun getUnreadNotificationsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 1")
    suspend fun getReadNotificationsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isDelivered = 1")
    suspend fun getDeliveredNotificationsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isScheduled = 1")
    suspend fun getScheduledNotificationsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND type = :type")
    suspend fun getNotificationsCountByType(userId: String, type: NotificationType): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND priority = :priority")
    suspend fun getNotificationsCountByPriority(userId: String, priority: NotificationPriority): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND createdAt >= :startDate AND createdAt <= :endDate")
    suspend fun getNotificationsCountByDateRange(userId: String, startDate: Long, endDate: Long): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0 AND type = :type")
    suspend fun getUnreadNotificationsCountByType(userId: String, type: NotificationType): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0 AND priority = :priority")
    suspend fun getUnreadNotificationsCountByPriority(userId: String, priority: NotificationPriority): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isActive = 1")
    suspend fun getActiveNotificationsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isActive = 0")
    suspend fun getInactiveNotificationsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isScheduled = 1 AND scheduledAt <= :currentTime AND isDelivered = 0")
    suspend fun getDueScheduledNotificationsCount(userId: String, currentTime: Long): Int

    // Complex queries for notifications
    @Query("""
        SELECT n.*, 
               CASE 
                   WHEN n.isRead = 0 AND n.priority = 'HIGH' THEN 1
                   WHEN n.isRead = 0 AND n.priority = 'MEDIUM' THEN 2
                   WHEN n.isRead = 0 AND n.priority = 'LOW' THEN 3
                   WHEN n.isRead = 1 THEN 4
                   ELSE 5
               END as sortOrder
        FROM notifications n
        WHERE n.userId = :userId AND n.isActive = 1
        ORDER BY sortOrder ASC, n.createdAt DESC
        LIMIT :limit
    """)
    suspend fun getActiveNotificationsWithPrioritySorting(userId: String, limit: Int = 50): List<Notification>

    @Query("""
        SELECT n.*, 
               CASE 
                   WHEN n.isRead = 0 AND n.priority = 'HIGH' THEN 1
                   WHEN n.isRead = 0 AND n.priority = 'MEDIUM' THEN 2
                   WHEN n.isRead = 0 AND n.priority = 'LOW' THEN 3
                   WHEN n.isRead = 1 THEN 4
                   ELSE 5
               END as sortOrder
        FROM notifications n
        WHERE n.userId = :userId AND n.isActive = 1
        ORDER BY sortOrder ASC, n.createdAt DESC
        LIMIT :limit
    """)
    fun getActiveNotificationsWithPrioritySortingFlow(userId: String, limit: Int = 50): Flow<List<Notification>>

    @Query("""
        SELECT n.*, 
               CASE 
                   WHEN n.isRead = 0 AND n.priority = 'HIGH' THEN 1
                   WHEN n.isRead = 0 AND n.priority = 'MEDIUM' THEN 2
                   WHEN n.isRead = 0 AND n.priority = 'LOW' THEN 3
                   WHEN n.isRead = 1 THEN 4
                   ELSE 5
               END as sortOrder
        FROM notifications n
        WHERE n.userId = :userId AND n.isActive = 1 AND n.type = :type
        ORDER BY sortOrder ASC, n.createdAt DESC
        LIMIT :limit
    """)
    suspend fun getActiveNotificationsByTypeWithPrioritySorting(userId: String, type: NotificationType, limit: Int = 50): List<Notification>

    @Query("""
        SELECT n.*, 
               CASE 
                   WHEN n.isRead = 0 AND n.priority = 'HIGH' THEN 1
                   WHEN n.isRead = 0 AND n.priority = 'MEDIUM' THEN 2
                   WHEN n.isRead = 0 AND n.priority = 'LOW' THEN 3
                   WHEN n.isRead = 1 THEN 4
                   ELSE 5
               END as sortOrder
        FROM notifications n
        WHERE n.userId = :userId AND n.isActive = 1 AND n.type = :type
        ORDER BY sortOrder ASC, n.createdAt DESC
        LIMIT :limit
    """)
    fun getActiveNotificationsByTypeWithPrioritySortingFlow(userId: String, type: NotificationType, limit: Int = 50): Flow<List<Notification>>

    @Query("""
        SELECT n.*, 
               CASE 
                   WHEN n.isRead = 0 AND n.priority = 'HIGH' THEN 1
                   WHEN n.isRead = 0 AND n.priority = 'MEDIUM' THEN 2
                   WHEN n.isRead = 0 AND n.priority = 'LOW' THEN 3
                   WHEN n.isRead = 1 THEN 4
                   ELSE 5
               END as sortOrder
        FROM notifications n
        WHERE n.userId = :userId AND n.isActive = 1 AND n.priority = :priority
        ORDER BY sortOrder ASC, n.createdAt DESC
        LIMIT :limit
    """)
    suspend fun getActiveNotificationsByPriorityWithPrioritySorting(userId: String, priority: NotificationPriority, limit: Int = 50): List<Notification>

    @Query("""
        SELECT n.*, 
               CASE 
                   WHEN n.isRead = 0 AND n.priority = 'HIGH' THEN 1
                   WHEN n.isRead = 0 AND n.priority = 'MEDIUM' THEN 2
                   WHEN n.isRead = 0 AND n.priority = 'LOW' THEN 3
                   WHEN n.isRead = 1 THEN 4
                   ELSE 5
               END as sortOrder
        FROM notifications n
        WHERE n.userId = :userId AND n.isActive = 1 AND n.priority = :priority
        ORDER BY sortOrder ASC, n.createdAt DESC
        LIMIT :limit
    """)
    fun getActiveNotificationsByPriorityWithPrioritySortingFlow(userId: String, priority: NotificationPriority, limit: Int = 50): Flow<List<Notification>>

    // Delete operations
    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteAllNotificationsForUser(userId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId AND isRead = 1")
    suspend fun deleteReadNotificationsForUser(userId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId AND createdAt < :olderThanTimestamp")
    suspend fun deleteNotificationsOlderThan(userId: String, olderThanTimestamp: Long)

    @Query("DELETE FROM notifications WHERE userId = :userId AND type = :type")
    suspend fun deleteNotificationsByType(userId: String, type: NotificationType)

    @Query("DELETE FROM notifications WHERE userId = :userId AND priority = :priority")
    suspend fun deleteNotificationsByPriority(userId: String, priority: NotificationPriority)

    @Query("DELETE FROM notifications WHERE userId = :userId AND isScheduled = 1 AND scheduledAt < :currentTime")
    suspend fun deleteExpiredScheduledNotifications(userId: String, currentTime: Long)

    @Query("DELETE FROM notifications WHERE userId = :userId AND isActive = 0")
    suspend fun deleteInactiveNotificationsForUser(userId: String)

    // Search operations
    @Query("SELECT * FROM notifications WHERE userId = :userId AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    suspend fun searchNotifications(userId: String, query: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchNotificationsFlow(userId: String, query: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    suspend fun searchUnreadNotifications(userId: String, query: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchUnreadNotificationsFlow(userId: String, query: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 1 AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    suspend fun searchReadNotifications(userId: String, query: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 1 AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchReadNotificationsFlow(userId: String, query: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND type = :type AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    suspend fun searchNotificationsByType(userId: String, type: NotificationType, query: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND type = :type AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchNotificationsByTypeFlow(userId: String, type: NotificationType, query: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND priority = :priority AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    suspend fun searchNotificationsByPriority(userId: String, priority: NotificationPriority, query: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND priority = :priority AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchNotificationsByPriorityFlow(userId: String, priority: NotificationPriority, query: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND createdAt >= :startDate AND createdAt <= :endDate AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    suspend fun searchNotificationsByDateRange(userId: String, startDate: Long, endDate: Long, query: String): List<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND createdAt >= :startDate AND createdAt <= :endDate AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchNotificationsByDateRangeFlow(userId: String, startDate: Long, endDate: Long, query: String): Flow<List<Notification>>
}