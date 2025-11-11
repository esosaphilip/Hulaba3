package com.example.hulaba3.utils

import android.util.Log

object SpacedRepetitionHelper {
    // SuperMemo-2 recommended intervals in days
    private val reviewIntervalsDays = listOf(1, 3, 7, 16, 35)

    enum class ReviewQuality { EASY, GOOD, HARD }

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

    /**
     * Compute the next review schedule given a quality rating.
     * Simple Leitner-like logic using existing reviewCount stages:
     * - EASY: advance 2 stages
     * - GOOD: advance 1 stage
     * - HARD: step back 1 stage (min 0)
     */
    fun computeNextWithQuality(
        lastReviewDate: Long?,
        reviewCount: Int,
        quality: ReviewQuality
    ): Pair<Int, Long> {
        val newCount = when (quality) {
            ReviewQuality.EASY -> (reviewCount + 2)
            ReviewQuality.GOOD -> (reviewCount + 1)
            ReviewQuality.HARD -> (reviewCount - 1).coerceAtLeast(0)
        }.coerceAtMost(reviewIntervalsDays.lastIndex)

        val nextTime = getNextReviewTime(lastReviewDate, newCount)
        return newCount to nextTime
    }
}