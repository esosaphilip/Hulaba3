package com.example.hulaba3

import android.app.Application
import com.example.hulaba3.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.core.context.startKoin

class HulabaApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Start Koin for dependency injection
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@HulabaApplication)
            modules(appModule)
        }
        // Initialize any application-wide services or configurations
        initializeServices()
    }
    
    private fun initializeServices() {
        // Initialize notification channels
        // Initialize background sync
        // Initialize analytics
        // Initialize crash reporting
    }
}