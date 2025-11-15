package com.example.hulaba3



import android.app.Application
import com.example.hulaba3.utils.NotificationHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createNotificationChannel(this)

        // Koin initialization removed temporarily due to missing dependency.
        // If you intend to use Koin, add the koin-android dependency and restore:
        // startKoin {
        //     androidContext(this@MyApplication)
        //     modules(appModule)
        // }
    }
}