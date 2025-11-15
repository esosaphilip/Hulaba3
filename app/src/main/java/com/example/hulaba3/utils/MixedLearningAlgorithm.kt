package com.example.hulaba3.utils

import com.example.hulaba3.data.database.*
import java.util.*

/**
 * Mixed Learning Algorithm for interleaving vocabulary and topic concepts
 * Implements smart ordering based on retention and learning patterns
 */
object MixedLearningAlgorithm {
    
    data class MixedLearningItem(
        val id: String,
        val type: LearningType,
        val content: Any,
        val priority: Float,
        val nextReviewTime: Long,
        val difficulty: String,
        val estimatedTime: Int
    )
    
    enum class LearningType {
        VOCABULARY,
        TOPIC_CONCEPT
    }
    
    data class MixedLearningConfig(
        val totalItems: Int = 20,
        val vocabularyRatio: Float = 0.6f,
        val topicRatio: Float = 0.4f,
        val prioritizeOverdue: Boolean = true,
        val difficultyWeight: Float = 0.3f,
        val retentionWeight: Float = 0.4f,
        val timeWeight: Float = 0.3f
    )
    
    /**
     * Generate mixed learning session items based on configuration
     */
    fun generateMixedLearningItems(
        vocabularyItems: List<UserVocabularyProgress>,
        topicConcepts: List<UserConceptProgress>,
        config: MixedLearningConfig = MixedLearningConfig()
    ): List<MixedLearningItem> {
        val now = System.currentTimeMillis()
        val mixedItems = mutableListOf<MixedLearningItem>()
        
        // Process vocabulary items
        val vocabItems = vocabularyItems.map { vocab ->
            MixedLearningItem(
                id = "vocab_${vocab.wordId}",
                type = LearningType.VOCABULARY,
                content = vocab,
                priority = calculatePriority(vocab, now, config),
                nextReviewTime = vocab.nextReviewAt,
                difficulty = vocab.confidenceLevel.toString(),
                estimatedTime = when (vocab.status) {
                    "new" -> 3
                    "learning" -> 2
                    "review" -> 1
                    else -> 2
                }
            )
        }
        
        // Process topic concepts
        val conceptItems = topicConcepts.map { concept ->
            MixedLearningItem(
                id = "concept_${concept.conceptId}",
                type = LearningType.TOPIC_CONCEPT,
                content = concept,
                priority = calculatePriority(concept, now, config),
                nextReviewTime = concept.nextReviewAt ?: now,
                difficulty = concept.status,
                estimatedTime = when (concept.status) {
                    "new" -> 5
                    "learning" -> 3
                    "review" -> 2
                    else -> 3
                }
            )
        }
        
        // Apply ratio constraints
        val targetVocabCount = (config.totalItems * config.vocabularyRatio).toInt()
        val targetConceptCount = (config.totalItems * config.topicRatio).toInt()
        
        // Sort by priority and select top items for each category
        val selectedVocab = vocabItems.sortedByDescending { it.priority }
            .take(targetVocabCount)
        val selectedConcepts = conceptItems.sortedByDescending { it.priority }
            .take(targetConceptCount)
        
        // Combine and shuffle with smart ordering
        mixedItems.addAll(selectedVocab)
        mixedItems.addAll(selectedConcepts)
        
        // Apply smart ordering to avoid same-type clustering
        return applySmartOrdering(mixedItems, config)
    }
    
    /**
     * Calculate priority score for learning items
     */
    private fun calculatePriority(
        item: Any,
        now: Long,
        config: MixedLearningConfig
    ): Float {
        return when (item) {
            is UserVocabularyProgress -> calculateVocabPriority(item, now, config)
            is UserConceptProgress -> calculateConceptPriority(item, now, config)
            else -> 0.5f
        }
    }
    
    private fun calculateVocabPriority(
        vocab: UserVocabularyProgress,
        now: Long,
        config: MixedLearningConfig
    ): Float {
        val overdueScore = if (config.prioritizeOverdue && vocab.nextReviewAt < now) 1.0f else 0.0f
        val difficultyScore = when (vocab.confidenceLevel) {
            in 0..2 -> 0.8f
            in 3..5 -> 0.6f
            in 6..8 -> 0.4f
            else -> 0.2f
        }
        val retentionScore = 1.0f - (vocab.correctCount.toFloat() / (vocab.reviewCount + 1).toFloat())

        val estimatedTime = estimatedTimeForVocab(vocab)

        val overdueWeight = if (config.prioritizeOverdue) 1.0f else 0.0f
        return (overdueWeight * overdueScore * 0.4f) +
               (config.difficultyWeight * difficultyScore) +
               (config.retentionWeight * retentionScore) +
               (config.timeWeight * (1.0f / estimatedTime))
    }
    
