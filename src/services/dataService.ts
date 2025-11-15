import {
  Word,
  Topic,
  Concept,
  StudySession,
  UserProgress,
  LearningMode,
  DifficultyLevel,
  TopicCategory,
  MasteryLevel,
  Achievement,
  GermanTechIntegration,
  SpeakingExercise,
  SpeakingType
} from '@/types';

// Mock German Words
const mockWords: Word[] = [
  {
    id: 'word_1',
    german: 'Hallo',
    english: 'Hello',
    pronunciation: 'HAH-loh',
    difficulty: DifficultyLevel.BEGINNER,
    category: 'greetings',
    exampleSentence: 'Hallo, wie geht es dir?',
    exampleTranslation: 'Hello, how are you?',
    audioUrl: '/audio/hallo.mp3',
    imageUrl: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=hello+greeting+gesture+German+cultural+context&image_size=square',
    frequency: 95,
    tags: ['greeting', 'basic', 'conversation'],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-01')
  },
  {
    id: 'word_2',
    german: 'Danke',
    english: 'Thank you',
    pronunciation: 'DAHN-keh',
    difficulty: DifficultyLevel.BEGINNER,
    category: 'politeness',
    exampleSentence: 'Danke für deine Hilfe!',
    exampleTranslation: 'Thank you for your help!',
    audioUrl: '/audio/danke.mp3',
    imageUrl: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=thank+you+gratitude+gesture+German+cultural+context&image_size=square',
    frequency: 92,
    tags: ['politeness', 'basic', 'conversation'],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-01')
  },
  {
    id: 'word_3',
    german: 'Entschuldigung',
    english: 'Excuse me / Sorry',
    pronunciation: 'ent-SHOOL-dee-goong',
    difficulty: DifficultyLevel.INTERMEDIATE,
    category: 'politeness',
    exampleSentence: 'Entschuldigung, wo ist die Toilette?',
    exampleTranslation: 'Excuse me, where is the toilet?',
    audioUrl: '/audio/entschuldigung.mp3',
    imageUrl: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=apology+excuse+me+polite+German+cultural+context&image_size=square',
    frequency: 78,
    tags: ['politeness', 'intermediate', 'conversation'],
    createdAt: new Date('2024-01-02'),
    updatedAt: new Date('2024-01-02')
  },
  {
    id: 'word_4',
    german: 'Funktion',
    english: 'Function',
    pronunciation: 'foonk-TSIOHN',
    difficulty: DifficultyLevel.ADVANCED,
    category: 'tech',
    exampleSentence: 'Diese Funktion berechnet die Summe.',
    exampleTranslation: 'This function calculates the sum.',
    audioUrl: '/audio/funktion.mp3',
    imageUrl: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=programming+function+code+German+tech+context&image_size=square',
    frequency: 45,
    tags: ['tech', 'programming', 'advanced'],
    createdAt: new Date('2024-01-03'),
    updatedAt: new Date('2024-01-03')
  },
  {
    id: 'word_5',
    german: 'Datenbank',
    english: 'Database',
    pronunciation: 'DAH-ten-bahnk',
    difficulty: DifficultyLevel.ADVANCED,
    category: 'tech',
    exampleSentence: 'Die Datenbank speichert alle Informationen.',
    exampleTranslation: 'The database stores all information.',
    audioUrl: '/audio/datenbank.mp3',
    imageUrl: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=database+server+tech+German+context&image_size=square',
    frequency: 38,
    tags: ['tech', 'database', 'advanced'],
    createdAt: new Date('2024-01-03'),
    updatedAt: new Date('2024-01-03')
  },
  {
    id: 'word_6',
    german: 'Algorithmus',
    english: 'Algorithm',
    pronunciation: 'al-go-REE-mus',
    difficulty: DifficultyLevel.EXPERT,
    category: 'tech',
    exampleSentence: 'Der Algorithmus sortiert die Liste effizient.',
    exampleTranslation: 'The algorithm sorts the list efficiently.',
    audioUrl: '/audio/algorithmus.mp3',
    imageUrl: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=algorithm+flowchart+German+tech+context&image_size=square',
    frequency: 25,
    tags: ['tech', 'algorithm', 'expert'],
    createdAt: new Date('2024-01-04'),
    updatedAt: new Date('2024-01-04')
  }
];

