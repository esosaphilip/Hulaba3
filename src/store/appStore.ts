import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import {
  Word,
  Topic,
  Concept,
  StudySession,
  UserProgress,
  LearningMode,
  DifficultyLevel,
  TopicCategory,
  NotificationContext,
  GermanTechIntegration,
  SpeakingExercise,
  SpeakingAttempt
} from '@/types';

interface AppState {
  // User data
  userId: string | null;
  userProgress: UserProgress | null;
  
  // Learning content
  words: Word[];
  topics: Topic[];
  concepts: Concept[];
  germanTechContent: GermanTechIntegration[];
  speakingExercises: SpeakingExercise[];
  
  // Current session
  currentSession: StudySession | null;
  currentMode: LearningMode;
  currentDifficulty: DifficultyLevel;
  currentTopic: Topic | null;
  
  // Learning state
  studyQueue: (Word | Concept)[];
  reviewQueue: (Word | Concept)[];
  mixedLearningItems: (Word | Concept | GermanTechIntegration)[];
  speakingAttempts: SpeakingAttempt[];
  
  // UI state
  isLoading: boolean;
  error: string | null;
  notificationContext: NotificationContext;
  
  // Actions
  setUserId: (userId: string) => void;
  setUserProgress: (progress: UserProgress) => void;
  setWords: (words: Word[]) => void;
  setTopics: (topics: Topic[]) => void;
  setConcepts: (concepts: Concept[]) => void;
  setGermanTechContent: (content: GermanTechIntegration[]) => void;
  setSpeakingExercises: (exercises: SpeakingExercise[]) => void;
  
  // Learning actions
  startSession: (mode: LearningMode, difficulty: DifficultyLevel) => void;
  endSession: () => void;
  addToStudyQueue: (items: (Word | Concept)[]) => void;
  addToReviewQueue: (items: (Word | Concept)[]) => void;
  updateWordProgress: (wordId: string, progress: Partial<UserProgress['wordProgress'][string]>) => void;
  updateConceptProgress: (conceptId: string, progress: Partial<UserProgress['conceptProgress'][string]>) => void;
  addSpeakingAttempt: (attempt: SpeakingAttempt) => void;
  
  // Mixed learning
  generateMixedLearningQueue: () => void;
  getNextMixedItem: () => Word | Concept | GermanTechIntegration | null;
  
  // UI actions
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setNotificationContext: (context: NotificationContext) => void;
  
  // Speaking practice
  getNextSpeakingExercise: (difficulty?: DifficultyLevel) => SpeakingExercise | null;
  getSpeakingExercisesByType: (type: SpeakingExercise['type']) => SpeakingExercise[];
}

const initialNotificationContext: NotificationContext = {
  timeOfDay: new Date().getHours(),
  dayOfWeek: new Date().getDay(),
  deviceContext: {
    batteryLevel: 100,
    isCharging: false,
    isConnectedToWifi: true,
    screenOnTime: 0,
    appUsage: []
  }
};

