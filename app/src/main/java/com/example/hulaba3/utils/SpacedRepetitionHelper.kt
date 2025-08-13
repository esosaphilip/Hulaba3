package com.example.hulaba3.utils

object SpacedRepetitionHelper {
    // SuperMemo-2 recommended intervals in days
    private val reviewIntervalsDays = listOf(1, 3, 7, 16, 35)

    /**
     * Returns the absolute time (in millis) for the next review.
     * If lastReviewDate == null, start from "now" and add the first interval.
     * If reviewCount >= intervals size, keep using the last interval.
     */
    fun getNextReviewTime(lastReviewDate: Long?, reviewCount: Int): Long {
        val base = lastReviewDate ?: System.currentTimeMillis()
        val index = reviewCount.coerceIn(0, reviewIntervalsDays.lastIndex)
        val days = reviewIntervalsDays[index]
        return base + days * 24 * 60 * 60 * 1000L
    }
}
