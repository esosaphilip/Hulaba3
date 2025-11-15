package com.example.hulaba3.di

import com.example.hulaba3.data.database.AppDatabase
import com.example.hulaba3.data.repository.*
import com.example.hulaba3.data.supabase.AuthService
import com.example.hulaba3.data.supabase.SupabaseClient
import com.example.hulaba3.data.supabase.SupabaseRepository
import com.example.hulaba3.uilayer.screens.learning.GermanTechLearningViewModel
import com.example.hulaba3.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin modules providing app-wide singletons and ViewModels
 */
val appModule = module {
    // Database
    single { AppDatabase.getDatabase(get()) }

    // DAOs (add as needed by repositories and viewmodels)
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().userSettingsDao() }
    single { get<AppDatabase>().userAchievementDao() }
    single { get<AppDatabase>().userGoalDao() }
    single { get<AppDatabase>().userStudyStatsDao() }
    single { get<AppDatabase>().userLearningPatternDao() }
    single { get<AppDatabase>().userNotificationDao() }
    single { get<AppDatabase>().wordDao() }
    single { get<AppDatabase>().vocabularyContextDao() }
    single { get<AppDatabase>().userVocabularyProgressDao() }
    single { get<AppDatabase>().vocabularyReviewDao() }
    single { get<AppDatabase>().topicDao() }
    single { get<AppDatabase>().topicCategoryDao() }
    single { get<AppDatabase>().conceptDao() }
    single { get<AppDatabase>().studyMaterialDao() }
    single { get<AppDatabase>().userTopicProgressDao() }
    single { get<AppDatabase>().userConceptProgressDao() }
    single { get<AppDatabase>().conceptReviewDao() }
    single { get<AppDatabase>().userNoteDao() }
    single { get<AppDatabase>().materialPageProgressDao() }
    single { get<AppDatabase>().studyGroupDao() }
    single { get<AppDatabase>().studyGroupMemberDao() }
    single { get<AppDatabase>().groupDiscussionDao() }
    single { get<AppDatabase>().discussionReplyDao() }
    single { get<AppDatabase>().sharedCollectionDao() }
    single { get<AppDatabase>().collectionItemDao() }
    single { get<AppDatabase>().collectionDownloadDao() }
    single { get<AppDatabase>().challengeDao() }
    single { get<AppDatabase>().challengeParticipantDao() }
    single { get<AppDatabase>().nativeSpeakerDao() }
    single { get<AppDatabase>().languageExchangeSessionDao() }
    single { get<AppDatabase>().userConnectionDao() }
    single { get<AppDatabase>().userReputationDao() }

    // Repositories & services
    single { SupabaseClient() }
    single { AuthService(get()) }
    single { SupabaseRepository(get(), get()) }
    single { GermanTechRepository() }
    single { WordRepository(get()) }
    single { TopicRepository(get()) }
    single { StudySessionRepository(get(), get()) }
    single { LearningRepository() }
    single { UserRepository() }

    // ViewModels
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { TopicDetailViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { GermanTechLearningViewModel(get()) }
    viewModel {
        LearningModeViewModel(
            wordRepository = get(),
            topicRepository = get(),
            studySessionRepository = get(),
            userVocabularyProgressDao = get(),
            userConceptProgressDao = get(),
            vocabularyReviewDao = get(),
            conceptReviewDao = get()
        )
    }
}