package com.example.hulaba3.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        // Core Entities
        Word::class,
        Topic::class,
        User::class,
        UserSettings::class,
        
        // Vocabulary System
        VocabularyContext::class,
        UserVocabularyProgress::class,
        VocabularyReview::class,
        
        // Topic System
        TopicCategory::class,
        Concept::class,
        StudyMaterial::class,
        UserTopicProgress::class,
        UserConceptProgress::class,
        ConceptReview::class,
        UserNote::class,
        MaterialPageProgress::class,
        
        // Learning System
        Question::class,
        Answer::class,
        StudySession::class,
        QuestionAttempt::class,
        UserAchievement::class,
        UserGoal::class,
        UserStudyStats::class,
        UserLearningPattern::class,
        // Notifications
        Notification::class,
        UserNotification::class,
        
        // Community System
        StudyGroup::class,
        StudyGroupMember::class,
        GroupDiscussion::class,
        DiscussionReply::class,
        SharedCollection::class,
        CollectionItem::class,
        CollectionDownload::class,
        Challenge::class,
        ChallengeParticipant::class,
        NativeSpeaker::class,
        LanguageExchangeSession::class,
        UserConnection::class,
        UserReputation::class,
        
        // Quiz System
        Quiz::class,
        QuizQuestion::class,
        QuizAnswer::class,
        QuizSession::class,
        QuizSessionAnswer::class,
        
    
        // Speaking Practice System
        SpeakingPracticeEntity::class,
        SpeakingPracticeSession::class,
        SpeakingPracticeExercise::class,
        SpeakingPracticeAttempt::class,
],
    version = 10,
    exportSchema = false
)
@TypeConverters(
    // Enums to String for quiz/question/speaking practice
    QuizTypeConverter::class,
    QuizDifficultyConverter::class,
    QuestionTypeConverter::class,
    QuestionDifficultyConverter::class,
    SpeakingPracticeTypeConverter::class,
    SpeakingPracticeDifficultyConverter::class,
    SpeakingPracticeExerciseTypeConverter::class,
    SpeakingPracticeExerciseDifficultyConverter::class,
    // Core types
    DateConverter::class,
    UriTypeConverter::class,
    JsonConverter::class,
    TimeConverter::class,
    BooleanConverter::class,
    ColorConverter::class,
    // Notifications
    NotificationTypeConverter::class,
    NotificationPriorityConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    // Core DAOs
    abstract fun wordDao(): WordDao
    abstract fun topicDao(): TopicDao
    abstract fun userDao(): UserDao
    abstract fun userSettingsDao(): UserSettingsDao
    
    // Vocabulary DAOs
    abstract fun vocabularyContextDao(): VocabularyContextDao
    abstract fun userVocabularyProgressDao(): UserVocabularyProgressDao
    abstract fun vocabularyReviewDao(): VocabularyReviewDao
    
    // Topic DAOs
    abstract fun topicCategoryDao(): TopicCategoryDao
    abstract fun conceptDao(): ConceptDao
    abstract fun studyMaterialDao(): StudyMaterialDao
    abstract fun userTopicProgressDao(): UserTopicProgressDao
    abstract fun userConceptProgressDao(): UserConceptProgressDao
    abstract fun conceptReviewDao(): ConceptReviewDao
    abstract fun userNoteDao(): UserNoteDao
    abstract fun materialPageProgressDao(): MaterialPageProgressDao
    
    // Learning System DAOs
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun questionAttemptDao(): QuestionAttemptDao
    abstract fun userAchievementDao(): UserAchievementDao
    abstract fun userGoalDao(): UserGoalDao
    abstract fun userStudyStatsDao(): UserStudyStatsDao
    abstract fun userLearningPatternDao(): UserLearningPatternDao
    abstract fun userNotificationDao(): NotificationDao
    
    // Quiz System DAO
    abstract fun quizDao(): QuizDao
    
    // Community DAOs
    abstract fun studyGroupDao(): StudyGroupDao
    abstract fun studyGroupMemberDao(): StudyGroupMemberDao
    abstract fun groupDiscussionDao(): GroupDiscussionDao
    abstract fun discussionReplyDao(): DiscussionReplyDao
    abstract fun sharedCollectionDao(): SharedCollectionDao
    abstract fun collectionItemDao(): CollectionItemDao
    abstract fun collectionDownloadDao(): CollectionDownloadDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun challengeParticipantDao(): ChallengeParticipantDao
    abstract fun nativeSpeakerDao(): NativeSpeakerDao
    abstract fun languageExchangeSessionDao(): LanguageExchangeSessionDao
    abstract fun userConnectionDao(): UserConnectionDao
    abstract fun userReputationDao(): UserReputationDao
    // Speaking Practice DAO
    abstract fun speakingPracticeDao(): SpeakingPracticeDao



    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hulaba_database"
                )
                .addMigrations(
                    MIGRATION_5_6, // Add new vocabulary and topic tables
                    MIGRATION_6_7, // Add user and learning system tables
                    MIGRATION_7_8, // Add community and social tables
                    MIGRATION_8_9, // Add AI and analytics tables
                    MIGRATION_9_10 // Final schema updates
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 5 to 6: Add comprehensive vocabulary and topic tables
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create vocabulary contexts table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS vocabulary_contexts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        icon TEXT,
                        color_hex TEXT,
                        description TEXT,
                        order_index INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                // Create user vocabulary progress table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS UserVocabularyProgress (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        wordId INTEGER NOT NULL,
                        status TEXT NOT NULL DEFAULT 'new',
                        confidenceLevel INTEGER NOT NULL DEFAULT 0,
                        lastReviewedAt INTEGER,
                        nextReviewAt INTEGER NOT NULL,
                        reviewCount INTEGER NOT NULL DEFAULT 0,
                        correctCount INTEGER NOT NULL DEFAULT 0,
                        incorrectCount INTEGER NOT NULL DEFAULT 0,
                        isFavorite INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        UNIQUE(userId, wordId)
                    )
                """)
                
                // Create vocabulary reviews table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS VocabularyReview (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userVocabularyProgressId INTEGER NOT NULL,
                        sessionId INTEGER,
                        difficultyRating INTEGER NOT NULL,
                        userAnswer TEXT,
                        correctAnswer TEXT,
                        wasCorrect INTEGER NOT NULL,
                        timeSpentSeconds INTEGER NOT NULL DEFAULT 0,
                        reviewedAt INTEGER NOT NULL
                    )
                """)
                
                // Create topic categories table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS TopicCategory (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        icon TEXT,
                        color_hex TEXT,
                        description TEXT,
                        order_index INTEGER NOT NULL DEFAULT 0,
                        parentCategoryId INTEGER,
                        isActive INTEGER NOT NULL DEFAULT 1
                    )
                """)
                
                // Create concepts table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS Concept (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        topicId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        definition TEXT,
                        useCase TEXT,
                        visualDiagramUrl TEXT,
                        codeExample TEXT,
                        germanTranslation TEXT,
                        germanDefinition TEXT,
                        germanUseCase TEXT,
                        sourcePageNumber INTEGER,
                        orderIndex INTEGER NOT NULL DEFAULT 0,
                        difficulty TEXT NOT NULL DEFAULT 'medium',
                        estimatedStudyTimeMinutes INTEGER,
                        relatedConceptIds TEXT,
                        tags TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create study materials table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS StudyMaterial (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        topicId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        fileUrl TEXT NOT NULL,
                        fileType TEXT NOT NULL DEFAULT 'pdf',
                        fileSizeBytes INTEGER,
                        pageCount INTEGER,
                        durationSeconds INTEGER,
                        thumbnailUrl TEXT,
                        description TEXT,
                        uploadedBy TEXT,
                        isPrimary INTEGER NOT NULL DEFAULT 0,
                        orderIndex INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL
                    )
                """)
                
                // Create user topic progress table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS UserTopicProgress (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        topicId TEXT NOT NULL,
                        status TEXT NOT NULL DEFAULT 'not_started',
                        progressPercentage REAL NOT NULL DEFAULT 0,
                        masteredConceptsCount INTEGER NOT NULL DEFAULT 0,
                        totalConceptsCount INTEGER NOT NULL DEFAULT 0,
                        lastStudiedAt INTEGER,
                        nextReviewAt INTEGER,
                        startedAt INTEGER NOT NULL,
                        completedAt INTEGER,
                        totalStudyTimeMinutes INTEGER NOT NULL DEFAULT 0,
                        averageSessionDurationMinutes INTEGER,
                        confidenceLevel INTEGER NOT NULL DEFAULT 0,
                        notes TEXT,
                        isFavorite INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        UNIQUE(userId, topicId)
                    )
                """)
                
                // Create user concept progress table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS UserConceptProgress (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        conceptId INTEGER NOT NULL,
                        topicId TEXT NOT NULL,
                        status TEXT NOT NULL DEFAULT 'new',
                        confidenceLevel INTEGER NOT NULL DEFAULT 0,
                        lastReviewedAt INTEGER,
                        nextReviewAt INTEGER,
                        reviewCount INTEGER NOT NULL DEFAULT 0,
                        correctReviewsCount INTEGER NOT NULL DEFAULT 0,
                        timeSpentMinutes INTEGER NOT NULL DEFAULT 0,
                        notes TEXT,
                        isFlagged INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        UNIQUE(userId, conceptId)
                    )
                """)
                
                // Create concept reviews table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS ConceptReview (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        conceptId INTEGER NOT NULL,
                        topicId TEXT NOT NULL,
                        sessionId INTEGER,
                        difficultyRating INTEGER NOT NULL,
                        confidenceBefore INTEGER NOT NULL DEFAULT 0,
                        confidenceAfter INTEGER NOT NULL DEFAULT 0,
                        timeSpentSeconds INTEGER NOT NULL DEFAULT 0,
                        userNotes TEXT,
                        reviewedAt INTEGER NOT NULL
                    )
                """)
                
                // Create user notes table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS UserNote (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        topicId TEXT,
                        conceptId INTEGER,
                        title TEXT,
                        content TEXT NOT NULL,
                        isPublic INTEGER NOT NULL DEFAULT 0,
                        tags TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create material page progress table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS MaterialPageProgress (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        materialId INTEGER NOT NULL,
                        pageNumber INTEGER NOT NULL,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        readingProgress REAL NOT NULL DEFAULT 0,
                        timeSpentSeconds INTEGER NOT NULL DEFAULT 0,
                        notes TEXT,
                        highlights TEXT,
                        lastReadAt INTEGER NOT NULL,
                        UNIQUE(userId, materialId, pageNumber)
                    )
                """)
            }
        }

        // Migration from version 6 to 7: Add user and learning system tables
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create users table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS users (
                        id TEXT PRIMARY KEY NOT NULL,
                        email TEXT NOT NULL UNIQUE,
                        username TEXT NOT NULL UNIQUE,
                        avatarUrl TEXT,
                        germanLevel TEXT NOT NULL DEFAULT 'A1',
                        targetGermanLevel TEXT NOT NULL DEFAULT 'C1',
                        dailyMixRatioWords INTEGER NOT NULL DEFAULT 60,
                        dailyMixRatioTopics INTEGER NOT NULL DEFAULT 40,
                        preferredSessionLength INTEGER NOT NULL DEFAULT 10,
                        streakCount INTEGER NOT NULL DEFAULT 0,
                        totalStudyTimeMinutes INTEGER NOT NULL DEFAULT 0,
                        wordsLearnedCount INTEGER NOT NULL DEFAULT 0,
                        topicsCompletedCount INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        lastStudyDate INTEGER,
                        notificationEnabled INTEGER NOT NULL DEFAULT 1,
                        offlineModeEnabled INTEGER NOT NULL DEFAULT 1
                    )
                """)
                
                // Create user settings table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_settings (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL UNIQUE,
                        theme TEXT NOT NULL DEFAULT 'auto',
                        fontSizeScale REAL NOT NULL DEFAULT 1.0,
                        animationEnabled INTEGER NOT NULL DEFAULT 1,
                        highContrastMode INTEGER NOT NULL DEFAULT 0,
                        colorBlindMode TEXT,
                        speechSpeed REAL NOT NULL DEFAULT 1.0,
                        autoPlayPronunciation INTEGER NOT NULL DEFAULT 1,
                        speechRecognitionEnabled INTEGER NOT NULL DEFAULT 1,
                        oneHandedMode INTEGER NOT NULL DEFAULT 0,
                        transitModeEnabled INTEGER NOT NULL DEFAULT 1,
                        quietHoursStart INTEGER,
                        quietHoursEnd INTEGER,
                        morningStartTime INTEGER NOT NULL DEFAULT 480,
                        morningEndTime INTEGER NOT NULL DEFAULT 600,
                        lunchStartTime INTEGER NOT NULL DEFAULT 750,
                        lunchEndTime INTEGER NOT NULL DEFAULT 780,
                        eveningStartTime INTEGER NOT NULL DEFAULT 1140,
                        eveningEndTime INTEGER NOT NULL DEFAULT 1260,
                        locationBasedTriggers INTEGER NOT NULL DEFAULT 1,
                        adaptToSchedule INTEGER NOT NULL DEFAULT 1,
                        weekendReminders INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                // Create user achievements table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_achievements (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        achievementType TEXT NOT NULL,
                        achievementLevel INTEGER NOT NULL DEFAULT 1,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        icon TEXT,
                        pointsAwarded INTEGER NOT NULL DEFAULT 0,
                        unlockedAt INTEGER NOT NULL,
                        progressValue REAL,
                        targetValue REAL
                    )
                """)
                
                // Create user goals table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_goals (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        goalType TEXT NOT NULL,
                        targetValue TEXT NOT NULL,
                        currentValue TEXT NOT NULL,
                        deadline INTEGER,
                        priority INTEGER NOT NULL DEFAULT 1,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create user study stats table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_study_stats (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        totalStudyTimeMinutes INTEGER NOT NULL DEFAULT 0,
                        wordsReviewedCount INTEGER NOT NULL DEFAULT 0,
                        conceptsReviewedCount INTEGER NOT NULL DEFAULT 0,
                        sessionsCount INTEGER NOT NULL DEFAULT 0,
                        averageSessionDurationMinutes REAL NOT NULL DEFAULT 0,
                        bestTimeOfDay INTEGER,
                        retentionRate REAL,
                        accuracyRate REAL,
                        streakDay INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        UNIQUE(userId, date)
                    )
                """)
                
                // Create user learning patterns table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_learning_patterns (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        patternType TEXT NOT NULL,
                        patternValue TEXT NOT NULL,
                        confidence REAL NOT NULL DEFAULT 0,
                        evidenceCount INTEGER NOT NULL DEFAULT 0,
                        lastUpdated INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """)
                
                // Create user notifications table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_notifications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        notificationType TEXT NOT NULL,
                        title TEXT NOT NULL,
                        message TEXT NOT NULL,
                        data TEXT,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        isActioned INTEGER NOT NULL DEFAULT 0,
                        scheduledFor INTEGER,
                        sentAt INTEGER,
                        createdAt INTEGER NOT NULL
                    )
                """)
            }
        }

        // Migration from version 7 to 8: Add community and social tables
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create study groups table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS study_groups (
                        id TEXT PRIMARY KEY NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT,
                        category TEXT NOT NULL,
                        languageLevel TEXT,
                        maxMembers INTEGER NOT NULL DEFAULT 100,
                        isPrivate INTEGER NOT NULL DEFAULT 0,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        coverImageUrl TEXT,
                        createdBy TEXT NOT NULL,
                        memberCount INTEGER NOT NULL DEFAULT 0,
                        lastActivityAt INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create study group members table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS study_group_members (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        groupId TEXT NOT NULL,
                        userId TEXT NOT NULL,
                        role TEXT NOT NULL DEFAULT 'member',
                        isActive INTEGER NOT NULL DEFAULT 1,
                        joinedAt INTEGER NOT NULL,
                        lastActiveAt INTEGER NOT NULL,
                        contributionsCount INTEGER NOT NULL DEFAULT 0,
                        reputationPoints INTEGER NOT NULL DEFAULT 0,
                        UNIQUE(groupId, userId)
                    )
                """)
                
                // Create group discussions table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS group_discussions (
                        id TEXT PRIMARY KEY NOT NULL,
                        groupId TEXT NOT NULL,
                        authorId TEXT NOT NULL,
                        title TEXT,
                        content TEXT NOT NULL,
                        discussionType TEXT NOT NULL DEFAULT 'general',
                        isPinned INTEGER NOT NULL DEFAULT 0,
                        isLocked INTEGER NOT NULL DEFAULT 0,
                        replyCount INTEGER NOT NULL DEFAULT 0,
                        likeCount INTEGER NOT NULL DEFAULT 0,
                        lastReplyAt INTEGER,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create discussion replies table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS discussion_replies (
                        id TEXT PRIMARY KEY NOT NULL,
                        discussionId TEXT NOT NULL,
                        authorId TEXT NOT NULL,
                        content TEXT NOT NULL,
                        parentReplyId TEXT,
                        likeCount INTEGER NOT NULL DEFAULT 0,
                        isAcceptedAnswer INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create shared collections table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS shared_collections (
                        id TEXT PRIMARY KEY NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        collectionType TEXT NOT NULL,
                        createdBy TEXT NOT NULL,
                        isPublic INTEGER NOT NULL DEFAULT 1,
                        isOfficial INTEGER NOT NULL DEFAULT 0,
                        coverImageUrl TEXT,
                        tags TEXT,
                        languageLevel TEXT,
                        downloadCount INTEGER NOT NULL DEFAULT 0,
                        ratingAverage REAL NOT NULL DEFAULT 0,
                        ratingCount INTEGER NOT NULL DEFAULT 0,
                        viewCount INTEGER NOT NULL DEFAULT 0,
                        likeCount INTEGER NOT NULL DEFAULT 0,
                        itemCount INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create collection items table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS collection_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        collectionId TEXT NOT NULL,
                        vocabularyId INTEGER,
                        conceptId INTEGER,
                        topicId TEXT,
                        orderIndex INTEGER NOT NULL DEFAULT 0,
                        addedBy TEXT,
                        addedAt INTEGER NOT NULL,
                        note TEXT
                    )
                """)
                
                // Create collection downloads table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS collection_downloads (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        collectionId TEXT NOT NULL,
                        userId TEXT NOT NULL,
                        downloadedAt INTEGER NOT NULL,
                        lastUsedAt INTEGER NOT NULL,
                        usageCount INTEGER NOT NULL DEFAULT 0,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        userRating INTEGER,
                        userReview TEXT,
                        UNIQUE(collectionId, userId)
                    )
                """)
                
                // Create challenges table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS challenges (
                        id TEXT PRIMARY KEY NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        challengeType TEXT NOT NULL,
                        difficulty TEXT NOT NULL DEFAULT 'medium',
                        durationDays INTEGER NOT NULL,
                        startDate INTEGER NOT NULL,
                        endDate INTEGER NOT NULL,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        isPublic INTEGER NOT NULL DEFAULT 1,
                        maxParticipants INTEGER,
                        requirements TEXT,
                        rewards TEXT,
                        coverImageUrl TEXT,
                        createdBy TEXT,
                        participantCount INTEGER NOT NULL DEFAULT 0,
                        completionRate REAL NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create challenge participants table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS challenge_participants (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        challengeId TEXT NOT NULL,
                        userId TEXT NOT NULL,
                        status TEXT NOT NULL DEFAULT 'joined',
                        progress REAL NOT NULL DEFAULT 0,
                        currentDay INTEGER NOT NULL DEFAULT 0,
                        streakDays INTEGER NOT NULL DEFAULT 0,
                        score INTEGER NOT NULL DEFAULT 0,
                        rank INTEGER,
                        joinedAt INTEGER NOT NULL,
                        lastActivityAt INTEGER NOT NULL,
                        completedAt INTEGER,
                        droppedAt INTEGER,
                        UNIQUE(challengeId, userId)
                    )
                """)
                
                // Create native speakers table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS native_speakers (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL UNIQUE,
                        language TEXT NOT NULL DEFAULT 'German',
                        dialect TEXT,
                        proficiencyLevel TEXT NOT NULL DEFAULT 'native',
                        isAvailable INTEGER NOT NULL DEFAULT 1,
                        hourlyRate INTEGER,
                        bio TEXT,
                        specialties TEXT,
                        teachingExperience TEXT,
                        certifications TEXT,
                        ratingAverage REAL NOT NULL DEFAULT 0,
                        ratingCount INTEGER NOT NULL DEFAULT 0,
                        sessionCount INTEGER NOT NULL DEFAULT 0,
                        responseTimeHours REAL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                // Create language exchange sessions table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS language_exchange_sessions (
                        id TEXT PRIMARY KEY NOT NULL,
                        learnerId TEXT NOT NULL,
                        nativeSpeakerId TEXT NOT NULL,
                        sessionType TEXT NOT NULL DEFAULT 'conversation',
                        scheduledFor INTEGER,
                        durationMinutes INTEGER NOT NULL DEFAULT 30,
                        status TEXT NOT NULL DEFAULT 'scheduled',
                        topic TEXT,
                        learnerLanguage TEXT NOT NULL DEFAULT 'English',
                        nativeLanguage TEXT NOT NULL DEFAULT 'German',
                        notes TEXT,
                        feedbackGiven INTEGER NOT NULL DEFAULT 0,
                        ratingGiven INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        startedAt INTEGER,
                        completedAt INTEGER
                    )
                """)
                
                // Create user connections table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_connections (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        connectedUserId TEXT NOT NULL,
                        connectionType TEXT NOT NULL DEFAULT 'friend',
                        status TEXT NOT NULL DEFAULT 'accepted',
                        isFavorite INTEGER NOT NULL DEFAULT 0,
                        notes TEXT,
                        sharedCollectionsCount INTEGER NOT NULL DEFAULT 0,
                        jointStudySessionsCount INTEGER NOT NULL DEFAULT 0,
                        lastInteractionAt INTEGER,
                        createdAt INTEGER NOT NULL,
                        UNIQUE(userId, connectedUserId)
                    )
                """)
                
                // Create user reputation table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_reputation (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL UNIQUE,
                        totalPoints INTEGER NOT NULL DEFAULT 0,
                        helpfulAnswersCount INTEGER NOT NULL DEFAULT 0,
                        sharedCollectionsCount INTEGER NOT NULL DEFAULT 0,
                        collectionDownloadsCount INTEGER NOT NULL DEFAULT 0,
                        studyGroupContributionsCount INTEGER NOT NULL DEFAULT 0,
                        nativeSpeakerSessionsCount INTEGER NOT NULL DEFAULT 0,
                        challengeWinsCount INTEGER NOT NULL DEFAULT 0,
                        ratingAverage REAL NOT NULL DEFAULT 0,
                        ratingCount INTEGER NOT NULL DEFAULT 0,
                        level TEXT NOT NULL DEFAULT 'beginner',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
            }
        }

        // Additional migrations for versions 8-10 would go here
        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add AI and analytics specific tables
                // This would include tables for AI-generated content, analytics, etc.
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Final schema updates and optimizations
            }
        }
    }
}
