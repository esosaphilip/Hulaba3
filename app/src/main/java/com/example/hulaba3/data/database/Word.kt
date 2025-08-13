package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val meaning: String,
    val example: String,
    val lastReviewed: Long? = null, // Timestamp of last review
    val reviewCount: Int, // Number of reviews done so far
    val nextReviewTime: Long // Timestamp of the next review (used for Spaced Repetition)
)



