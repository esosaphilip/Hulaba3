import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { BookOpen, Brain, Target, Clock, Flame, Award, TrendingUp, Play, Settings, User } from 'lucide-react';
import { useAppStore } from '@/store/appStore';
import { dataService } from '@/services/dataService';
import { LearningMode, DifficultyLevel } from '@/types';

export default function LearningDashboard() {
  const navigate = useNavigate();
  const [recommendations, setRecommendations] = useState<{
    words: any[];
    concepts: any[];
    topics: any[];
    reason: string;
  }>({ words: [], concepts: [], topics: [], reason: '' });
  const [dailyProgress, setDailyProgress] = useState({ current: 0, target: 0, percentage: 0 });
  const [streakInfo, setStreakInfo] = useState({ current: 0, longest: 0, isActive: false });

  // Get data from store
  const userProgress = useAppStore(state => state.userProgress);
  const words = useAppStore(state => state.words);
  const topics = useAppStore(state => state.topics);
  const isLoading = useAppStore(state => state.isLoading);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      // Load recommendations
      const recs = await dataService.getRecommendations();
      setRecommendations(recs);

      // Load daily progress
      const progress = await dataService.getDailyGoalProgress();
      setDailyProgress(progress);

      // Load streak info
      const streak = await dataService.getStreakInfo();
      setStreakInfo(streak);
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    }
  };

  const startLearningMode = (mode: LearningMode, difficulty: DifficultyLevel) => {
    navigate(`/learn/${mode}`, { state: { difficulty } });
  };

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'beginner': return 'bg-green-100 text-green-800';
      case 'intermediate': return 'bg-yellow-100 text-yellow-800';
      case 'advanced': return 'bg-orange-100 text-orange-800';
      case 'expert': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600 mx-auto mb-4"></div>
            <p className="text-gray-600">Loading your learning dashboard...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h1 className="text-4xl font-bold text-gray-900 mb-2">Welcome back!</h1>
              <p className="text-gray-600">Ready to continue your German learning journey?</p>
            </div>
            <div className="flex items-center gap-4">
              <button className="p-2 rounded-lg bg-white shadow-sm hover:shadow-md transition-shadow">
                <Settings className="w-5 h-5 text-gray-600" />
              </button>
              <button className="p-2 rounded-lg bg-white shadow-sm hover:shadow-md transition-shadow">
                <User className="w-5 h-5 text-gray-600" />
              </button>
            </div>
          </div>

          {/* Stats Overview */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <div className="bg-white rounded-2xl shadow-lg p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-orange-100 rounded-xl">
                  <Flame className="w-6 h-6 text-orange-600" />
                </div>
                <span className="text-2xl font-bold text-gray-900">{streakInfo.current}</span>
              </div>
              <h3 className="text-sm font-medium text-gray-600 mb-1">Current Streak</h3>
              <p className="text-xs text-gray-500">Longest: {streakInfo.longest} days</p>
            </div>

            <div className="bg-white rounded-2xl shadow-lg p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-blue-100 rounded-xl">
                  <Clock className="w-6 h-6 text-blue-600" />
                </div>
                <span className="text-2xl font-bold text-gray-900">{userProgress?.overallStats.totalStudyTime || 0}</span>
              </div>
              <h3 className="text-sm font-medium text-gray-600 mb-1">Total Minutes</h3>
              <p className="text-xs text-gray-500">Study time</p>
            </div>

            <div className="bg-white rounded-2xl shadow-lg p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-green-100 rounded-xl">
                  <BookOpen className="w-6 h-6 text-green-600" />
                </div>
                <span className="text-2xl font-bold text-gray-900">{userProgress?.overallStats.totalWordsLearned || 0}</span>
              </div>
              <h3 className="text-sm font-medium text-gray-600 mb-1">Words Learned</h3>
              <p className="text-xs text-gray-500">In your vocabulary</p>
            </div>

            <div className="bg-white rounded-2xl shadow-lg p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-purple-100 rounded-xl">
                  <Award className="w-6 h-6 text-purple-600" />
                </div>
                <span className="text-2xl font-bold text-gray-900">{userProgress?.overallStats.level || 1}</span>
              </div>
              <h3 className="text-sm font-medium text-gray-600 mb-1">Current Level</h3>
              <p className="text-xs text-gray-500">{userProgress?.overallStats.experiencePoints || 0} XP</p>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Column - Learning Modes */}
          <div className="lg:col-span-2 space-y-6">
            {/* Daily Progress */}
            <div className="bg-white rounded-2xl shadow-lg p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-bold text-gray-900">Daily Progress</h2>
                <span className="text-sm text-gray-500">{dailyProgress.percentage.toFixed(0)}% complete</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-3 mb-4">
                <div 
                  className="bg-gradient-to-r from-blue-500 to-purple-500 h-3 rounded-full transition-all duration-300"
                  style={{ width: `${Math.min(dailyProgress.percentage, 100)}%` }}
                />
              </div>
              <div className="flex justify-between text-sm text-gray-600">
                <span>{dailyProgress.current} minutes studied</span>
                <span>Goal: {dailyProgress.target} minutes</span>
              </div>
            </div>

            {/* Learning Modes */}
            <div className="bg-white rounded-2xl shadow-lg p-6">
              <h2 className="text-xl font-bold text-gray-900 mb-6">Choose Your Learning Mode</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <button
                  onClick={() => startLearningMode(LearningMode.MIXED, DifficultyLevel.BEGINNER)}
                  className="p-4 border-2 border-gray-200 rounded-xl hover:border-purple-500 hover:bg-purple-50 transition-all group"
                >
                  <div className="flex items-center gap-3 mb-2">
                    <div className="p-2 bg-purple-100 rounded-lg group-hover:bg-purple-200 transition-colors">
                      <Brain className="w-5 h-5 text-purple-600" />
                    </div>
                    <div className="text-left">
                      <h3 className="font-semibold text-gray-900">Mixed Learning</h3>
                      <p className="text-sm text-gray-600">AI-powered interleaving</p>
                    </div>
                  </div>
                  <p className="text-xs text-gray-500 text-left">Our most effective mode combining words, concepts, and tech content</p>
                </button>

                <button
                  onClick={() => startLearningMode(LearningMode.FLASHCARDS, DifficultyLevel.BEGINNER)}
                  className="p-4 border-2 border-gray-200 rounded-xl hover:border-blue-500 hover:bg-blue-50 transition-all group"
                >
                  <div className="flex items-center gap-3 mb-2">
                    <div className="p-2 bg-blue-100 rounded-lg group-hover:bg-blue-200 transition-colors">
                      <BookOpen className="w-5 h-5 text-blue-600" />
                    </div>
                    <div className="text-left">
                      <h3 className="font-semibold text-gray-900">Flashcards</h3>
                      <p className="text-sm text-gray-600">Traditional learning</p>
                    </div>
                  </div>
                  <p className="text-xs text-gray-500 text-left">Classic flashcard mode with spaced repetition</p>
                </button>

                <button
                  onClick={() => startLearningMode(LearningMode.QUIZ, DifficultyLevel.INTERMEDIATE)}
                  className="p-4 border-2 border-gray-200 rounded-xl hover:border-green-500 hover:bg-green-50 transition-all group"
                >
                  <div className="flex items-center gap-3 mb-2">
                    <div className="p-2 bg-green-100 rounded-lg group-hover:bg-green-200 transition-colors">
                      <Target className="w-5 h-5 text-green-600" />
                    </div>
                    <div className="text-left">
                      <h3 className="font-semibold text-gray-900">Quiz Mode</h3>
                      <p className="text-sm text-gray-600">Test your knowledge</p>
                    </div>
                  </div>
                  <p className="text-xs text-gray-500 text-left">Challenge yourself with interactive quizzes</p>
                </button>

                <button
                  onClick={() => startLearningMode(LearningMode.SPEAKING, DifficultyLevel.ADVANCED)}
                  className="p-4 border-2 border-gray-200 rounded-xl hover:border-orange-500 hover:bg-orange-50 transition-all group"
                >
                  <div className="flex items-center gap-3 mb-2">
                    <div className="p-2 bg-orange-100 rounded-lg group-hover:bg-orange-200 transition-colors">
                      <TrendingUp className="w-5 h-5 text-orange-600" />
                    </div>
                    <div className="text-left">
                      <h3 className="font-semibold text-gray-900">Speaking Practice</h3>
                      <p className="text-sm text-gray-600">Pronunciation & fluency</p>
                    </div>
                  </div>
                  <p className="text-xs text-gray-500 text-left">Practice speaking with AI feedback</p>
                </button>
              </div>
            </div>
          </div>

          {/* Right Column - Recommendations & Quick Stats */}
          <div className="space-y-6">
            {/* AI Recommendations */}
            <div className="bg-white rounded-2xl shadow-lg p-6">
              <h2 className="text-xl font-bold text-gray-900 mb-4">AI Recommendations</h2>
              <p className="text-sm text-gray-600 mb-4">{recommendations.reason}</p>
              
              {recommendations.words.length > 0 && (
                <div className="mb-4">
                  <h3 className="font-semibold text-gray-800 mb-2">Words to Review</h3>
                  <div className="space-y-2">
                    {recommendations.words.slice(0, 3).map((word) => (
                      <div key={word.id} className="flex items-center justify-between p-2 bg-gray-50 rounded-lg">
                        <div>
                          <p className="font-medium text-gray-900">{word.german}</p>
                          <p className="text-sm text-gray-600">{word.english}</p>
                        </div>
                        <span className={`px-2 py-1 rounded text-xs font-medium ${getDifficultyColor(word.difficulty)}`}>
                          {word.difficulty}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {recommendations.concepts.length > 0 && (
                <div className="mb-4">
                  <h3 className="font-semibold text-gray-800 mb-2">Concepts to Master</h3>
                  <div className="space-y-2">
                    {recommendations.concepts.slice(0, 2).map((concept) => (
                      <div key={concept.id} className="p-2 bg-gray-50 rounded-lg">
                        <p className="font-medium text-gray-900">{concept.title}</p>
                        <p className="text-sm text-gray-600">{concept.description}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              <button
                onClick={() => startLearningMode(LearningMode.MIXED, DifficultyLevel.BEGINNER)}
                className="w-full px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors font-medium"
              >
                Start Recommended Session
              </button>
            </div>

            {/* Quick Topics */}
            <div className="bg-white rounded-2xl shadow-lg p-6">
              <h2 className="text-xl font-bold text-gray-900 mb-4">Quick Topics</h2>
              <div className="space-y-3">
                {topics.slice(0, 4).map((topic) => (
                  <div key={topic.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer">
                    <div>
                      <p className="font-medium text-gray-900">{topic.title}</p>
                      <p className="text-sm text-gray-600">{topic.estimatedTime} min</p>
                    </div>
                    <Play className="w-4 h-4 text-gray-400" />
                  </div>
                ))}
              </div>
            </div>

            {/* Achievements */}
            <div className="bg-white rounded-2xl shadow-lg p-6">
              <h2 className="text-xl font-bold text-gray-900 mb-4">Recent Achievements</h2>
              <div className="space-y-3">
                {userProgress?.achievements.slice(0, 3).map((achievement) => (
                  <div key={achievement.id} className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                    <span className="text-2xl">{achievement.icon}</span>
                    <div>
                      <p className="font-medium text-gray-900">{achievement.title}</p>
                      <p className="text-sm text-gray-600">{achievement.description}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}