// Mock Topics with Concepts
const mockTopics: Topic[] = [
  {
    id: 'topic_1',
    title: 'Basic Greetings',
    description: 'Learn essential German greetings and introductions',
    category: TopicCategory.CONVERSATION,
    difficulty: DifficultyLevel.BEGINNER,
    concepts: [
      {
        id: 'concept_1',
        title: 'Formal vs Informal Greetings',
        description: 'Understanding when to use formal and informal greetings in German',
        detailedExplanation: 'German has both formal (Sie) and informal (du) forms of address. Use "Sie" with strangers, elders, and professional settings. Use "du" with friends, family, and children.',
        examples: [
          {
            id: 'example_1',
            german: 'Guten Tag, Herr Schmidt. Wie geht es Ihnen?',
            english: 'Good day, Mr. Schmidt. How are you?',
            explanation: 'Formal greeting using "Sie" form',
            type: 'sentence'
          },
          {
            id: 'example_2',
            german: 'Hallo Anna! Wie geht\'s dir?',
            english: 'Hello Anna! How are you?',
            explanation: 'Informal greeting using "du" form',
            type: 'sentence'
          }
        ],
        relatedConcepts: ['concept_2'],
        difficulty: DifficultyLevel.BEGINNER,
        visualAids: [
          {
            id: 'visual_1',
            type: 'diagram',
            url: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=German+formal+informal+greeting+diagram+flowchart&image_size=landscape_16_9',
            description: 'Formal vs Informal Greeting Decision Tree',
            altText: 'Diagram showing when to use formal and informal greetings'
          }
        ],
        keyPoints: [
          'Use "Sie" for formal situations',
          'Use "du" for informal situations',
          'When in doubt, start formal'
        ],
        commonMistakes: [
          'Using "du" with strangers',
          'Mixing formal and informal in same conversation'
        ],
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-01')
      },
      {
        id: 'concept_2',
        title: 'Time-based Greetings',
        description: 'Learn greetings for different times of day',
        detailedExplanation: 'German greetings change based on time of day. Use "Guten Morgen" (Good morning) until noon, "Guten Tag" (Good day) until 6 PM, and "Guten Abend" (Good evening) after 6 PM.',
        examples: [
          {
            id: 'example_3',
            german: 'Guten Morgen! Haben Sie gut geschlafen?',
            english: 'Good morning! Did you sleep well?',
            explanation: 'Morning greeting (before 12 PM)',
            type: 'sentence'
          },
          {
            id: 'example_4',
            german: 'Guten Abend! Wie war dein Tag?',
            english: 'Good evening! How was your day?',
            explanation: 'Evening greeting (after 6 PM)',
            type: 'sentence'
          }
        ],
        relatedConcepts: ['concept_1'],
        difficulty: DifficultyLevel.BEGINNER,
        visualAids: [
          {
            id: 'visual_2',
            type: 'chart',
            url: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=German+greetings+time+chart+morning+afternoon+evening&image_size=landscape_4_3',
            description: 'Time-based Greeting Chart',
            altText: 'Chart showing which greetings to use at different times'
          }
        ],
        keyPoints: [
          'Guten Morgen: Before 12 PM',
          'Guten Tag: 12 PM - 6 PM',
          'Guten Abend: After 6 PM'
        ],
        commonMistakes: [
          'Using "Guten Morgen" in the afternoon',
          'Using "Guten Tag" in the evening'
        ],
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-01')
      }
    ],
    relatedWords: ['word_1', 'word_2', 'word_3'],
    estimatedTime: 15,
    prerequisites: [],
    tags: ['greetings', 'conversation', 'basic'],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-01')
  },
  {
    id: 'topic_2',
    title: 'German Tech Vocabulary',
    description: 'Essential German vocabulary for technology and programming',
    category: TopicCategory.TECH,
    difficulty: DifficultyLevel.INTERMEDIATE,
    concepts: [
      {
        id: 'concept_3',
        title: 'Programming Concepts in German',
        description: 'Learn how programming concepts are expressed in German',
        detailedExplanation: 'German programming vocabulary often combines existing German words or adapts English terms. Understanding these patterns helps with technical discussions.',
        examples: [
          {
            id: 'example_5',
            german: 'Die Schleife wiederholt den Code.',
            english: 'The loop repeats the code.',
            explanation: '"Schleife" means loop in programming context',
            type: 'sentence'
          },
          {
            id: 'example_6',
            german: 'Die Funktion gibt einen Wert zurück.',
            english: 'The function returns a value.',
            explanation: '"Funktion" and "zurückgeben" for function and return',
            type: 'sentence'
          }
        ],
        relatedConcepts: ['concept_4'],
        difficulty: DifficultyLevel.INTERMEDIATE,
        visualAids: [
          {
            id: 'visual_3',
            type: 'diagram',
            url: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=German+programming+concepts+diagram+code+flowchart&image_size=landscape_16_9',
            description: 'Programming Concepts in German',
            altText: 'Diagram showing German programming terminology'
          }
        ],
        keyPoints: [
          'Many programming terms are similar to English',
          'German compound words are common',
          'Context determines meaning'
        ],
        commonMistakes: [
          'Translating technical terms literally',
          'Ignoring context-specific meanings'
        ],
        createdAt: new Date('2024-01-02'),
        updatedAt: new Date('2024-01-02')
      }
    ],
    relatedWords: ['word_4', 'word_5', 'word_6'],
    estimatedTime: 25,
    prerequisites: ['topic_1'],
    tags: ['tech', 'programming', 'vocabulary'],
    createdAt: new Date('2024-01-02'),
    updatedAt: new Date('2024-01-02')
  }
];

