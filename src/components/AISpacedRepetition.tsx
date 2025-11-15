import { useState, useEffect } from 'react';
import { Brain, TrendingUp, Clock, Target, Zap, Calendar, BarChart3 } from 'lucide-react';
import { AISpacedRepetitionData, LearningPattern, MemoryStrength } from '@/types';

interface AISpacedRepetitionProps {
  userId: string;
  onPatternUpdate: (patterns: LearningPattern[]) => void;
}

export default function AISpacedRepetition({
  userId,
  onPatternUpdate
}: AISpacedRepetitionProps) {
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [aiData, setAiData] = useState<AISpacedRepetitionData | null>(null);
  const [patterns, setPatterns] = useState<LearningPattern[]>([]);
  const [memoryStrengths, setMemoryStrengths] = useState<MemoryStrength[]>([]);
  const [optimizationSuggestions, setOptimizationSuggestions] = useState<string[]>([]);
  const [showDetails, setShowDetails] = useState(false);

  // Simulate AI analysis
  const analyzeLearningPatterns = async () => {
    setIsAnalyzing(true);
    
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 2000));

    // Generate mock AI analysis data
    const mockPatterns: LearningPattern[] = [
      {
        id: 'pattern_1',
        type: 'time_preference',
        description: 'Peak learning efficiency at 9-11 AM',
        confidence: 0.85,
        frequency: 'daily',
        impact: 'high'
      },
      {
        id: 'pattern_2',
        type: 'difficulty_progression',
        description: 'Better retention with gradual difficulty increase',
        confidence: 0.92,
        frequency: 'session',
        impact: 'high'
      },
      {
        id: 'pattern_3',
        type: 'review_frequency',
        description: 'Optimal review interval: 1-3-7-14 days',
        confidence: 0.78,
        frequency: 'weekly',
        impact: 'medium'
      },
      {
        id: 'pattern_4',
        type: 'learning_style',
        description: 'Visual learner with kinesthetic reinforcement',
        confidence: 0.88,
        frequency: 'constant',
        impact: 'high'
      }
    ];

    const mockMemoryStrengths: MemoryStrength[] = [
      {
        conceptId: 'concept_1',
        concept: 'German Articles (der, die, das)',
        strength: 0.85,
        lastReviewed: new Date(Date.now() - 24 * 60 * 60 * 1000), // 1 day ago
        nextReview: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000), // 3 days from now
        reviewCount: 12,
        difficulty: 'medium'
      },
      {
        conceptId: 'concept_2',
        concept: 'Verb Conjugation Patterns',
        strength: 0.65,
        lastReviewed: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), // 2 days ago
        nextReview: new Date(Date.now() + 1 * 24 * 60 * 60 * 1000), // 1 day from now
        reviewCount: 8,
        difficulty: 'hard'
      },
      {
        conceptId: 'concept_3',
        concept: 'Basic Tech Vocabulary',
        strength: 0.92,
        lastReviewed: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000), // 1 week ago
        nextReview: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000), // 2 weeks from now
        reviewCount: 25,
        difficulty: 'easy'
      },
      {
        conceptId: 'concept_4',
        concept: 'Sentence Structure Rules',
        strength: 0.45,
        lastReviewed: new Date(Date.now() - 12 * 60 * 60 * 1000), // 12 hours ago
        nextReview: new Date(Date.now() + 12 * 60 * 60 * 1000), // 12 hours from now
        reviewCount: 3,
        difficulty: 'hard'
      }
    ];

    const mockSuggestions = [
      'Focus on sentence structure - review in 12 hours',
      'Increase difficulty for tech vocabulary - ready for advanced terms',
      'Add visual aids for verb conjugation patterns',
      'Schedule morning sessions for optimal retention',
      'Reduce review frequency for mastered concepts'
    ];

    const mockAiData: AISpacedRepetitionData = {
      userId,
      totalSessions: 47,
      averageRetentionRate: 0.78,
      optimalReviewIntervals: [1, 3, 7, 14, 30],
      difficultyDistribution: {
        easy: 35,
        medium: 45,
        hard: 20
      },
      learningVelocity: 1.2, // 20% faster than baseline
      predictedRetention: 0.85,
      nextReviewRecommendation: new Date(Date.now() + 24 * 60 * 60 * 1000),
      confidenceScore: 0.89
    };

    setPatterns(mockPatterns);
    setMemoryStrengths(mockMemoryStrengths);
    setOptimizationSuggestions(mockSuggestions);
    setAiData(mockAiData);
    setIsAnalyzing(false);
    onPatternUpdate(mockPatterns);
  };

  useEffect(() => {
    analyzeLearningPatterns();
  }, [userId]);

  const getPatternColor = (impact: string) => {
    switch (impact) {
      case 'high': return 'bg-red-100 text-red-800 border-red-200';
      case 'medium': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'low': return 'bg-green-100 text-green-800 border-green-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getStrengthColor = (strength: number) => {
    if (strength >= 0.8) return 'bg-green-500';
    if (strength >= 0.6) return 'bg-yellow-500';
    if (strength >= 0.4) return 'bg-orange-500';
    return 'bg-red-500';
  };

  const getStrengthLabel = (strength: number) => {
    if (strength >= 0.8) return 'Strong';
    if (strength >= 0.6) return 'Good';
    if (strength >= 0.4) return 'Needs Review';
    return 'Weak';
  };

  const formatNextReview = (date: Date) => {
    const now = new Date();
    const diff = date.getTime() - now.getTime();
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const days = Math.floor(hours / 24);
    
    if (days > 0) return `${days} day${days > 1 ? 's' : ''}`;
    if (hours > 0) return `${hours} hour${hours > 1 ? 's' : ''}`;
    return 'Soon';
  };

  return (
    <div className="max-w-6xl mx-auto p-6">
      <div className="bg-white rounded-2xl shadow-lg p-8">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-4">
            <div className="p-3 bg-gradient-to-r from-purple-100 to-blue-100 rounded-xl">
              <Brain className="w-8 h-8 text-purple-600" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-gray-900">AI Spaced Repetition</h1>
              <p className="text-gray-600">Personalized learning optimization powered by AI</p>
            </div>
          </div>
          <button
            onClick={analyzeLearningPatterns}
            disabled={isAnalyzing}
            className="px-6 py-3 bg-gradient-to-r from-purple-600 to-blue-600 text-white rounded-xl hover:from-purple-700 hover:to-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all font-medium"
          >
            {isAnalyzing ? (
              <div className="flex items-center gap-2">
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                Analyzing...
              </div>
            ) : (
              'Re-analyze Patterns'
            )}
          </button>
        </div>

        {/* AI Overview */}
        {aiData && (
          <div className="mb-8 p-6 bg-gradient-to-r from-purple-50 to-blue-50 rounded-xl">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
              <div className="text-center">
                <div className="text-3xl font-bold text-purple-600 mb-1">
                  {(aiData.averageRetentionRate * 100).toFixed(1)}%
                </div>
                <div className="text-sm text-gray-600">Avg Retention</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-blue-600 mb-1">
                  {aiData.totalSessions}
                </div>
                <div className="text-sm text-gray-600">Total Sessions</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-green-600 mb-1">
                  +{(aiData.learningVelocity * 100 - 100).toFixed(0)}%
                </div>
                <div className="text-sm text-gray-600">Learning Speed</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-orange-600 mb-1">
                  {(aiData.confidenceScore * 100).toFixed(0)}%
                </div>
                <div className="text-sm text-gray-600">AI Confidence</div>
              </div>
            </div>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Learning Patterns */}
          <div>
            <div className="flex items-center gap-2 mb-6">
              <TrendingUp className="w-5 h-5 text-purple-600" />
              <h2 className="text-xl font-bold text-gray-900">Detected Patterns</h2>
            </div>
            
            {patterns.length > 0 ? (
              <div className="space-y-4">
                {patterns.map((pattern) => (
                  <div key={pattern.id} className="p-4 bg-gray-50 rounded-xl">
                    <div className="flex items-start justify-between mb-3">
                      <div className="flex-1">
                        <h3 className="font-medium text-gray-900 mb-1">{pattern.description}</h3>
                        <div className="flex items-center gap-2 text-sm text-gray-600">
                          <span className={`px-2 py-1 rounded-full text-xs ${getPatternColor(pattern.impact)}`}>
                            {pattern.impact} impact
                          </span>
                          <span className="text-gray-400">•</span>
                          <span>{pattern.frequency}</span>
                        </div>
                      </div>
                      <div className="flex items-center gap-1">
                        <div className={`w-2 h-2 rounded-full ${
                          pattern.confidence >= 0.8 ? 'bg-green-500' :
                          pattern.confidence >= 0.6 ? 'bg-yellow-500' : 'bg-red-500'
                        }`}></div>
                        <span className="text-sm text-gray-600">
                          {(pattern.confidence * 100).toFixed(0)}%
                        </span>
                      </div>
                    </div>
                    
                    <div className="w-full bg-gray-200 rounded-full h-1">
                      <div 
                        className="bg-gradient-to-r from-purple-500 to-blue-500 h-1 rounded-full transition-all duration-300"
                        style={{ width: `${pattern.confidence * 100}%` }}
                      ></div>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-12 bg-gray-50 rounded-xl">
                <TrendingUp className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">No patterns detected</h3>
                <p className="text-gray-600">Start learning to build your personalized patterns</p>
              </div>
            )}
          </div>

          {/* Memory Strengths */}
          <div>
            <div className="flex items-center gap-2 mb-6">
              <Target className="w-5 h-5 text-blue-600" />
              <h2 className="text-xl font-bold text-gray-900">Memory Strengths</h2>
            </div>
            
            {memoryStrengths.length > 0 ? (
              <div className="space-y-4">
                {memoryStrengths.map((memory) => (
                  <div key={memory.conceptId} className="p-4 bg-gray-50 rounded-xl">
                    <div className="flex items-center justify-between mb-3">
                      <h3 className="font-medium text-gray-900">{memory.concept}</h3>
                      <div className="flex items-center gap-2">
                        <span className="text-sm text-gray-600">
                          {getStrengthLabel(memory.strength)}
                        </span>
                        <div className={`w-3 h-3 rounded-full ${getStrengthColor(memory.strength)}`}></div>
                      </div>
                    </div>
                    
                    <div className="flex items-center gap-4 text-sm text-gray-600 mb-3">
                      <div className="flex items-center gap-1">
                        <Clock className="w-4 h-4" />
                        <span>{formatNextReview(memory.nextReview)}</span>
                      </div>
                      <div className="flex items-center gap-1">
                        <Calendar className="w-4 h-4" />
                        <span>{memory.reviewCount} reviews</span>
                      </div>
                      <span className={`px-2 py-1 rounded-full text-xs ${
                        memory.difficulty === 'easy' ? 'bg-green-100 text-green-800' :
                        memory.difficulty === 'medium' ? 'bg-yellow-100 text-yellow-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {memory.difficulty}
                      </span>
                    </div>
                    
                    <div className="w-full bg-gray-200 rounded-full h-2">
                      <div 
                        className={`h-2 rounded-full transition-all duration-300 ${getStrengthColor(memory.strength)}`}
                        style={{ width: `${memory.strength * 100}%` }}
                      ></div>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-12 bg-gray-50 rounded-xl">
                <Target className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">No memory data</h3>
                <p className="text-gray-600">Complete some learning sessions to see your memory strengths</p>
              </div>
            )}
          </div>
        </div>

        {/* AI Suggestions */}
        {optimizationSuggestions.length > 0 && (
          <div className="mt-8 p-6 bg-gradient-to-r from-green-50 to-blue-50 rounded-xl">
            <div className="flex items-center gap-3 mb-4">
              <Zap className="w-5 h-5 text-green-600" />
              <h2 className="text-xl font-bold text-gray-900">AI Optimization Suggestions</h2>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {optimizationSuggestions.map((suggestion, index) => (
                <div key={index} className="flex items-start gap-3 p-3 bg-white rounded-lg">
                  <div className="w-2 h-2 bg-green-500 rounded-full mt-2 flex-shrink-0"></div>
                  <p className="text-gray-900">{suggestion}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Advanced Analytics Toggle */}
        <div className="mt-8 text-center">
          <button
            onClick={() => setShowDetails(!showDetails)}
            className="px-6 py-3 bg-gray-100 text-gray-700 rounded-xl hover:bg-gray-200 transition-colors font-medium flex items-center gap-2 mx-auto"
          >
            <BarChart3 className="w-4 h-4" />
            {showDetails ? 'Hide' : 'Show'} Advanced Analytics
          </button>
        </div>

        {/* Advanced Details */}
        {showDetails && aiData && (
          <div className="mt-6 p-6 bg-gray-50 rounded-xl">
            <h3 className="text-lg font-bold text-gray-900 mb-4">Advanced Metrics</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Optimal Review Intervals</h4>
                <div className="flex flex-wrap gap-2">
                  {aiData.optimalReviewIntervals.map((interval, index) => (
                    <span key={index} className="px-2 py-1 bg-blue-100 text-blue-800 rounded text-sm">
                      {interval}d
                    </span>
                  ))}
                </div>
              </div>
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Difficulty Distribution</h4>
                <div className="space-y-1 text-sm">
                  <div className="flex justify-between">
                    <span>Easy:</span>
                    <span className="font-medium">{aiData.difficultyDistribution.easy}%</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Medium:</span>
                    <span className="font-medium">{aiData.difficultyDistribution.medium}%</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Hard:</span>
                    <span className="font-medium">{aiData.difficultyDistribution.hard}%</span>
                  </div>
                </div>
              </div>
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Predictions</h4>
                <div className="space-y-1 text-sm">
                  <div className="flex justify-between">
                    <span>Retention:</span>
                    <span className="font-medium">{(aiData.predictedRetention * 100).toFixed(1)}%</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Next Review:</span>
                    <span className="font-medium">{formatNextReview(aiData.nextReviewRecommendation)}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}