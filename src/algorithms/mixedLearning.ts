import {
  Word,
  Concept,
  GermanTechIntegration,
  LearningMode,
  DifficultyLevel,
  MasteryLevel,
  StudySession
} from '@/types';

export interface MixedLearningConfig {
  wordWeight: number;
  conceptWeight: number;
  techWeight: number;
  difficultyDistribution: Record<DifficultyLevel, number>;
  masteryThresholds: Record<MasteryLevel, number>;
  interleavingFactor: number; // 0-1, higher = more mixing
  spacedRepetitionFactor: number; // 0-1, higher = more spaced repetition
}

export interface LearningItem {
  id: string;
  type: 'word' | 'concept' | 'tech';
  item: Word | Concept | GermanTechIntegration;
  difficulty: DifficultyLevel;
  mastery: MasteryLevel;
  priority: number;
  lastStudied?: Date;
  nextReview?: Date;
  reviewCount: number;
  correctCount: number;
}

export class MixedLearningAlgorithm {
  private config: MixedLearningConfig;
  
  constructor(config: Partial<MixedLearningConfig> = {}) {
    this.config = {
      wordWeight: 0.4,
      conceptWeight: 0.4,
      techWeight: 0.2,
      difficultyDistribution: {
        beginner: 0.3,
        intermediate: 0.4,
        advanced: 0.2,
        expert: 0.1
      },
      masteryThresholds: {
        new: 0,
        learning: 0.3,
        reviewing: 0.6,
        mastered: 0.8,
        expert: 0.95
      },
      interleavingFactor: 0.7,
      spacedRepetitionFactor: 0.6,
      ...config
    };
  }

  generateMixedLearningQueue(
    words: Word[],
    concepts: Concept[],
    techContent: GermanTechIntegration[],
    wordProgress: Record<string, any>,
    conceptProgress: Record<string, any>,
    sessionHistory: StudySession[] = []
  ): LearningItem[] {
    const learningItems: LearningItem[] = [];
    
    // Convert all content to learning items with progress data
    const wordItems = this.convertWordsToLearningItems(words, wordProgress);
    const conceptItems = this.convertConceptsToLearningItems(concepts, conceptProgress);
    const techItems = this.convertTechToLearningItems(techContent);
    
    // Apply spaced repetition algorithm
    const spacedItems = this.applySpacedRepetition([...wordItems, ...conceptItems]);
    
    // Apply interleaving algorithm
    const interleavedItems = this.applyInterleaving([
      ...spacedItems.filter(item => item.type === 'word'),
      ...spacedItems.filter(item => item.type === 'concept'),
      ...techItems
    ]);
    
    // Apply difficulty balancing
    const balancedItems = this.balanceDifficulty(interleavedItems);
    
    // Apply mastery-based prioritization
    const prioritizedItems = this.prioritizeByMastery(balancedItems);
    
    // Apply session history adaptation
    const adaptedItems = this.adaptToSessionHistory(prioritizedItems, sessionHistory);
    
    return adaptedItems.slice(0, 20); // Return top 20 items
  }

  private convertWordsToLearningItems(
    words: Word[],
    progress: Record<string, any>
  ): LearningItem[] {
    return words.map(word => {
      const wordProgress = progress[word.id] || {
        reviewCount: 0,
        correctCount: 0,
        lastReviewed: null,
        nextReview: null,
        masteryLevel: 'new'
      };
      
      return {
        id: word.id,
        type: 'word',
        item: word,
        difficulty: word.difficulty,
        mastery: wordProgress.masteryLevel || 'new',
        priority: this.calculateWordPriority(word, wordProgress),
        lastStudied: wordProgress.lastReviewed ? new Date(wordProgress.lastReviewed) : undefined,
        nextReview: wordProgress.nextReview ? new Date(wordProgress.nextReview) : undefined,
        reviewCount: wordProgress.reviewCount || 0,
        correctCount: wordProgress.correctCount || 0
      };
    });
  }

  private convertConceptsToLearningItems(
    concepts: Concept[],
    progress: Record<string, any>
  ): LearningItem[] {
    return concepts.map(concept => {
      const conceptProgress = progress[concept.id] || {
        reviewCount: 0,
        understandingLevel: 0,
        lastReviewed: null,
        nextReview: null
      };
      
      const mastery = this.calculateConceptMastery(conceptProgress);
      
      return {
        id: concept.id,
        type: 'concept',
        item: concept,
        difficulty: concept.difficulty,
        mastery,
        priority: this.calculateConceptPriority(concept, conceptProgress),
        lastStudied: conceptProgress.lastReviewed ? new Date(conceptProgress.lastReviewed) : undefined,
        nextReview: conceptProgress.nextReview ? new Date(conceptProgress.nextReview) : undefined,
        reviewCount: conceptProgress.reviewCount || 0,
        correctCount: Math.round((conceptProgress.understandingLevel || 0) * (conceptProgress.reviewCount || 0) / 100)
      };
    });
  }