// Mock German Tech Integration Content
const mockGermanTechContent: GermanTechIntegration[] = [
  {
    id: 'tech_1',
    title: 'German Programming Fundamentals',
    description: 'Learn programming concepts through German terminology',
    germanTerms: [
      {
        id: 'tech_term_1',
        german: 'Funktion',
        english: 'Function',
        pronunciation: 'foonk-TSIOHN',
        context: 'Programming',
        usage: 'Used when defining or calling functions',
        relatedTerms: ['Methode', 'Prozedur'],
        examples: ['Die Funktion berechnet die Summe.', 'Diese Funktion ist sehr nützlich.']
      },
      {
        id: 'tech_term_2',
        german: 'Variable',
        english: 'Variable',
        pronunciation: 'vah-ree-AH-beh',
        context: 'Programming',
        usage: 'Used when declaring or using variables',
        relatedTerms: ['Konstante', 'Parameter'],
        examples: ['Die Variable speichert den Wert.', 'Diese Variable ist global.']
      }
    ],
    codeExamples: [
      {
        id: 'code_ex_1',
        title: 'Basic Function in German Context',
        description: 'A simple function with German comments',
        code: `def berechne_summe(a, b):
    # Diese Funktion berechnet die Summe von zwei Zahlen
    # Parameter: a und b sind die zu addierenden Zahlen
    # Rückgabe: Die Summe von a und b
    
    summe = a + b  # Addiere die beiden Zahlen
    return summe   # Gebe die Summe zurück`,
        language: 'python',
        germanComments: [
          'Diese Funktion berechnet die Summe von zwei Zahlen',
          'Addiere die beiden Zahlen',
          'Gebe die Summe zurück'
        ],
        englishComments: [
          'This function calculates the sum of two numbers',
          'Add the two numbers',
          'Return the sum'
        ],
        explanation: 'This example shows how to write German comments in code',
        difficulty: DifficultyLevel.BEGINNER
      }
    ],
    documentation: [
      {
        id: 'doc_1',
        title: 'Funktionen in der Programmierung',
        content: 'Funktionen sind grundlegende Bausteine der Programmierung. Sie ermöglichen es, Code zu strukturieren und wiederverwendbar zu machen.',
        germanTerms: ['Funktion', 'Parameter', 'Rückgabewert'],
        codeSnippets: [
          {
            id: 'snippet_1',
            code: 'function meineFunktion(parameter) {\n  return parameter * 2;\n}',
            language: 'javascript',
            germanExplanation: 'Diese Funktion verdoppelt den übergebenen Wert',
            englishExplanation: 'This function doubles the passed value',
            lineNumbers: [1, 2, 3]
          }
        ]
      }
    ],
    quizzes: [
      {
        id: 'tech_quiz_1',
        question: 'What does "Funktion" mean in programming context?',
        germanQuestion: 'Was bedeutet "Funktion" im Programmierkontext?',
        options: ['Variable', 'Function', 'Loop', 'Class'],
        germanOptions: ['Variable', 'Funktion', 'Schleife', 'Klasse'],
        correctAnswer: 1,
        explanation: 'Funktion means function in programming context',
        germanExplanation: 'Funktion bedeutet Funktion im Programmierkontext',
        difficulty: DifficultyLevel.BEGINNER,
        category: 'programming'
      }
    ],
    difficulty: DifficultyLevel.INTERMEDIATE,
    category: 'programming',
    createdAt: new Date('2024-01-05'),
    updatedAt: new Date('2024-01-05')
  }
];

