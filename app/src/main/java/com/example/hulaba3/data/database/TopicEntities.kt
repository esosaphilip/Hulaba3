package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Embedded

// Topic categories for organization
@Entity(
    tableName = "topic_categories",
    indices = [
        Index(value = ["name"]),
        Index(value = ["parentCategoryId"])
    ]
)
data class TopicCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // Technology, Business, Language, Professional Skills, etc.
    val icon: String? = null,
    val colorHex: String? = null,
    val description: String? = null,
    val orderIndex: Int = 0,
    val parentCategoryId: Long? = null, // For hierarchical categories
    val isActive: Boolean = true
)

// Concepts within topics - the core learning units
@Entity(
    tableName = "concepts",
    indices = [
        Index(value = ["topicId"]),
        Index(value = ["title"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Concept(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: String,
    val title: String,
    val description: String? = null,
    val definition: String? = null,
    val useCase: String? = null,
    val visualDiagramUrl: String? = null,
    val codeExample: String? = null,
    val germanTranslation: String? = null,
    val germanDefinition: String? = null,
    val germanUseCase: String? = null,
    val sourcePageNumber: Int? = null,
    val orderIndex: Int = 0,
    val difficulty: String = "medium", // easy, medium, hard
    val estimatedStudyTimeMinutes: Int? = null,
    val relatedConceptIds: String? = null, // JSON array
    val tags: String? = null, // JSON array
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Study materials (PDFs, images, videos, etc.)
@Entity(
    tableName = "study_materials",
    indices = [
        Index(value = ["topicId"]),
        Index(value = ["fileType"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StudyMaterial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: String,
    val title: String,
    val fileUrl: String,
    val fileType: String = "pdf", // pdf, image, video, audio, link
    val fileSizeBytes: Long? = null,
    val pageCount: Int? = null,
    val durationSeconds: Int? = null, // for audio/video
    val thumbnailUrl: String? = null,
    val description: String? = null,
    val uploadedBy: String? = null,
    val isPrimary: Boolean = false, // Main material for the topic
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// User's topic progress with detailed tracking
@Entity(
    tableName = "user_topic_progress",
    indices = [
        Index(value = ["userId", "topicId"], unique = true),
        Index(value = ["userId"]),
        Index(value = ["topicId"]),
        Index(value = ["status"]),
        Index(value = ["nextReviewAt"])
    ]
)
data class UserTopicProgress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val topicId: String,
    val status: String = "not_started", // not_started, in_progress, completed, mastered
    val progressPercentage: Float = 0f,
    val masteredConceptsCount: Int = 0,
    val totalConceptsCount: Int = 0,
    val lastStudiedAt: Long? = null,
    val nextReviewAt: Long? = null,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val totalStudyTimeMinutes: Int = 0,
    val averageSessionDurationMinutes: Int? = null,
    val confidenceLevel: Int = 0, // 0-5 scale
    val notes: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// User's concept progress within topics
@Entity(
    tableName = "user_concept_progress",
    indices = [
        Index(value = ["userId", "conceptId"], unique = true),
        Index(value = ["userId"]),
        Index(value = ["conceptId"]),
        Index(value = ["topicId"]),
        Index(value = ["status"]),
        Index(value = ["nextReviewAt"])
    ]
)
data class UserConceptProgress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val conceptId: Long,
    val topicId: String,
    val status: String = "new", // new, learning, reviewing, mastered
    val confidenceLevel: Int = 0, // 0-5 scale
    val lastReviewedAt: Long? = null,
    val nextReviewAt: Long? = null,
    val reviewCount: Int = 0,
    val correctReviewsCount: Int = 0,
    val timeSpentMinutes: Int = 0,
    val notes: String? = null,
    val isFlagged: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Concept review history for spaced repetition
@Entity(
    tableName = "concept_reviews",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["conceptId"]),
        Index(value = ["topicId"]),
        Index(value = ["sessionId"]),
        Index(value = ["reviewedAt"])
    ]
)
data class ConceptReview(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val conceptId: Long,
    val topicId: String,
    val sessionId: Long? = null,
    val difficultyRating: Int, // 1-5 scale
    val confidenceBefore: Int = 0, // 0-5 scale
    val confidenceAfter: Int = 0, // 0-5 scale
    val timeSpentSeconds: Int = 0,
    val userNotes: String? = null,
    val reviewedAt: Long = System.currentTimeMillis()
)

// User notes for concepts and topics
@Entity(
    tableName = "user_notes",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["topicId"]),
        Index(value = ["conceptId"]),
        Index(value = ["createdAt"]),
        Index(value = ["isPublic"])
    ]
)
data class UserNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val topicId: String? = null,
    val conceptId: Long? = null,
    val title: String? = null,
    val content: String,
    val isPublic: Boolean = false,
    val tags: String? = null, // JSON array
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Material page tracking for PDF study
@Entity(
    tableName = "material_page_progress",
    indices = [
        Index(value = ["userId", "materialId", "pageNumber"], unique = true),
        Index(value = ["userId"]),
        Index(value = ["materialId"]),
        Index(value = ["lastReadAt"])
    ]
)
data class MaterialPageProgress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val materialId: Long,
    val pageNumber: Int,
    val isRead: Boolean = false,
    val readingProgress: Float = 0f, // 0-1 for partial reading
    val timeSpentSeconds: Int = 0,
    val notes: String? = null,
    val highlights: String? = null, // JSON array of highlight objects
    val lastReadAt: Long = System.currentTimeMillis()
)

// Data classes for complex queries
data class TopicWithProgress(
    @Embedded(prefix = "topic_") val topic: Topic,
    @Embedded(prefix = "progress_") val progress: UserTopicProgress
)

data class ConceptWithProgress(
    @Embedded(prefix = "concept_") val concept: Concept,
    @Embedded(prefix = "progress_") val progress: UserConceptProgress
)

data class TopicStats(
    val totalTopics: Int,
    val notStarted: Int,
    val inProgress: Int,
    val completed: Int,
    val mastered: Int,
    val avgProgress: Float,
    val totalStudyTime: Int
)

data class ConceptStats(
    val totalConcepts: Int,
    val newConcepts: Int,
    val learningConcepts: Int,
    val reviewingConcepts: Int,
    val masteredConcepts: Int,
    val avgConfidence: Float,
    val totalStudyTime: Int
)

data class ConceptReviewStats(
    val avgDifficulty: Float,
    val avgConfidenceGain: Float,
    val avgTimeSpent: Float,
    val totalReviews: Int
)

data class MaterialProgressStats(
    val readPages: Int,
    val totalPages: Int,
    val readPercentage: Float,
    val totalTimeSpent: Int
)