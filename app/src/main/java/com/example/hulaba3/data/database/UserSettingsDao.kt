package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettings)
    
    @Query("SELECT * FROM user_settings WHERE userId = :userId")
    suspend fun getSettingsByUserId(userId: String): UserSettings?
    
    @Query("SELECT * FROM user_settings WHERE userId = :userId")
    fun getSettingsByUserIdFlow(userId: String): Flow<UserSettings?>
    
    @Update
    suspend fun updateSettings(settings: UserSettings)
    
    @Query("UPDATE user_settings SET theme = :theme WHERE userId = :userId")
    suspend fun updateTheme(userId: String, theme: String)
    
    @Query("UPDATE user_settings SET fontSizeScale = :fontSizeScale WHERE userId = :userId")
    suspend fun updateFontSizeScale(userId: String, fontSizeScale: Float)
    
    @Query("UPDATE user_settings SET animationEnabled = :enabled WHERE userId = :userId")
    suspend fun updateAnimationEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET highContrastMode = :enabled WHERE userId = :userId")
    suspend fun updateHighContrastMode(userId: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET speechSpeed = :speed WHERE userId = :userId")
    suspend fun updateSpeechSpeed(userId: String, speed: Float)
    
    @Query("UPDATE user_settings SET autoPlayPronunciation = :enabled WHERE userId = :userId")
    suspend fun updateAutoPlayPronunciation(userId: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET speechRecognitionEnabled = :enabled WHERE userId = :userId")
    suspend fun updateSpeechRecognitionEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET oneHandedMode = :enabled WHERE userId = :userId")
    suspend fun updateOneHandedMode(userId: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET transitModeEnabled = :enabled WHERE userId = :userId")
    suspend fun updateTransitModeEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET quietHoursStart = :startTime, quietHoursEnd = :endTime WHERE userId = :userId")
    suspend fun updateQuietHours(userId: String, startTime: Int?, endTime: Int?)
    
    @Query("UPDATE user_settings SET morningStartTime = :startTime, morningEndTime = :endTime WHERE userId = :userId")
    suspend fun updateMorningHours(userId: String, startTime: Int, endTime: Int)
    
    @Query("UPDATE user_settings SET lunchStartTime = :startTime, lunchEndTime = :endTime WHERE userId = :userId")
    suspend fun updateLunchHours(userId: String, startTime: Int, endTime: Int)
    
    @Query("UPDATE user_settings SET eveningStartTime = :startTime, eveningEndTime = :endTime WHERE userId = :userId")
    suspend fun updateEveningHours(userId: String, startTime: Int, endTime: Int)
    
    @Delete
    suspend fun deleteSettings(settings: UserSettings)
    
    @Query("DELETE FROM user_settings WHERE userId = :userId")
    suspend fun deleteSettingsByUserId(userId: String)
}