// Mock Speaking Exercises
const mockSpeakingExercises: SpeakingExercise[] = [
  {
    id: 'speak_1',
    type: SpeakingType.REPETITION,
    prompt: 'Repeat the following German greeting:',
    germanPrompt: 'Wiederholen Sie die folgende deutsche Begrüßung:',
    expectedResponse: 'Guten Tag, wie geht es Ihnen?',
    pronunciationGuide: 'GOO-ten tahk, vee gate ess EE-nen',
    difficulty: DifficultyLevel.BEGINNER,
    category: TopicCategory.CONVERSATION,
    audioUrl: '/audio/greeting_formal.mp3',
    visualAid: {
      id: 'visual_speak_1',
      type: 'image',
      url: 'https://trae-api-us.mchost.guru/api/ide/v1/text_to_image?prompt=formal+greeting+German+business+handshake&image_size=square',
      description: 'Formal greeting scenario',
      altText: 'Two people shaking hands in a formal setting'
    }
  },
  {
    id: 'speak_2',
    type: SpeakingType.DIALOGUE,
    prompt: 'Practice this conversation about programming:',
    germanPrompt: 'Üben Sie dieses Gespräch über Programmierung:',
    expectedResponse: 'Ich lerne Python. Es ist eine gute Programmiersprache.',
    pronunciationGuide: 'ikh LER-neh PEE-thohn. ess ist-eh GOO-te proh-grah-MEER-shprah-kheh',
    difficulty: DifficultyLevel.INTERMEDIATE,
    category: TopicCategory.TECH,
    audioUrl: '/audio/programming_dialogue.mp3'
  }
];

