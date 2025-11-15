package com.example.hulaba3.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class NotificationActionReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "NotificationActionReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val notificationId = intent.getIntExtra("notification_id", -1)
        val itemId = intent.getLongExtra("item_id", -1)
        val itemType = intent.getStringExtra("item_type")
        
        Log.d(TAG, "Received action: $action for notification: $notificationId")
        
        // Cancel the notification
        NotificationManagerCompat.from(context).cancel(notificationId)
        
        when (action) {
            "REVIEW_NOW" -> handleReviewNow(context, itemId, itemType)
            "MARK_KNOWN" -> handleMarkKnown(context, itemId, itemType)
            "MARK_UNKNOWN" -> handleMarkUnknown(context, itemId, itemType)
            "START_LEARNING" -> handleStartLearning(context)
            "TRY_LEARNING_MODE" -> handleTryLearningMode(context)
            "SNOOZE" -> handleSnooze(context, itemId, itemType)
        }
    }
    
    private fun handleReviewNow(context: Context, itemId: Long, itemType: String?) {
        // Avoid direct DB mutations here; just navigate into the app.
        openAppToReview(context, itemType ?: "word", itemId)
    }
    
    private fun handleMarkKnown(context: Context, itemId: Long, itemType: String?) {
        // Let in-app logic handle marking progress as known
        openAppToLearning(context)
    }
    
    private fun handleMarkUnknown(context: Context, itemId: Long, itemType: String?) {
        // Let in-app logic handle weaker recall
        openAppToLearning(context)
    }
    
    private fun handleStartLearning(context: Context) {
        // Open the app to the main learning screen
        openAppToLearning(context)
    }
    
    private fun handleTryLearningMode(context: Context) {
        // Open the app to mixed learning mode
        openAppToMixedLearning(context)
    }

    private fun handleSnooze(context: Context, itemId: Long, itemType: String?) {
        // Minimal snooze handling: simply open the app; proper rescheduling can be done in-app
        openAppToLearning(context)
    }
    
    // Removed calculation here; review scheduling should be handled by ViewModels/services when the app is opened.
    
    private fun openAppToReview(context: Context, itemType: String, itemId: Long) {
        // Create intent to open the app and navigate to the specific review screen
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "review")
            putExtra("item_type", itemType)
            putExtra("item_id", itemId)
        }
        
        intent?.let {
            context.startActivity(it)
        }
    }
    
    private fun openAppToLearning(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "learning")
        }
        intent?.let { context.startActivity(it) }
    }
    
    private fun openAppToMixedLearning(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "mixed_learning")
        }
        intent?.let { context.startActivity(it) }
    }
}