  private convertTechToLearningItems(
    techContent: GermanTechIntegration[]
  ): LearningItem[] {
    return techContent.map(tech => ({
      id: tech.id,
      type: 'tech',
      item: tech,
      difficulty: tech.difficulty,
      mastery: 'new',
      priority: this.calculateTechPriority(tech),
      reviewCount: 0,
      correctCount: 0
    }));
  }

  private applySpacedRepetition(items: LearningItem[]): LearningItem[] {
    const now = new Date();
    
    return items.map(item => {
      if (!item.nextReview) {
        item.priority += 10; // New items get priority boost
        return item;
      }
      
      const daysUntilReview = (item.nextReview.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
      
      if (daysUntilReview <= 0) {
        item.priority += 20; // Overdue items get high priority
      } else if (daysUntilReview <= 1) {
        item.priority += 15; // Due soon
      } else if (daysUntilReview <= 3) {
        item.priority += 10; // Due within 3 days
      }
      
      return item;
    });
  }

  private applyInterleaving(items: LearningItem[]): LearningItem[] {
    const interleaved: LearningItem[] = [];
    const typeGroups = {
      word: items.filter(item => item.type === 'word'),
      concept: items.filter(item => item.type === 'concept'),
      tech: items.filter(item => item.type === 'tech')
    };
    
    // Sort each group by priority
    Object.values(typeGroups).forEach(group => {
      group.sort((a, b) => b.priority - a.priority);
    });
    
    // Interleave based on weights
    const weights = {
      word: this.config.wordWeight,
      concept: this.config.conceptWeight,
      tech: this.config.techWeight
    };
    
    let totalItems = items.length;
    
    while (totalItems > 0) {
      const rand = Math.random();
      let cumulative = 0;
      
      for (const [type, weight] of Object.entries(weights)) {
        cumulative += weight;
        if (rand <= cumulative && typeGroups[type as keyof typeof typeGroups].length > 0) {
          interleaved.push(typeGroups[type as keyof typeof typeGroups].shift()!);
          totalItems--;
          break;
        }
      }
    }
    
    return interleaved;
  }

  private balanceDifficulty(items: LearningItem[]): LearningItem[] {
    const difficultyGroups = {
      beginner: items.filter(item => item.difficulty === 'beginner'),
      intermediate: items.filter(item => item.difficulty === 'intermediate'),
      advanced: items.filter(item => item.difficulty === 'advanced'),
      expert: items.filter(item => item.difficulty === 'expert')
    };
    
    const balanced: LearningItem[] = [];
    const targetDistribution = this.config.difficultyDistribution;
    
    // Sort each difficulty group by priority
    Object.values(difficultyGroups).forEach(group => {
      group.sort((a, b) => b.priority - a.priority);
    });
    
    // Distribute items according to target distribution
    const totalItems = items.length;
    
    Object.entries(targetDistribution).forEach(([difficulty, percentage]) => {
      const count = Math.round(totalItems * percentage);
      const group = difficultyGroups[difficulty as keyof typeof difficultyGroups];
      balanced.push(...group.slice(0, count));
    });
    
    return balanced;
  }

  private prioritizeByMastery(items: LearningItem[]): LearningItem[] {
    return items.map(item => {
      const masteryBoost = this.getMasteryPriorityBoost(item.mastery);
      item.priority += masteryBoost;
      return item;
    }).sort((a, b) => b.priority - a.priority);
  }

  private adaptToSessionHistory(items: LearningItem[], sessionHistory: StudySession[]): LearningItem[] {
    if (sessionHistory.length === 0) return items;
    
    const recentSessions = sessionHistory.slice(-5); // Last 5 sessions
    const sessionPatterns = this.analyzeSessionPatterns(recentSessions);
    
    return items.map(item => {
      // Reduce priority for recently studied items
      if (item.lastStudied) {
        const hoursSinceStudied = (Date.now() - item.lastStudied.getTime()) / (1000 * 60 * 60);
        if (hoursSinceStudied < 24) {
          item.priority -= 10;
        }
      }
      
      // Adjust based on session patterns
      if (sessionPatterns.preferredTypes.includes(item.type)) {
        item.priority += 5;
      }
      
      if (sessionPatterns.strugglingDifficulties.includes(item.difficulty)) {
        item.priority += 15; // Focus on struggling areas
      }
      
      return item;
    });
  }

  private calculateWordPriority(word: Word, progress: any): number {
    let priority = 50; // Base priority
    
    // Frequency-based priority (common words get higher priority)
    priority += word.frequency * 20;
    
    // Progress-based adjustments
    if (progress.masteryLevel === 'new') {
      priority += 30;
    } else if (progress.masteryLevel === 'learning') {
      priority += 20;
    } else if (progress.masteryLevel === 'reviewing') {
      priority += 10;
    } else if (progress.masteryLevel === 'mastered') {
      priority -= 20;
    }
    
    // Accuracy-based adjustments
    if (progress.reviewCount > 0) {
      const accuracy = progress.correctCount / progress.reviewCount;
      if (accuracy < 0.5) {
        priority += 25; // Struggling words get priority
      } else if (accuracy > 0.9) {
        priority -= 15; // Well-known words get lower priority
      }
    }
    
    return Math.max(0, Math.min(100, priority));
  }

  private calculateConceptPriority(concept: Concept, progress: any): number {
    let priority = 60; // Base priority (concepts are important)
    
    // Understanding level
    const understanding = progress.understandingLevel || 0;
    priority += (100 - understanding) * 0.4; // Lower understanding = higher priority
    
    // Review count factor
    if (progress.reviewCount < 3) {
      priority += 20; // New concepts get priority
    }
    
    return Math.max(0, Math.min(100, priority));
  }

  private calculateTechPriority(tech: GermanTechIntegration): number {
    let priority = 70; // Base priority (tech content is valuable)
    
    // Category-based priority
    switch (tech.category) {
      case 'programming':
      case 'web_development':
        priority += 15;
        break;
      case 'ai_ml':
      case 'cybersecurity':
        priority += 20;
        break;
      default:
        priority += 10;
    }
    
    return Math.max(0, Math.min(100, priority));
  }

  private calculateConceptMastery(progress: any): MasteryLevel {
    const understanding = progress.understandingLevel || 0;
    const reviewCount = progress.reviewCount || 0;
    
    if (understanding >= 95 && reviewCount >= 10) return 'expert';
    if (understanding >= 80 && reviewCount >= 5) return 'mastered';
    if (understanding >= 60 && reviewCount >= 3) return 'reviewing';
    if (understanding >= 30 || reviewCount >= 1) return 'learning';
    return 'new';
  }

  private getMasteryPriorityBoost(mastery: MasteryLevel): number {
    switch (mastery) {
      case 'new': return 30;
      case 'learning': return 20;
      case 'reviewing': return 10;
      case 'mastered': return -10;
      case 'expert': return -20;
      default: return 0;
    }
  }

  private analyzeSessionPatterns(sessions: StudySession[]): {
    preferredTypes: string[];
    strugglingDifficulties: string[];
    averageAccuracy: number;
  } {
    const typeCounts: Record<string, number> = {};
    const difficultyAccuracy: Record<string, number[]> = {};
    
    sessions.forEach(session => {
      // Count session types
      typeCounts[session.mode] = (typeCounts[session.mode] || 0) + 1;
      
      // Track accuracy by difficulty
      if (!difficultyAccuracy[session.mode]) {
        difficultyAccuracy[session.mode] = [];
      }
      difficultyAccuracy[session.mode].push(session.accuracy);
    });
    
    // Find preferred types (most common)
    const preferredTypes = Object.entries(typeCounts)
      .sort(([,a], [,b]) => b - a)
      .slice(0, 2)
      .map(([type]) => type);
    
    // Find struggling difficulties (lowest accuracy)
    const strugglingDifficulties = Object.entries(difficultyAccuracy)
      .map(([difficulty, accuracies]) => ({
        difficulty,
        avgAccuracy: accuracies.reduce((sum, acc) => sum + acc, 0) / accuracies.length
      }))
      .sort((a, b) => a.avgAccuracy - b.avgAccuracy)
      .slice(0, 2)
      .map(({difficulty}) => difficulty);
    
    const averageAccuracy = sessions.reduce((sum, session) => sum + session.accuracy, 0) / sessions.length;
    
    return {
      preferredTypes,
      strugglingDifficulties,
      averageAccuracy
    };
  }

  // Spaced repetition calculation
  calculateNextReviewTime(reviewCount: number, accuracy: number, difficulty: DifficultyLevel): Date {
    const now = new Date();
    let days = 1;
    
    // Base interval calculation
    if (reviewCount === 0) {
      days = 1;
    } else if (reviewCount === 1) {
      days = 3;
    } else if (reviewCount === 2) {
      days = 7;
    } else if (reviewCount === 3) {
      days = 14;
    } else if (reviewCount === 4) {
      days = 30;
    } else {
      days = Math.min(365, Math.pow(2, reviewCount - 3) * 30);
    }
    
    // Adjust based on accuracy
    if (accuracy < 0.5) {
      days = Math.max(1, days * 0.5); // Reduce interval for poor performance
    } else if (accuracy > 0.9) {
      days = days * 1.5; // Increase interval for good performance
    }
    
    // Adjust based on difficulty
    switch (difficulty) {
      case 'beginner':
        days = days * 1.2;
        break;
      case 'intermediate':
        days = days * 1.0;
        break;
      case 'advanced':
        days = days * 0.8;
        break;
      case 'expert':
        days = days * 0.6;
        break;
    }
    
    return new Date(now.getTime() + days * 24 * 60 * 60 * 1000);
  }
}