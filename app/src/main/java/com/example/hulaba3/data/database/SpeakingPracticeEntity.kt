
package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "speaking_practice")
data class SpeakingPracticeEntity(
    @PrimaryKey val id: String,
    val wordId: String,
    val recordingFilePath: String,
    val targetText: String,
    val userText: String,
    val accuracyScore: Float,
    val fluencyScore: Float,
    val pronunciationScore: Float,
    val feedback: String,
    val createdAt: Long
)
