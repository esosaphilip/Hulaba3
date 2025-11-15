package com.example.hulaba3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

// Study groups for collaborative learning
@Entity(
    tableName = "study_groups",
    indices = [
        Index(value = ["createdBy"]),
        Index(value = ["category"])
    ]
)
data class StudyGroup(
    @PrimaryKey val id: String,
    val name: String,
    val description: String? = null,
    val category: String, // german_learning, tech_german, interview_prep, etc.
    val languageLevel: String? = null, // A1, A2, B1, B2, C1, C2
    val maxMembers: Int = 100,
    val isPrivate: Boolean = false,
    val isActive: Boolean = true,
    val coverImageUrl: String? = null,
    val createdBy: String, // userId
    val memberCount: Int = 0,
    val lastActivityAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Study group members and roles
@Entity(
    tableName = "study_group_members",
    indices = [
        Index(value = ["groupId", "userId"], unique = true),
        Index(value = ["userId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = StudyGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StudyGroupMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: String,
    val userId: String,
    val role: String = "member", // admin, moderator, member
    val isActive: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val contributionsCount: Int = 0, // posts, helpful actions, etc.
    val reputationPoints: Int = 0
)

// Group discussions and posts
@Entity(
    tableName = "group_discussions",
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["authorId"]),
        Index(value = ["createdAt"])
    ]
)
data class GroupDiscussion(
    @PrimaryKey val id: String,
    val groupId: String,
    val authorId: String,
    val title: String? = null,
    val content: String,
    val discussionType: String = "general", // general, question, announcement, resource
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val replyCount: Int = 0,
    val likeCount: Int = 0,
    val lastReplyAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Discussion replies
@Entity(
    tableName = "discussion_replies",
    indices = [
        Index(value = ["discussionId"]),
        Index(value = ["authorId"]),
        Index(value = ["createdAt"])
    ]
)
data class DiscussionReply(
    @PrimaryKey val id: String,
    val discussionId: String,
    val authorId: String,
    val content: String,
    val parentReplyId: String? = null, // for nested replies
    val likeCount: Int = 0,
    val isAcceptedAnswer: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Shared collections of vocabulary and topics
@Entity(
    tableName = "shared_collections",
    indices = [
        Index(value = ["createdBy"]),
        Index(value = ["collectionType"]),
        Index(value = ["isPublic"])
    ]
)
data class SharedCollection(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = null,
    val collectionType: String, // vocabulary, topics, mixed, german_tech, interview_prep
    val createdBy: String, // userId
    val isPublic: Boolean = true,
    val isOfficial: Boolean = false,
    val coverImageUrl: String? = null,
    val tags: String? = null, // JSON array
    val languageLevel: String? = null, // A1, A2, B1, B2, C1, C2
    val downloadCount: Int = 0,
    val ratingAverage: Float = 0f,
    val ratingCount: Int = 0,
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    val itemCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Collection items (vocabulary words and concepts)
@Entity(
    tableName = "collection_items",
    indices = [
        Index(value = ["collectionId", "vocabularyId"], unique = true),
        Index(value = ["collectionId", "conceptId"], unique = true),
        Index(value = ["orderIndex"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = SharedCollection::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CollectionItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: String,
    val vocabularyId: Long? = null,
    val conceptId: Long? = null,
    val topicId: String? = null,
    val orderIndex: Int = 0,
    val addedBy: String? = null, // userId if added by community member
    val addedAt: Long = System.currentTimeMillis(),
    val note: String? = null // community note about why this item is useful
)

// Collection downloads and usage tracking
@Entity(
    tableName = "collection_downloads",
    indices = [
        Index(value = ["collectionId", "userId"], unique = true),
        Index(value = ["userId"]),
        Index(value = ["downloadedAt"])
    ]
)
data class CollectionDownload(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: String,
    val userId: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = System.currentTimeMillis(),
    val usageCount: Int = 0,
    val isActive: Boolean = true,
    val userRating: Int? = null, // 1-5 stars
    val userReview: String? = null
)

// Challenges and competitions
@Entity(
    tableName = "challenges",
    indices = [
        Index(value = ["challengeType"]),
        Index(value = ["isActive"]),
        Index(value = ["startDate"]),
        Index(value = ["endDate"])
    ]
)
data class Challenge(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val challengeType: String, // speaking, streak, vocabulary, mixed
    val difficulty: String = "medium", // easy, medium, hard, expert
    val durationDays: Int,
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean = true,
    val isPublic: Boolean = true,
    val maxParticipants: Int? = null,
    val requirements: String? = null, // JSON object with requirements
    val rewards: String? = null, // JSON object with rewards/badges
    val coverImageUrl: String? = null,
    val createdBy: String? = null, // userId or system
    val participantCount: Int = 0,
    val completionRate: Float = 0f,
    val createdAt: Long = System.currentTimeMillis()
)

// User challenge participation
@Entity(
    tableName = "challenge_participants",
    indices = [
        Index(value = ["challengeId", "userId"], unique = true),
        Index(value = ["userId"]),
        Index(value = ["joinedAt"])
    ]
)
data class ChallengeParticipant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: String,
    val userId: String,
    val status: String = "joined", // joined, active, completed, dropped
    val progress: Float = 0f,
    val currentDay: Int = 0,
    val streakDays: Int = 0,
    val score: Int = 0,
    val rank: Int? = null,
    val joinedAt: Long = System.currentTimeMillis(),
    val lastActivityAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val droppedAt: Long? = null
)

// Native speaker connections and language exchange
@Entity(
    tableName = "native_speakers",
    indices = [
        Index(value = ["userId"], unique = true),
        Index(value = ["isAvailable"]),
        Index(value = ["language"]),
        Index(value = ["dialect"])
    ]
)
data class NativeSpeaker(
    @PrimaryKey val id: String,
    val userId: String,
    val language: String = "German",
    val dialect: String? = null, // Standard German, Swiss German, etc.
    val proficiencyLevel: String = "native",
    val isAvailable: Boolean = true,
    val hourlyRate: Int? = null, // in cents, null for free exchange
    val bio: String? = null,
    val specialties: String? = null, // JSON array of specialties
    val teachingExperience: String? = null,
    val certifications: String? = null, // JSON array
    val ratingAverage: Float = 0f,
    val ratingCount: Int = 0,
    val sessionCount: Int = 0,
    val responseTimeHours: Float? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Language exchange sessions
@Entity(
    tableName = "language_exchange_sessions",
    indices = [
        Index(value = ["learnerId"]),
        Index(value = ["nativeSpeakerId"]),
        Index(value = ["scheduledFor"]),
        Index(value = ["status"])
    ]
)
data class LanguageExchangeSession(
    @PrimaryKey val id: String,
    val learnerId: String,
    val nativeSpeakerId: String,
    val sessionType: String = "conversation", // conversation, correction, teaching
    val scheduledFor: Long? = null,
    val durationMinutes: Int = 30,
    val status: String = "scheduled", // scheduled, in_progress, completed, cancelled
    val topic: String? = null,
    val learnerLanguage: String = "English",
    val nativeLanguage: String = "German",
    val notes: String? = null,
    val feedbackGiven: Boolean = false,
    val ratingGiven: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val startedAt: Long? = null,
    val completedAt: Long? = null
)

// User social connections and friendships
@Entity(
    tableName = "user_connections",
    indices = [
        Index(value = ["userId", "connectedUserId"], unique = true),
        Index(value = ["connectedUserId"]),
        Index(value = ["connectionType"]),
        Index(value = ["createdAt"])
    ]
)
data class UserConnection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val connectedUserId: String,
    val connectionType: String = "friend", // friend, study_buddy, mentor, student
    val status: String = "accepted", // pending, accepted, blocked
    val isFavorite: Boolean = false,
    val notes: String? = null,
    val sharedCollectionsCount: Int = 0,
    val jointStudySessionsCount: Int = 0,
    val lastInteractionAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// User reputation and contribution tracking
@Entity(
    tableName = "user_reputation",
    indices = [
        Index(value = ["userId"], unique = true)
    ]
)
data class UserReputation(
    @PrimaryKey val id: String,
    val userId: String,
    val totalPoints: Int = 0,
    val helpfulAnswersCount: Int = 0,
    val sharedCollectionsCount: Int = 0,
    val collectionDownloadsCount: Int = 0,
    val studyGroupContributionsCount: Int = 0,
    val nativeSpeakerSessionsCount: Int = 0,
    val challengeWinsCount: Int = 0,
    val ratingAverage: Float = 0f,
    val ratingCount: Int = 0,
    val level: String = "beginner", // beginner, contributor, expert, mentor
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)