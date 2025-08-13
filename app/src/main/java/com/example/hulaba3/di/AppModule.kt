package com.example.hulaba3.di

import com.example.hulaba3.data.database.AppDatabase
import com.example.hulaba3.data.repository.TopicRepository
import com.example.hulaba3.data.repository.WordRepository
import com.example.hulaba3.utils.ReminderApi
import com.example.hulaba3.utils.NotificationScheduler
import com.example.hulaba3.viewmodel.TopicViewModel
import com.example.hulaba3.viewmodel.WordViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
        single { AppDatabase.getDatabase(get()) }  // Provide AppDatabase instance
        single { get<AppDatabase>().topicDao() }
        single { get<AppDatabase>().wordDao() }
        single { WordRepository(get()) }
        single { TopicRepository(get()) } // Provide TopicRepository
        // Provide NotificationManager
        single { ReminderApi(androidContext()) }
        single { NotificationScheduler }
        // Ensure NotificationScheduler is available
        viewModel { WordViewModel(get()) }  // Inject AppDatabase into WordViewModel
        viewModel { TopicViewModel(get(), get()) }  // Inject dependencies for TopicViewModel
}

