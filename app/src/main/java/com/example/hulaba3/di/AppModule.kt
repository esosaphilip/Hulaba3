package com.example.hulaba3.di


import AppDatabase
import com.example.hulaba3.data.repository.TopicRepository
import com.example.hulaba3.data.repository.WordRepository
import com.example.hulaba3.data.repository.QuestionRepository
import com.example.hulaba3.data.repository.StudySessionRepository
import com.example.hulaba3.utils.GeminiApiService
import com.example.hulaba3.utils.PdfTextExtractor
import com.example.hulaba3.utils.QuizGenerationService
import com.example.hulaba3.utils.ReminderApi
import com.example.hulaba3.viewmodel.TopicViewModel
import com.example.hulaba3.viewmodel.WordViewModel
import com.example.hulaba3.viewmodel.QuizViewModel
import com.example.hulaba3.viewmodel.StudySessionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
        // Database
        single { AppDatabase.getDatabase(get()) }

        // DAOs
        single { get<AppDatabase>().topicDao() }
        single { get<AppDatabase>().wordDao() }
        single { get<AppDatabase>().questionDao() }
        single { get<AppDatabase>().answerDao() }
        single { get<AppDatabase>().studySessionDao() }
        single { get<AppDatabase>().questionAttemptDao() }

        // Existing Repositories
        single { WordRepository(get()) }
        single { TopicRepository(get()) }

        // New Repositories for AI Quiz Features
        single { QuestionRepository(get(), get()) } // questionDao, answerDao
        single { StudySessionRepository(get(), get()) } // studySessionDao, questionAttemptDao

        // Services
        single { PdfTextExtractor(androidContext()) }
        single { GeminiApiService() }
        single { QuizGenerationService(androidContext(), get(), get(), get()) }
        single { ReminderApi(androidContext()) }

        // ViewModels
        viewModel { WordViewModel(get()) }
        viewModel { TopicViewModel(get()) }
        viewModel { QuizViewModel(get(), get(), get()) } // questionRepository, studySessionRepository, quizGenerationService
        viewModel { StudySessionViewModel(get(), get()) } // studySessionRepository, questionRepository
}