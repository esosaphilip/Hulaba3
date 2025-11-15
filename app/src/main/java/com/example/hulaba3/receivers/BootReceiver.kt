package com.example.hulaba3.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.hulaba3.notifications.SmartNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Boot receiver triggered: ${intent.action}")
        
        // Only reschedule notifications on boot completion
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleNotifications(context)
        }
    }
    
    private fun rescheduleNotifications(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val notificationManager = SmartNotificationManager(context)
                notificationManager.rescheduleAllNotifications()
                Log.d(TAG, "Notifications rescheduled after boot")
            } catch (e: Exception) {
                Log.e(TAG, "Error rescheduling notifications", e)
            }
        }
    }
}