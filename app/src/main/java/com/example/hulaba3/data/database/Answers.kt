package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


// New entity for multiple choice answers
@Entity(
    tableName = "answers",
    foreignKeys = [
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["questionId"])]
)
data class Answer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long, // Links to Question.id
    val answerText: String,
    val isCorrect: Boolean,
    val orderIndex: Int // For consistent ordering of options (A, B, C, D)
)