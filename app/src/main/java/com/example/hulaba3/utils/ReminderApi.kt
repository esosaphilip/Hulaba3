package com.example.hulaba3.utils

import android.content.Context
import com.example.hulaba3.data.database.Word


class ReminderApi(private val context: Context) {

    // Schedule reminder for a word
    fun scheduleWordReminder(word: Word) {
        NotificationScheduler.scheduleWordReminder(context, word)
    }
}
