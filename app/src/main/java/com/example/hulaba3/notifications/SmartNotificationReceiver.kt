package com.example.hulaba3.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.hulaba3.MainActivity

class SmartNotificationReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_QUICK_REVIEW = "com.example.hulaba3.ACTION_QUICK_REVIEW"
        const val ACTION_SAVE_LATER = "com.example.hulaba3.ACTION_SAVE_LATER"
        const val EXTRA_WORD_ID = "extra_word_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val wordId = intent.getLongExtra(EXTRA_WORD_ID, -1L)
        if (wordId == -1L) {
            Log.w("SmartNotificationReceiver", "Missing wordId in intent")
            return
        }

        when (intent.action) {
            ACTION_QUICK_REVIEW -> {
                // Deep link into Smart Word Card for this word
                val deepLinkUri = Uri.parse("hulaba://smartWord/$wordId")
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = deepLinkUri
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(launchIntent)
            }
            ACTION_SAVE_LATER -> {
                // Delegate handling to the app UI. This keeps receivers lightweight and avoids DB access in broadcast context.
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("navigate_to", "smart_word_save_later")
                    putExtra("word_id", wordId)
                }
                context.startActivity(launchIntent)
                Log.d("SmartNotificationReceiver", "Save later requested for wordId=$wordId")
            }
            else -> Log.w("SmartNotificationReceiver", "Unknown action: ${intent.action}")
        }
    }
}