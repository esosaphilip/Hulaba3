package com.example.hulaba3.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.utils.NotificationScheduler
import AppDatabase
import com.example.hulaba3.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getDatabase(context)
                        val wordDao = db.wordDao()
                        val existing = wordDao.getWordById(wordId)
                        if (existing != null) {
                            val updated: Word = existing.copy(
                                savedForLater = true,
                                nextReviewTime = System.currentTimeMillis() + 45L * 60L * 1000L
                            )
                            wordDao.updateWord(updated)
                            NotificationScheduler.scheduleWordReminder(context, updated)
                            Log.d("SmartNotificationReceiver", "Word ${existing.word} saved for later")
                        } else {
                            Log.w("SmartNotificationReceiver", "Word not found: $wordId")
                        }
                    } catch (e: Exception) {
                        Log.e("SmartNotificationReceiver", "Error handling SAVE_LATER: ${e.localizedMessage}")
                    }
                }
            }
            else -> Log.w("SmartNotificationReceiver", "Unknown action: ${intent.action}")
        }
    }
}