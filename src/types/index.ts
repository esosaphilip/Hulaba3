// Core Learning Types
export interface Word {
  id: string;
  german: string;
  english: string;
  pronunciation: string;
  difficulty: DifficultyLevel;
  category: string;
  exampleSentence: string;
  exampleTranslation: string;
  audioUrl?: string;
  imageUrl?: string;
  frequency: number; // How common the word is
  tags: string[];
  createdAt: Date;
  updatedAt: Date;
}

export interface Topic {
  id: string;
  title: string;
  description: string;
  category: TopicCategory;
  difficulty: DifficultyLevel;
  concepts: Concept[];
  relatedWords: string[]; // Word IDs
  estimatedTime: number; // minutes
  prerequisites: string[]; // Topic IDs
  tags: string[];
  createdAt: Date;
  updatedAt: Date;
}

export interface Concept {
  id: string;
  title: string;
  description: string;
  detailedExplanation: string;
  examples: ConceptExample[];
  relatedConcepts: string[]; // Concept IDs
  difficulty: DifficultyLevel;
  visualAids: VisualAid[];
  keyPoints: string[];
  commonMistakes: string[];
  createdAt: Date;
  updatedAt: Date;
}

export interface ConceptExample {
  id: string;
  german: string;
  english: string;
  explanation: string;
  type: ExampleType;
}

export interface VisualAid {
  id: string;
  type: VisualAidType;
  url: string;
  description: string;
  altText: string;
}

export interface StudySession {
  id: string;
  userId: string;
  type: SessionType;
  mode: LearningMode;
  startTime: Date;
  endTime: Date;
  duration: number; // minutes
  wordsStudied: string[]; // Word IDs
  conceptsStudied: string[]; // Concept IDs
  correctAnswers: number;
  totalAnswers: number;
  accuracy: number;
  difficultyProgress: Record<DifficultyLevel, number>;
  streak: number;
  achievements: Achievement[];
  notes: string;
  location?: Location;
  deviceInfo: DeviceInfo;
  createdAt: Date;
}

export interface UserProgress {
  id: string;
  userId: string;
  wordProgress: Record<string, WordProgress>; // Word ID -> Progress
  conceptProgress: Record<string, ConceptProgress>; // Concept ID -> Progress
  topicProgress: Record<string, TopicProgress>; // Topic ID -> Progress
  overallStats: OverallStats;
  streak: StreakInfo;
  achievements: Achievement[];
  learningPreferences: LearningPreferences;
  studyPatterns: StudyPattern[];
  goals: LearningGoal[];
  createdAt: Date;
  updatedAt: Date;
}

export interface WordProgress {
  wordId: string;
  reviewCount: number;
  correctCount: number;
  lastReviewed: Date;
  nextReview: Date;
  masteryLevel: MasteryLevel;
  difficultyRating: number; // 1-5
  confidence: number; // 0-1
  mistakes: string[];
  notes: string;
}

export interface ConceptProgress {
  conceptId: string;
  reviewCount: number;
  understandingLevel: number; // 0-100
  lastReviewed: Date;
  nextReview: Date;
  relatedWordsMastered: string[];
  quizScores: QuizScore[];
  notes: string;
}

export interface TopicProgress {
  topicId: string;
  completionPercentage: number;
  conceptsMastered: string[];
  wordsLearned: string[];
  estimatedTimeRemaining: number;
  lastStudied: Date;
  nextRecommendedStudy: Date;
}

export interface QuizScore {
  quizId: string;
  score: number;
  totalQuestions: number;
  date: Date;
  type: QuizType;
}

export interface Achievement {
  id: string;
  title: string;
  description: string;
  icon: string;
  unlockedAt: Date;
  category: AchievementCategory;
  rarity: RarityLevel;
  progress?: number;
  maxProgress?: number;
}

export interface StreakInfo {
  current: number;
  longest: number;
  lastStudyDate: Date;
  weeklyGoal: number;
  weeklyProgress: number;
}

export interface LearningPreferences {
  difficulty: DifficultyLevel;
  preferredModes: LearningMode[];
  dailyGoal: number; // minutes
  notifications: NotificationSettings;
  accessibility: AccessibilitySettings;
  interface: InterfaceSettings;
}

export interface NotificationSettings {
  enabled: boolean;
  dailyReminder: boolean;
  spacedRepetition: boolean;
  streakReminder: boolean;
  studySuggestions: boolean;
  quietHours: TimeRange;
  locationBased: boolean;
}

export interface AccessibilitySettings {
  fontSize: FontSize;
  highContrast: boolean;
  oneHandedMode: boolean;
  screenReader: boolean;
  reducedMotion: boolean;
  colorBlindMode: ColorBlindMode;
}

export interface InterfaceSettings {
  theme: Theme;
  language: string;
  showPronunciation: boolean;
  showExamples: boolean;
  autoPlayAudio: boolean;
  animationSpeed: AnimationSpeed;
}

export interface StudyPattern {
  id: string;
  dayOfWeek: number; // 0-6
  hourOfDay: number; // 0-23
  duration: number; // minutes
  mode: LearningMode;
  location?: Location;
  effectiveness: number; // 0-1
}