// Mock User Progress
const mockUserProgress: UserProgress = {
  id: 'progress_1',
  userId: 'user_1',
  wordProgress: {
    'word_1': {
      wordId: 'word_1',
      reviewCount: 5,
      correctCount: 4,
      lastReviewed: new Date('2024-01-15'),
      nextReview: new Date('2024-01-18'),
      masteryLevel: MasteryLevel.REVIEWING,
      difficultyRating: 2,
      confidence: 0.8,
      mistakes: ['pronunciation'],
      notes: 'Focus on pronunciation'
    },
    'word_2': {
      wordId: 'word_2',
      reviewCount: 3,
      correctCount: 3,
      lastReviewed: new Date('2024-01-14'),
      nextReview: new Date('2024-01-17'),
      masteryLevel: MasteryLevel.LEARNING,
      difficultyRating: 1,
      confidence: 0.9,
      mistakes: [],
      notes: 'Easy to remember'
    }
  },
  conceptProgress: {
    'concept_1': {
      conceptId: 'concept_1',
      reviewCount: 2,
      understandingLevel: 75,
      lastReviewed: new Date('2024-01-13'),
      nextReview: new Date('2024-01-16'),
      relatedWordsMastered: ['word_1', 'word_2'],
      quizScores: [
        {
          quizId: 'quiz_1',
          score: 8,
          totalQuestions: 10,
          date: new Date('2024-01-13'),
          type: 'multiple_choice'
        }
      ],
      notes: 'Good understanding of formal vs informal'
    }
  },
  topicProgress: {
    'topic_1': {
      topicId: 'topic_1',
      completionPercentage: 60,
      conceptsMastered: ['concept_1'],
      wordsLearned: ['word_1', 'word_2'],
      estimatedTimeRemaining: 6,
      lastStudied: new Date('2024-01-15'),
      nextRecommendedStudy: new Date('2024-01-16')
    }
  },
  overallStats: {
    totalStudyTime: 120,
    totalWordsLearned: 5,
    totalConceptsMastered: 1,
    averageAccuracy: 0.75,
    longestStreak: 3,
    currentStreak: 2,
    weeklyStudyTime: 45,
    monthlyStudyTime: 120,
    level: 2,
    experiencePoints: 250,
    nextLevelExp: 500
  },
  streak: {
    current: 2,
    longest: 3,
    lastStudyDate: new Date('2024-01-15'),
    weeklyGoal: 60,
    weeklyProgress: 45
  },
  achievements: [
    {
      id: 'achievement_1',
      title: 'First Steps',
      description: 'Complete your first lesson',
      icon: '🎯',
      unlockedAt: new Date('2024-01-10'),
      category: 'mastery',
      rarity: 'common'
    },
    {
      id: 'achievement_2',
      title: 'Streak Starter',
      description: 'Maintain a 2-day study streak',
      icon: '🔥',
      unlockedAt: new Date('2024-01-12'),
      category: 'streak',
      rarity: 'common'
    }
  ],
  learningPreferences: {
    difficulty: DifficultyLevel.BEGINNER,
    preferredModes: [LearningMode.FLASHCARDS, LearningMode.MIXED],
    dailyGoal: 30,
    notifications: {
      enabled: true,
      dailyReminder: true,
      spacedRepetition: true,
      streakReminder: true,
      studySuggestions: true,
      quietHours: { start: '22:00', end: '08:00' },
      locationBased: false
    },
    accessibility: {
      fontSize: 'medium',
      highContrast: false,
      oneHandedMode: false,
      screenReader: false,
      reducedMotion: false,
      colorBlindMode: 'none'
    },
    interface: {
      theme: 'light',
      language: 'en',
      showPronunciation: true,
      showExamples: true,
      autoPlayAudio: true,
      animationSpeed: 'normal'
    }
  },
  studyPatterns: [
    {
      id: 'pattern_1',
      dayOfWeek: 1, // Monday
      hourOfDay: 18, // 6 PM
      duration: 15,
      mode: LearningMode.FLASHCARDS,
      effectiveness: 0.8
    }
  ],
  goals: [
    {
      id: 'goal_1',
      title: 'Learn 50 German Words',
      description: 'Master 50 basic German vocabulary words',
      targetDate: new Date('2024-02-01'),
      currentProgress: 5,
      targetProgress: 50,
      category: 'vocabulary',
      milestones: [
        {
          id: 'milestone_1',
          title: 'First 10 Words',
          description: 'Learn your first 10 German words',
          progressRequired: 10,
          completed: true,
          completedAt: new Date('2024-01-12')
        }
      ]
    }
  ],
  createdAt: new Date('2024-01-01'),
  updatedAt: new Date('2024-01-15')
};

class DataService {
  private words: Word[] = mockWords;
  private topics: Topic[] = mockTopics;
  private userProgress: UserProgress = mockUserProgress;
  private germanTechContent: GermanTechIntegration[] = mockGermanTechContent;
  private speakingExercises: SpeakingExercise[] = mockSpeakingExercises;

