package com.example.hulaba3.data.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "topics")
data class Topic(
    @PrimaryKey val id: String,
    val title: String,
    val pdfUri: String? = null, // Storing URI as a String
    val lastReviewed: Long? = null, // New column for review tracking
    val nextReviewTime: Long = 0 // New column for next review time
)
