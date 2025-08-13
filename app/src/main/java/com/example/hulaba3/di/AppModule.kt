package com.example.hulaba3.di

import com.example.hulaba3.data.database.AppDatabase
import com.example.hulaba3.data.repository.TopicRepository
import com.example.hulaba3.data.repository.WordRepository
import com.example.hulaba3.utils.ReminderApi
import com.example.hulaba3.viewmodel.TopicViewModel
import com.example.hulaba3.viewmodel.WordViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
        single { AppDatabase.getDatabase(get()) }
        single { get<AppDatabase>().topicDao() }
        single { get<AppDatabase>().wordDao() }
        single { WordRepository(get()) }
        single { TopicRepository(get()) }
        single { ReminderApi(androidContext()) }

        viewModel { WordViewModel(get()) }
        viewModel { TopicViewModel(get()) } // Removed NotificationScheduler parameter
}