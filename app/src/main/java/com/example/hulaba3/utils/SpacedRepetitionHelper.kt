package com.example.hulaba3.utils

import android.util.Log

object SpacedRepetitionHelper {
    // SuperMemo-2 recommended intervals in days
    private val reviewIntervalsDays = listOf(1, 3, 7, 16, 35)

    /**
     * Returns the absolute time (in millis) for the next review.
     * For new words (lastReviewDate == null), schedules from current time.
     * For reviewed words, schedules from the last review date.
     */
    fun getNextReviewTime(lastReviewDate: Long?, reviewCount: Int): Long {
        val now = System.currentTimeMillis()
        val index = reviewCount.coerceIn(0, reviewIntervalsDays.lastIndex)
        val days = reviewIntervalsDays[index]
        val intervalMillis = days * 24 * 60 * 60 * 1000L

        val nextReviewTime = if (lastReviewDate == null) {
            // For new words, schedule from now
            now + intervalMillis
        } else {
            // For reviewed words, schedule from last review
            lastReviewDate + intervalMillis
        }

        Log.d("SpacedRepetitionHelper", "Review scheduled in $days days (reviewCount: $reviewCount)")
        return nextReviewTime
    }
}