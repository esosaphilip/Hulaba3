package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "words",
    indices = [
        Index(value = ["germanWord"]),
        Index(value = ["contextId"]),
        Index(value = ["difficultyLevel"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = VocabularyContext::class,
            parentColumns = ["id"],
            childColumns = ["contextId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val germanWord: String,
    val englishTranslation: String,
    val pronunciation: String? = null,
    val exampleSentenceGerman: String? = null,
    val exampleSentenceEnglish: String? = null,
    val partOfSpeech: String? = null, // noun, verb, adjective, etc.
    val gender: String? = null, // der, die, das
    val pluralForm: String? = null,
    val audioUrl: String? = null,
    val difficultyLevel: String = "A1", // A1, A2, B1, B2, C1, C2
    val contextId: Long? = null,
    val frequencyRank: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// User's vocabulary progress with spaced repetition
@Entity(tableName = "UserVocabularyProgress")
data class UserVocabularyProgress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val wordId: Long,
    val status: String = "new", // new, learning, reviewing, mastered
    val confidenceLevel: Int = 0, // 0-5 scale
    val lastReviewedAt: Long? = null,
    val nextReviewAt: Long = System.currentTimeMillis(),
    val reviewCount: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Vocabulary context/categories
@Entity(tableName = "vocabulary_contexts")
data class VocabularyContext(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // Restaurant, Tech, Travel, etc.
    val icon: String? = null, // Icon resource name
    val colorHex: String? = null, // Brand color for this context
    val description: String? = null,
    val orderIndex: Int = 0
)

// Vocabulary review history for spaced repetition algorithm
@Entity(tableName = "VocabularyReview")
data class VocabularyReview(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userVocabularyProgressId: Long,
    val sessionId: Long? = null,
    val difficultyRating: Int, // 1-5 scale
    val userAnswer: String? = null,
    val correctAnswer: String? = null,
    val wasCorrect: Boolean,
    val timeSpentSeconds: Int = 0,
    val reviewedAt: Long = System.currentTimeMillis()
)