package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "topics",
    indices = [
        Index(value = ["title"]),
        Index(value = ["categoryId"]),
        Index(value = ["createdBy"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TopicCategory::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["createdBy"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Topic(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = null,
    val categoryId: Long? = null,
    val difficultyLevel: String = "intermediate", // beginner, intermediate, advanced
    val estimatedStudyTimeMinutes: Int? = null,
    val totalConceptsCount: Int = 0,
    val coverImageUrl: String? = null,
    val createdBy: String? = null,
    val isPublic: Boolean = false,
    val isOfficial: Boolean = false,
    val language: String = "english", // english, german, bilingual
    val tags: String? = null, // JSON array of tags
    val prerequisites: String? = null, // JSON array of prerequisite topic IDs
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
