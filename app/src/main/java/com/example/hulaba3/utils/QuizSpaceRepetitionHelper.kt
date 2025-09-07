package com.example.hulaba3.utils


import com.example.hulaba3.data.database.Question

object QuizSpacedRepetitionHelper {

    /**
     * SM-2 algorithm implementation for quiz questions
     * Based on user performance (correct/incorrect answers)
     */
    fun updateQuestionAfterAttempt(
        question: Question,
        wasCorrect: Boolean,
        responseQuality: Int = if (wasCorrect) 4 else 1 // 0-5 scale
    ): Question {
        val now = System.currentTimeMillis()

        return if (wasCorrect) {
            // Correct answer - increase interval
            val newCorrectStreak = question.correctStreak + 1
            val newReviewCount = question.reviewCount + 1
            val newEaseFactor = calculateNewEaseFactor(question.easeFactor, responseQuality)
            val newInterval = calculateInterval(newReviewCount, newEaseFactor)

            question.copy(
                lastReviewed = now,
                reviewCount = newReviewCount,
                nextReviewTime = now + newInterval,
                easeFactor = newEaseFactor,
                correctStreak = newCorrectStreak
            )
        } else {
            // Incorrect answer - reset to beginning
            question.copy(
                lastReviewed = now,
                reviewCount = 0,
                nextReviewTime = now + (24 * 60 * 60 * 1000), // 1 day
                easeFactor = maxOf(1.3f, question.easeFactor - 0.2f),
                correctStreak = 0,
                incorrectCount = question.incorrectCount + 1
            )
        }
    }

    private fun calculateNewEaseFactor(currentEaseFactor: Float, responseQuality: Int): Float {
        val newEF = currentEaseFactor + (0.1f - (5 - responseQuality) * (0.08f + (5 - responseQuality) * 0.02f))
        return maxOf(1.3f, newEF) // Minimum ease factor is 1.3
    }

    private fun calculateInterval(reviewCount: Int, easeFactor: Float): Long {
        val days = when (reviewCount) {
            0 -> 1
            1 -> 6
            else -> {
                val previousInterval = calculateInterval(reviewCount - 1, easeFactor)
                (previousInterval / (24 * 60 * 60 * 1000) * easeFactor).toLong()
            }
        }
        return days * 24 * 60 * 60 * 1000 // Convert to milliseconds
    }
}