export interface LearningGoal {
  id: string;
  title: string;
  description: string;
  targetDate: Date;
  currentProgress: number;
  targetProgress: number;
  category: GoalCategory;
  milestones: Milestone[];
}

export interface Milestone {
  id: string;
  title: string;
  description: string;
  progressRequired: number;
  completed: boolean;
  completedAt?: Date;
}

export interface OverallStats {
  totalStudyTime: number; // minutes
  totalWordsLearned: number;
  totalConceptsMastered: number;
  averageAccuracy: number;
  longestStreak: number;
  currentStreak: number;
  weeklyStudyTime: number;
  monthlyStudyTime: number;
  level: number;
  experiencePoints: number;
  nextLevelExp: number;
}

export interface Location {
  latitude: number;
  longitude: number;
  accuracy?: number;
  timestamp: Date;
}

export interface DeviceInfo {
  platform: string;
  version: string;
  model?: string;
  screenSize: string;
}

export interface TimeRange {
  start: string; // HH:mm format
  end: string; // HH:mm format
}

// Enums
export enum DifficultyLevel {
  BEGINNER = "beginner",
  INTERMEDIATE = "intermediate",
  ADVANCED = "advanced",
  EXPERT = "expert"
}

export enum TopicCategory {
  GRAMMAR = "grammar",
  VOCABULARY = "vocabulary",
  CONVERSATION = "conversation",
  CULTURE = "culture",
  TECH = "tech",
  BUSINESS = "business",
  TRAVEL = "travel",
  DAILY_LIFE = "daily_life"
}

export enum LearningMode {
  FLASHCARDS = "flashcards",
  QUIZ = "quiz",
  LISTENING = "listening",
  SPEAKING = "speaking",
  READING = "reading",
  WRITING = "writing",
  MIXED = "mixed",
  SPACED_REPETITION = "spaced_repetition"
}

export enum SessionType {
  STUDY = "study",
  REVIEW = "review",
  QUIZ = "quiz",
  PRACTICE = "practice",
  CHALLENGE = "challenge"
}

export enum MasteryLevel {
  NEW = "new",
  LEARNING = "learning",
  REVIEWING = "reviewing",
  MASTERED = "mastered",
  EXPERT = "expert"
}

export enum ExampleType {
  SENTENCE = "sentence",
  DIALOGUE = "dialogue",
  QUESTION = "question",
  EXERCISE = "exercise"
}

export enum VisualAidType {
  IMAGE = "image",
  DIAGRAM = "diagram",
  CHART = "chart",
  VIDEO = "video",
  AUDIO = "audio",
  INFOGRAPHIC = "infographic"
}

export enum QuizType {
  MULTIPLE_CHOICE = "multiple_choice",
  FILL_BLANK = "fill_blank",
  TRANSLATION = "translation",
  LISTENING = "listening",
  SPEAKING = "speaking",
  MATCHING = "matching"
}

export enum AchievementCategory {
  STREAK = "streak",
  ACCURACY = "accuracy",
  SPEED = "speed",
  CONSISTENCY = "consistency",
  MASTERY = "mastery",
  EXPLORATION = "exploration",
  SOCIAL = "social",
  CHALLENGE = "challenge"
}

export enum RarityLevel {
  COMMON = "common",
  UNCOMMON = "uncommon",
  RARE = "rare",
  EPIC = "epic",
  LEGENDARY = "legendary"
}

export enum FontSize {
  SMALL = "small",
  MEDIUM = "medium",
  LARGE = "large",
  EXTRA_LARGE = "extra_large"
}

export enum Theme {
  LIGHT = "light",
  DARK = "dark",
  AUTO = "auto"
}

export enum ColorBlindMode {
  NONE = "none",
  PROTANOPIA = "protanopia",
  DEUTERANOPIA = "deuteranopia",
  TRITANOPIA = "tritanopia"
}

export enum AnimationSpeed {
  SLOW = "slow",
  NORMAL = "normal",
  FAST = "fast",
  NONE = "none"
}

export enum GoalCategory {
  VOCABULARY = "vocabulary",
  GRAMMAR = "grammar",
  CONVERSATION = "conversation",
  FLUENCY = "fluency",
  CERTIFICATION = "certification",
  TRAVEL = "travel",
  BUSINESS = "business",
  TECH = "tech"
}

// AI and Tech Integration Types
export interface AIPDFAnalysis {
  id: string;
  filename: string;
  content: string;
  extractedConcepts: Concept[];
  extractedWords: Word[];
  difficulty: DifficultyLevel;
  summary: string;
  keyTopics: string[];
  suggestedStudyOrder: string[];
  createdAt: Date;
}

export interface GermanTechIntegration {
  id: string;
  title: string;
  description: string;
  germanTerms: GermanTechTerm[];
  codeExamples: CodeExample[];
  documentation: DocumentationSection[];
  quizzes: TechQuiz[];
  difficulty: DifficultyLevel;
  category: TechCategory;
  createdAt: Date;
}

