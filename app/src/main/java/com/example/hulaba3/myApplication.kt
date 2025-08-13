package com.example.hulaba3



import android.app.Application
import com.example.hulaba3.di.appModule
import com.example.hulaba3.utils.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createNotificationChannel(this)

        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}