    private fun calculateConceptPriority(
        concept: UserConceptProgress,
        now: Long,
        config: MixedLearningConfig
    ): Float {
        val overdueScore = if (config.prioritizeOverdue && (concept.nextReviewAt ?: 0) < now) 1.0f else 0.0f
        val difficultyScore = when (concept.status) {
            "new" -> 0.8f
            "learning" -> 0.6f
            "review" -> 0.4f
            else -> 0.2f
        }
        val retentionScore = 1.0f - (concept.correctReviewsCount.toFloat() / (concept.reviewCount + 1).toFloat())
        
        val overdueWeight = if (config.prioritizeOverdue) 1.0f else 0.0f
        return (overdueWeight * overdueScore * 0.4f) +
               (config.difficultyWeight * difficultyScore) +
               (config.retentionWeight * retentionScore) +
               (config.timeWeight * (1.0f / concept.timeSpentMinutes.coerceAtLeast(1)))
    }
    
    /**
     * Apply smart ordering to avoid same-type clustering
     */
    private fun applySmartOrdering(
        items: List<MixedLearningItem>,
        config: MixedLearningConfig
    ): List<MixedLearningItem> {
        val orderedItems = mutableListOf<MixedLearningItem>()
        val vocabQueue = items.filter { it.type == LearningType.VOCABULARY }
            .sortedByDescending { it.priority }
            .toMutableList()
        val conceptQueue = items.filter { it.type == LearningType.TOPIC_CONCEPT }
            .sortedByDescending { it.priority }
            .toMutableList()
        
        // Use smart interleaving pattern
        var vocabIndex = 0
        var conceptIndex = 0
        val pattern = generateInterleavingPattern(vocabQueue.size, conceptQueue.size)
        
        for (type in pattern) {
            when (type) {
                LearningType.VOCABULARY -> {
                    if (vocabIndex < vocabQueue.size) {
                        orderedItems.add(vocabQueue[vocabIndex])
                        vocabIndex++
                    } else if (conceptIndex < conceptQueue.size) {
                        orderedItems.add(conceptQueue[conceptIndex])
                        conceptIndex++
                    }
                }
                LearningType.TOPIC_CONCEPT -> {
                    if (conceptIndex < conceptQueue.size) {
                        orderedItems.add(conceptQueue[conceptIndex])
                        conceptIndex++
                    } else if (vocabIndex < vocabQueue.size) {
                        orderedItems.add(vocabQueue[vocabIndex])
                        vocabIndex++
                    }
                }
            }
        }
        
        return orderedItems
    }

    private fun estimatedTimeForVocab(vocab: UserVocabularyProgress): Int {
        return when (vocab.status) {
            "new" -> 3
            "learning" -> 2
            "reviewing", "review" -> 1
            else -> 2
        }
    }
    
    /**
     * Generate optimal interleaving pattern based on ratios
     */
    private fun generateInterleavingPattern(vocabCount: Int, conceptCount: Int): List<LearningType> {
        val pattern = mutableListOf<LearningType>()
        val total = vocabCount + conceptCount
        val vocabRatio = vocabCount.toFloat() / total
        
        // Create balanced pattern
        val vocabPerGroup = if (vocabRatio >= 0.6f) 2 else 1
        val conceptPerGroup = if (vocabRatio <= 0.4f) 2 else 1
        
        var vocabAdded = 0
        var conceptAdded = 0
        
        while (vocabAdded < vocabCount || conceptAdded < conceptCount) {
            // Add vocabulary items
            for (i in 0 until vocabPerGroup) {
                if (vocabAdded < vocabCount) {
                    pattern.add(LearningType.VOCABULARY)
                    vocabAdded++
                }
            }
            
            // Add concept items
            for (i in 0 until conceptPerGroup) {
                if (conceptAdded < conceptCount) {
                    pattern.add(LearningType.TOPIC_CONCEPT)
                    conceptAdded++
                }
            }
        }
        
        return pattern
    }
    
    /**
     * Adaptive learning based on session performance
     */
    fun adaptConfiguration(
        currentConfig: MixedLearningConfig,
        sessionPerformance: SessionPerformance
    ): MixedLearningConfig {
        return when (sessionPerformance.overallAccuracy) {
            in 0.8f..1.0f -> currentConfig.copy(
                vocabularyRatio = (currentConfig.vocabularyRatio * 1.1f).coerceAtMost(0.8f),
                topicRatio = (currentConfig.topicRatio * 0.9f).coerceAtLeast(0.2f)
            )
            in 0.6f..0.8f -> currentConfig.copy(
                difficultyWeight = (currentConfig.difficultyWeight * 1.2f).coerceAtMost(0.5f)
            )
            in 0.0f..0.6f -> currentConfig.copy(
                difficultyWeight = (currentConfig.difficultyWeight * 1.5f).coerceAtMost(0.6f),
                retentionWeight = (currentConfig.retentionWeight * 1.3f).coerceAtMost(0.5f)
            )
            else -> currentConfig
        }
    }
    
    data class SessionPerformance(
        val totalItems: Int,
        val correctItems: Int,
        val timeSpent: Long,
        val vocabularyAccuracy: Float,
        val topicAccuracy: Float
    ) {
        val overallAccuracy: Float = correctItems.toFloat() / totalItems.toFloat()
    }
}