export interface GermanTechTerm {
  id: string;
  german: string;
  english: string;
  pronunciation: string;
  context: string;
  usage: string;
  relatedTerms: string[];
  examples: string[];
}

export interface CodeExample {
  id: string;
  title: string;
  description: string;
  code: string;
  language: string;
  germanComments: string[];
  englishComments: string[];
  explanation: string;
  difficulty: DifficultyLevel;
}

export interface DocumentationSection {
  id: string;
  title: string;
  content: string;
  germanTerms: string[];
  codeSnippets: CodeSnippet[];
  relatedSections: string[];
}

export interface CodeSnippet {
  id: string;
  code: string;
  language: string;
  germanExplanation: string;
  englishExplanation: string;
  lineNumbers: number[];
}

export interface TechQuiz {
  id: string;
  question: string;
  germanQuestion: string;
  options: string[];
  germanOptions: string[];
  correctAnswer: number;
  explanation: string;
  germanExplanation: string;
  difficulty: DifficultyLevel;
  category: TechCategory;
}

export enum TechCategory {
  PROGRAMMING = "programming",
  WEB_DEVELOPMENT = "web_development",
  DATABASES = "databases",
  ALGORITHMS = "algorithms",
  SYSTEMS = "systems",
  DEVOPS = "devops",
  AI_ML = "ai_ml",
  CYBERSECURITY = "cybersecurity"
}

// Notification and Context Types
export interface NotificationContext {
  location?: Location;
  timeOfDay: number; // 0-23
  dayOfWeek: number; // 0-6
  weather?: WeatherInfo;
  calendarEvents?: CalendarEvent[];
  transitInfo?: TransitInfo;
  deviceContext: DeviceContext;
}

export interface WeatherInfo {
  condition: WeatherCondition;
  temperature: number;
  location: string;
}

export interface CalendarEvent {
  id: string;
  title: string;
  startTime: Date;
  endTime: Date;
  location?: string;
  type: EventType;
}

export interface TransitInfo {
  mode: TransitMode;
  duration: number; // minutes
  destination: string;
  arrivalTime: Date;
}

export interface DeviceContext {
  batteryLevel: number;
  isCharging: boolean;
  isConnectedToWifi: boolean;
  screenOnTime: number;
  appUsage: AppUsage[];
}

export interface AppUsage {
  appName: string;
  usageTime: number; // minutes
  category: AppCategory;
}

export enum WeatherCondition {
  SUNNY = "sunny",
  CLOUDY = "cloudy",
  RAINY = "rainy",
  SNOWY = "snowy",
  STORMY = "stormy"
}

export enum EventType {
  WORK = "work",
  PERSONAL = "personal",
  STUDY = "study",
  COMMUTE = "commute",
  EXERCISE = "exercise",
  SOCIAL = "social"
}

export enum TransitMode {
  WALKING = "walking",
  CYCLING = "cycling",
  PUBLIC_TRANSPORT = "public_transport",
  DRIVING = "driving",
  FLIGHT = "flight"
}

export enum AppCategory {
  SOCIAL = "social",
  PRODUCTIVITY = "productivity",
  ENTERTAINMENT = "entertainment",
  EDUCATION = "education",
  NEWS = "news",
  GAMES = "games"
}

// Speaking Practice Types
export interface SpeakingExercise {
  id: string;
  type: SpeakingType;
  prompt: string;
  germanPrompt: string;
  expectedResponse: string;
  pronunciationGuide: string;
  difficulty: DifficultyLevel;
  category: TopicCategory;
  audioUrl?: string;
  visualAid?: VisualAid;
}

export interface SpeakingAttempt {
  id: string;
  exerciseId: string;
  userAudioUrl: string;
  duration: number; // seconds
  accuracy: number; // 0-1
  pronunciation: PronunciationScore;
  fluency: FluencyScore;
  vocabulary: VocabularyScore;
  grammar: GrammarScore;
  feedback: SpeakingFeedback;
  createdAt: Date;
}

export interface PronunciationScore {
  overall: number;
  phonemes: PhonemeScore[];
  stress: number;
  intonation: number;
}

export interface PhonemeScore {
  phoneme: string;
  accuracy: number;
  suggestions: string[];
}

export interface FluencyScore {
  overall: number;
  pace: number;
  pauses: number;
  flow: number;
}

export interface VocabularyScore {
  overall: number;
  wordChoice: number;
  variety: number;
  appropriateness: number;
}

export interface GrammarScore {
  overall: number;
  accuracy: number;
  complexity: number;
  wordOrder: number;
}

export interface SpeakingFeedback {
  strengths: string[];
  improvements: string[];
  specificSuggestions: string[];
  germanFeedback: string;
  englishFeedback: string;
  nextSteps: string[];
}

export enum SpeakingType {
  REPETITION = "repetition",
  DIALOGUE = "dialogue",
  DESCRIPTION = "description",
  OPINION = "opinion",
  QUESTION_ANSWER = "question_answer",
  ROLE_PLAY = "role_play"
}