export const useAppStore = create<AppState>()(
  immer((set, get) => ({
    // Initial state
    userId: null,
    userProgress: null,
    words: [],
    topics: [],
    concepts: [],
    germanTechContent: [],
    speakingExercises: [],
    currentSession: null,
    currentMode: LearningMode.MIXED,
    currentDifficulty: DifficultyLevel.BEGINNER,
    currentTopic: null,
    studyQueue: [],
    reviewQueue: [],
    mixedLearningItems: [],
    speakingAttempts: [],
    isLoading: false,
    error: null,
    notificationContext: initialNotificationContext,

    // Actions
    setUserId: (userId) => set({ userId }),
    
    setUserProgress: (progress) => set({ userProgress: progress }),
    
    setWords: (words) => set({ words }),
    
    setTopics: (topics) => set({ topics }),
    
    setConcepts: (concepts) => set({ concepts }),
    
    setGermanTechContent: (content) => set({ germanTechContent: content }),
    
    setSpeakingExercises: (exercises) => set({ speakingExercises: exercises }),

    // Learning actions
    startSession: (mode, difficulty) => set((state) => {
      const now = new Date();
      state.currentSession = {
        id: `session_${Date.now()}`,
        userId: state.userId || 'anonymous',
        type: 'study',
        mode,
        startTime: now,
        endTime: now,
        duration: 0,
        wordsStudied: [],
        conceptsStudied: [],
        correctAnswers: 0,
        totalAnswers: 0,
        accuracy: 0,
        difficultyProgress: {
          beginner: 0,
          intermediate: 0,
          advanced: 0,
          expert: 0
        },
        streak: 0,
        achievements: [],
        notes: '',
        deviceInfo: {
          platform: navigator.platform,
          version: navigator.userAgent,
          screenSize: `${window.innerWidth}x${window.innerHeight}`
        },
        createdAt: now
      };
      state.currentMode = mode;
      state.currentDifficulty = difficulty;
    }),

    endSession: () => set((state) => {
      if (state.currentSession) {
        const endTime = new Date();
        const duration = Math.round((endTime.getTime() - state.currentSession.startTime.getTime()) / 60000);
        state.currentSession.endTime = endTime;
        state.currentSession.duration = duration;
        state.currentSession = null;
      }
    }),

    addToStudyQueue: (items) => set((state) => {
      state.studyQueue = [...state.studyQueue, ...items];
    }),

    addToReviewQueue: (items) => set((state) => {
      state.reviewQueue = [...state.reviewQueue, ...items];
    }),

    updateWordProgress: (wordId, progress) => set((state) => {
      if (state.userProgress) {
        if (!state.userProgress.wordProgress[wordId]) {
          state.userProgress.wordProgress[wordId] = {
            wordId,
            reviewCount: 0,
            correctCount: 0,
            lastReviewed: new Date(),
            nextReview: new Date(),
            masteryLevel: 'new',
            difficultyRating: 3,
            confidence: 0,
            mistakes: [],
            notes: ''
          };
        }
        Object.assign(state.userProgress.wordProgress[wordId], progress);
      }
    }),

    updateConceptProgress: (conceptId, progress) => set((state) => {
      if (state.userProgress) {
        if (!state.userProgress.conceptProgress[conceptId]) {
          state.userProgress.conceptProgress[conceptId] = {
            conceptId,
            reviewCount: 0,
            understandingLevel: 0,
            lastReviewed: new Date(),
            nextReview: new Date(),
            relatedWordsMastered: [],
            quizScores: [],
            notes: ''
          };
        }
        Object.assign(state.userProgress.conceptProgress[conceptId], progress);
      }
    }),

    addSpeakingAttempt: (attempt) => set((state) => {
      state.speakingAttempts.push(attempt);
    }),

    // Mixed learning
    generateMixedLearningQueue: () => set((state) => {
      const { words, concepts, germanTechContent, currentDifficulty } = state;
      
      // Filter by difficulty
      const filteredWords = words.filter(w => w.difficulty === currentDifficulty);
      const filteredConcepts = concepts.filter(c => c.difficulty === currentDifficulty);
      const filteredTechContent = germanTechContent.filter(t => t.difficulty === currentDifficulty);
      
      // Create weighted mix (40% words, 40% concepts, 20% tech)
      const mixedItems: (Word | Concept | GermanTechIntegration)[] = [];
      const totalItems = 20;
      
      for (let i = 0; i < totalItems; i++) {
        const random = Math.random();
        if (random < 0.4 && filteredWords.length > 0) {
          mixedItems.push(filteredWords[Math.floor(Math.random() * filteredWords.length)]);
        } else if (random < 0.8 && filteredConcepts.length > 0) {
          mixedItems.push(filteredConcepts[Math.floor(Math.random() * filteredConcepts.length)]);
        } else if (filteredTechContent.length > 0) {
          mixedItems.push(filteredTechContent[Math.floor(Math.random() * filteredTechContent.length)]);
        }
      }
      
      state.mixedLearningItems = mixedItems;
    }),

    getNextMixedItem: () => {
      const { mixedLearningItems } = get();
      return mixedLearningItems.length > 0 ? mixedLearningItems[0] : null;
    },

    // UI actions
    setLoading: (loading) => set({ isLoading: loading }),
    
    setError: (error) => set({ error }),
    
    setNotificationContext: (context) => set({ notificationContext: context }),

    // Speaking practice
    getNextSpeakingExercise: (difficulty) => {
      const { speakingExercises, currentDifficulty } = get();
      const targetDifficulty = difficulty || currentDifficulty;
      const filtered = speakingExercises.filter(e => e.difficulty === targetDifficulty);
      return filtered.length > 0 ? filtered[Math.floor(Math.random() * filtered.length)] : null;
    },

    getSpeakingExercisesByType: (type) => {
      const { speakingExercises } = get();
      return speakingExercises.filter(e => e.type === type);
    }
  }))
);

// Selectors
export const useWords = () => useAppStore(state => state.words);
export const useTopics = () => useAppStore(state => state.topics);
export const useConcepts = () => useAppStore(state => state.concepts);
export const useUserProgress = () => useAppStore(state => state.userProgress);
export const useCurrentSession = () => useAppStore(state => state.currentSession);
export const useCurrentMode = () => useAppStore(state => state.currentMode);
export const useCurrentDifficulty = () => useAppStore(state => state.currentDifficulty);
export const useStudyQueue = () => useAppStore(state => state.studyQueue);
export const useReviewQueue = () => useAppStore(state => state.reviewQueue);
export const useMixedLearningItems = () => useAppStore(state => state.mixedLearningItems);
export const useSpeakingExercises = () => useAppStore(state => state.speakingExercises);
export const useSpeakingAttempts = () => useAppStore(state => state.speakingAttempts);
export const useGermanTechContent = () => useAppStore(state => state.germanTechContent);
export const useIsLoading = () => useAppStore(state => state.isLoading);
export const useError = () => useAppStore(state => state.error);