  // Word methods
  async getWords(): Promise<Word[]> {
    return Promise.resolve(this.words);
  }

  async getWordsByDifficulty(difficulty: DifficultyLevel): Promise<Word[]> {
    return Promise.resolve(this.words.filter(word => word.difficulty === difficulty));
  }

  async getWordsByCategory(category: string): Promise<Word[]> {
    return Promise.resolve(this.words.filter(word => word.category === category));
  }

  async searchWords(query: string): Promise<Word[]> {
    const lowercaseQuery = query.toLowerCase();
    return Promise.resolve(
      this.words.filter(
        word =>
          word.german.toLowerCase().includes(lowercaseQuery) ||
          word.english.toLowerCase().includes(lowercaseQuery) ||
          word.tags.some(tag => tag.toLowerCase().includes(lowercaseQuery))
      )
    );
  }

  // Topic methods
  async getTopics(): Promise<Topic[]> {
    return Promise.resolve(this.topics);
  }

  async getTopicsByCategory(category: TopicCategory): Promise<Topic[]> {
    return Promise.resolve(this.topics.filter(topic => topic.category === category));
  }

  async getTopicsByDifficulty(difficulty: DifficultyLevel): Promise<Topic[]> {
    return Promise.resolve(this.topics.filter(topic => topic.difficulty === difficulty));
  }

  async getTopicById(id: string): Promise<Topic | null> {
    return Promise.resolve(this.topics.find(topic => topic.id === id) || null);
  }

  // User Progress methods
  async getUserProgress(userId: string): Promise<UserProgress | null> {
    return Promise.resolve(this.userProgress);
  }

  async updateWordProgress(wordId: string, progress: any): Promise<void> {
    if (this.userProgress.wordProgress[wordId]) {
      Object.assign(this.userProgress.wordProgress[wordId], progress);
    } else {
      this.userProgress.wordProgress[wordId] = progress;
    }
    this.userProgress.updatedAt = new Date();
    return Promise.resolve();
  }

  async updateConceptProgress(conceptId: string, progress: any): Promise<void> {
    if (this.userProgress.conceptProgress[conceptId]) {
      Object.assign(this.userProgress.conceptProgress[conceptId], progress);
    } else {
      this.userProgress.conceptProgress[conceptId] = progress;
    }
    this.userProgress.updatedAt = new Date();
    return Promise.resolve();
  }

  async addStudySession(session: StudySession): Promise<void> {
    // Update overall stats
    this.userProgress.overallStats.totalStudyTime += session.duration;
    this.userProgress.overallStats.totalWordsLearned += session.wordsStudied.length;
    this.userProgress.overallStats.totalConceptsMastered += session.conceptsStudied.length;
    
    // Update streak
    const today = new Date().toDateString();
    const lastStudy = this.userProgress.streak.lastStudyDate.toDateString();
    
    if (today !== lastStudy) {
      const yesterday = new Date();
      yesterday.setDate(yesterday.getDate() - 1);
      
      if (yesterday.toDateString() === lastStudy) {
        this.userProgress.streak.current += 1;
        this.userProgress.overallStats.currentStreak += 1;
      } else {
        this.userProgress.streak.current = 1;
        this.userProgress.overallStats.currentStreak = 1;
      }
      
      this.userProgress.streak.lastStudyDate = new Date();
      
      if (this.userProgress.streak.current > this.userProgress.streak.longest) {
        this.userProgress.streak.longest = this.userProgress.streak.current;
        this.userProgress.overallStats.longestStreak = this.userProgress.streak.current;
      }
    }
    
    // Update weekly progress
    this.userProgress.streak.weeklyProgress += session.duration;
    
    // Add achievements
    session.achievements.forEach(achievement => {
      if (!this.userProgress.achievements.find(a => a.id === achievement.id)) {
        this.userProgress.achievements.push(achievement);
      }
    });
    
    this.userProgress.updatedAt = new Date();
    return Promise.resolve();
  }

  // German Tech Content methods
  async getGermanTechContent(): Promise<GermanTechIntegration[]> {
    return Promise.resolve(this.germanTechContent);
  }

