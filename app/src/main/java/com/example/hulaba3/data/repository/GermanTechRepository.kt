package com.example.hulaba3.data.repository

import com.example.hulaba3.uilayer.screens.learning.GermanTechWord

class GermanTechRepository {

    private val now = System.currentTimeMillis()

    private val sampleWords: List<GermanTechWord> = listOf(
        GermanTechWord(
            id = "auto",
            german = "das Auto",
            english = "car",
            pronunciation = "AH-toh",
            category = "Automotive",
            difficulty = "Beginner",
            exampleSentence = "Ich habe ein Auto.",
            contextExplanation = "Common vehicle term.",
            technicalDetails = "General noun; neuter gender 'das'.",
            masteryLevel = 0,
            lastReviewed = now - 2 * 24 * 60 * 60 * 1000,
            reviewCount = 0,
            nextReviewTime = now + 24 * 60 * 60 * 1000
        ),
        GermanTechWord(
            id = "computer",
            german = "der Computer",
            english = "computer",
            pronunciation = "kom-POO-ter",
            category = "IT",
            difficulty = "Beginner",
            exampleSentence = "Der Computer ist schnell.",
            contextExplanation = "Basic tech term.",
            technicalDetails = "Masculine noun 'der'.",
            masteryLevel = 0,
            lastReviewed = now - 3 * 24 * 60 * 60 * 1000,
            reviewCount = 0,
            nextReviewTime = now + 24 * 60 * 60 * 1000
        ),
        GermanTechWord(
            id = "netzwerk",
            german = "das Netzwerk",
            english = "network",
            pronunciation = "NETZ-verk",
            category = "IT",
            difficulty = "Intermediate",
            exampleSentence = "Das Netzwerk ist stabil.",
            contextExplanation = "Used in networking contexts.",
            technicalDetails = "Neuter noun 'das'.",
            masteryLevel = 1,
            lastReviewed = now - 1 * 24 * 60 * 60 * 1000,
            reviewCount = 1,
            nextReviewTime = now + 2 * 24 * 60 * 60 * 1000
        ),
        GermanTechWord(
            id = "datenbank",
            german = "die Datenbank",
            english = "database",
            pronunciation = "DAH-ten-bank",
            category = "IT",
            difficulty = "Intermediate",
            exampleSentence = "Die Datenbank speichert Informationen.",
            contextExplanation = "Relational/NoSQL context.",
            technicalDetails = "Feminine noun 'die'.",
            masteryLevel = 1,
            lastReviewed = now - 5 * 24 * 60 * 60 * 1000,
            reviewCount = 2,
            nextReviewTime = now + 1 * 24 * 60 * 60 * 1000
        ),
        GermanTechWord(
            id = "algorithmus",
            german = "der Algorithmus",
            english = "algorithm",
            pronunciation = "al-go-RITH-moos",
            category = "Computer Science",
            difficulty = "Advanced",
            exampleSentence = "Der Algorithmus löst das Problem effizient.",
            contextExplanation = "CS concept.",
            technicalDetails = "Masculine noun with irregular plural.",
            masteryLevel = 2,
            lastReviewed = now - 10 * 24 * 60 * 60 * 1000,
            reviewCount = 3,
            nextReviewTime = now + 3 * 24 * 60 * 60 * 1000
        )
    )

    fun getMixedLearningWords(limit: Int): List<GermanTechWord> {
        return sampleWords.take(limit)
    }

    fun startLearningSession(sessionId: String) {
        // TODO: persist session start
    }

    fun recordAnswer(sessionId: String, wordId: String, isCorrect: Boolean, answerTime: Long) {
        // TODO: persist answer
    }

    fun completeLearningSession(sessionId: String) {
        // TODO: persist session completion
    }
}