  async getGermanTechContentByDifficulty(difficulty: DifficultyLevel): Promise<GermanTechIntegration[]> {
    return Promise.resolve(this.germanTechContent.filter(content => content.difficulty === difficulty));
  }

  async getGermanTechContentByCategory(category: string): Promise<GermanTechIntegration[]> {
    return Promise.resolve(this.germanTechContent.filter(content => content.category === category));
  }

  // Speaking Exercise methods
  async getSpeakingExercises(): Promise<SpeakingExercise[]> {
    return Promise.resolve(this.speakingExercises);
  }

  async getSpeakingExercisesByDifficulty(difficulty: DifficultyLevel): Promise<SpeakingExercise[]> {
    return Promise.resolve(this.speakingExercises.filter(exercise => exercise.difficulty === difficulty));
  }

  async getSpeakingExercisesByType(type: SpeakingType): Promise<SpeakingExercise[]> {
    return Promise.resolve(this.speakingExercises.filter(exercise => exercise.type === type));
  }

  // Utility methods
  async getDailyGoalProgress(): Promise<{ current: number; target: number; percentage: number }> {
    const target = this.userProgress.learningPreferences.dailyGoal;
    const current = this.userProgress.streak.weeklyProgress;
    const today = new Date().getDay();
    const weeklyTarget = (target * 7) / 7; // Simplified daily calculation
    
    return Promise.resolve({
      current: Math.min(current, weeklyTarget),
      target: weeklyTarget,
      percentage: Math.min((current / weeklyTarget) * 100, 100)
    });
  }

  async getStreakInfo(): Promise<{ current: number; longest: number; isActive: boolean }> {
    const today = new Date().toDateString();
    const lastStudy = this.userProgress.streak.lastStudyDate.toDateString();
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    
    const isActive = today === lastStudy || yesterday.toDateString() === lastStudy;
    
    return Promise.resolve({
      current: this.userProgress.streak.current,
      longest: this.userProgress.streak.longest,
      isActive
    });
  }

  async getRecommendations(): Promise<{
    words: Word[];
    concepts: Concept[];
    topics: Topic[];
    reason: string;
  }> {
    // Simple recommendation logic based on progress
    const recommendations = {
      words: [] as Word[],
      concepts: [] as Concept[],
      topics: [] as Topic[],
      reason: ''
    };
    
    // Find words that need review
    const wordsNeedingReview = Object.entries(this.userProgress.wordProgress)
      .filter(([wordId, progress]) => {
        const nextReview = new Date(progress.nextReview);
        return nextReview <= new Date();
      })
      .map(([wordId]) => this.words.find(w => w.id === wordId))
      .filter(Boolean) as Word[];
    
    recommendations.words = wordsNeedingReview.slice(0, 3);
    
    // Find concepts with low understanding
    const conceptsNeedingReview = Object.entries(this.userProgress.conceptProgress)
      .filter(([conceptId, progress]) => progress.understandingLevel < 70)
      .map(([conceptId]) => {
        for (const topic of this.topics) {
          const concept = topic.concepts.find(c => c.id === conceptId);
          if (concept) return concept;
        }
        return null;
      })
      .filter(Boolean) as Concept[];
    
    recommendations.concepts = conceptsNeedingReview.slice(0, 2);
    
    // Find incomplete topics
    const incompleteTopics = this.topics.filter(topic => {
      const progress = this.userProgress.topicProgress[topic.id];
      return !progress || progress.completionPercentage < 100;
    });
    
    recommendations.topics = incompleteTopics.slice(0, 1);
    
    if (wordsNeedingReview.length > 0) {
      recommendations.reason = 'You have words and concepts ready for review based on spaced repetition.';
    } else if (conceptsNeedingReview.length > 0) {
      recommendations.reason = 'Some concepts need more practice to improve understanding.';
    } else {
      recommendations.reason = 'Continue with new topics to expand your German knowledge.';
    }
    
    return Promise.resolve(recommendations);
  }
}

export const dataService = new DataService();