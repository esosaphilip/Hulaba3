import { useState } from 'react';
import { ChevronDown, ChevronUp, Eye, EyeOff, Lightbulb, AlertTriangle, CheckCircle } from 'lucide-react';
import { Concept, MasteryLevel } from '@/types';
import { useAppStore } from '@/store/appStore';

interface ConceptCardProps {
  concept: Concept;
  onNext: () => void;
  onPrevious: () => void;
  onMarkUnderstood: (conceptId: string, understood: boolean) => void;
  isFirst: boolean;
  isLast: boolean;
  showProgress?: boolean;
}

export default function ConceptCard({
  concept,
  onNext,
  onPrevious,
  onMarkUnderstood,
  isFirst,
  isLast,
  showProgress = true
}: ConceptCardProps) {
  const [expandedSections, setExpandedSections] = useState<Record<string, boolean>>({
    examples: false,
    keyPoints: false,
    mistakes: false,
    related: false
  });
  const [isRevealed, setIsRevealed] = useState(false);
  const [understandingLevel, setUnderstandingLevel] = useState(0);

  const updateConceptProgress = useAppStore(state => state.updateConceptProgress);
  const userProgressData = useAppStore(state => state.userProgress);

  const toggleSection = (section: string) => {
    setExpandedSections(prev => ({
      ...prev,
      [section]: !prev[section]
    }));
  };

  const handleReveal = () => {
    setIsRevealed(!isRevealed);
  };

  const handleMarkUnderstood = (understood: boolean) => {
    onMarkUnderstood(concept.id, understood);
    
    // Update progress
    const newUnderstanding = understood ? 80 : 30;
    setUnderstandingLevel(newUnderstanding);
    
    updateConceptProgress(concept.id, {
      understandingLevel: newUnderstanding,
      lastReviewed: new Date(),
      reviewCount: (userProgressData?.conceptProgress[concept.id]?.reviewCount || 0) + 1
    });
  };

  const handleUnderstandingChange = (level: number) => {
    setUnderstandingLevel(level);
    updateConceptProgress(concept.id, {
      understandingLevel: level
    });
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

  const getMasteryColor = (mastery: MasteryLevel) => {
    switch (mastery) {
      case MasteryLevel.NEW: return 'bg-blue-100 text-blue-800';
      case MasteryLevel.LEARNING: return 'bg-yellow-100 text-yellow-800';
      case MasteryLevel.REVIEWING: return 'bg-orange-100 text-orange-800';
      case MasteryLevel.MASTERED: return 'bg-green-100 text-green-800';
      case MasteryLevel.EXPERT: return 'bg-purple-100 text-purple-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const currentConceptProgress = userProgressData?.conceptProgress[concept.id];
  const currentMastery = currentConceptProgress?.understandingLevel >= 80 ? MasteryLevel.MASTERED :
                        currentConceptProgress?.understandingLevel >= 60 ? MasteryLevel.REVIEWING :
                        currentConceptProgress?.understandingLevel >= 30 ? MasteryLevel.LEARNING :
                        MasteryLevel.NEW;

  return (
    <div className="max-w-3xl mx-auto p-6">
      {/* Progress Bar */}
      {showProgress && (
        <div className="mb-6">
          <div className="flex justify-between items-center mb-2">
            <span className="text-sm font-medium text-gray-700">Understanding Level</span>
            <span className="text-sm text-gray-500">
              {currentConceptProgress?.reviewCount || 0} reviews
            </span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div 
              className="bg-purple-600 h-2 rounded-full transition-all duration-300"
              style={{ width: `${understandingLevel}%` }}
            />
          </div>
          <div className="flex justify-between text-xs text-gray-500 mt-1">
            <span>0%</span>
            <span>{understandingLevel}%</span>
            <span>100%</span>
          </div>
        </div>
      )}

      {/* Main Card */}
      <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
        {/* Card Header */}
        <div className="bg-gradient-to-r from-purple-500 to-pink-600 p-6 text-white">
          <div className="flex justify-between items-start mb-4">
            <div className="flex gap-2 flex-wrap">
              <span className={`px-3 py-1 rounded-full text-xs font-medium ${getDifficultyColor(concept.difficulty)}`}>
                {concept.difficulty}
              </span>
              <span className={`px-3 py-1 rounded-full text-xs font-medium ${getMasteryColor(currentMastery)}`}>
                {currentMastery}
              </span>
            </div>
            <button
              onClick={handleReveal}
              className="p-2 rounded-full bg-white bg-opacity-20 hover:bg-opacity-30 transition-colors"
              title={isRevealed ? 'Hide details' : 'Show details'}
            >
              {isRevealed ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
            </button>
          </div>
          
          <div className="text-center">
            <h2 className="text-2xl font-bold mb-2">{concept.title}</h2>
            <p className="text-lg opacity-90">{concept.description}</p>
          </div>
        </div>

        {/* Card Content */}
        <div className="p-6 space-y-6">
          {/* Detailed Explanation */}
          <div className="bg-gray-50 rounded-lg p-4">
            <h3 className="font-semibold text-gray-800 mb-2">Detailed Explanation</h3>
            <p className="text-gray-700 leading-relaxed">{concept.detailedExplanation}</p>
          </div>

          {/* Key Points */}
          <div className="border border-gray-200 rounded-lg">
            <button
              onClick={() => toggleSection('keyPoints')}
              className="w-full flex items-center justify-between p-4 hover:bg-gray-50 transition-colors"
            >
              <div className="flex items-center gap-2">
                <Lightbulb className="w-5 h-5 text-yellow-500" />
                <span className="font-semibold text-gray-800">Key Points</span>
              </div>
              {expandedSections.keyPoints ? <ChevronUp className="w-5 h-5" /> : <ChevronDown className="w-5 h-5" />}
            </button>
            {expandedSections.keyPoints && (
              <div className="px-4 pb-4">
                <ul className="space-y-2">
                  {concept.keyPoints.map((point, index) => (
                    <li key={index} className="flex items-start gap-2">
                      <CheckCircle className="w-4 h-4 text-green-500 mt-0.5 flex-shrink-0" />
                      <span className="text-gray-700">{point}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>

          {/* Examples */}
          <div className="border border-gray-200 rounded-lg">
            <button
              onClick={() => toggleSection('examples')}
              className="w-full flex items-center justify-between p-4 hover:bg-gray-50 transition-colors"
            >
              <span className="font-semibold text-gray-800">Examples</span>
              {expandedSections.examples ? <ChevronUp className="w-5 h-5" /> : <ChevronDown className="w-5 h-5" />}
            </button>
            {expandedSections.examples && (
              <div className="px-4 pb-4 space-y-4">
                {concept.examples.map((example) => (
                  <div key={example.id} className="bg-blue-50 rounded-lg p-4">
                    <div className="mb-2">
                      <p className="text-blue-900 font-medium">🇩🇪 {example.german}</p>
                      <p className="text-blue-700">🇬🇧 {example.english}</p>
                    </div>
                    <p className="text-blue-600 text-sm italic">{example.explanation}</p>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Common Mistakes */}
          <div className="border border-gray-200 rounded-lg">
            <button
              onClick={() => toggleSection('mistakes')}
              className="w-full flex items-center justify-between p-4 hover:bg-gray-50 transition-colors"
            >
              <div className="flex items-center gap-2">
                <AlertTriangle className="w-5 h-5 text-orange-500" />
                <span className="font-semibold text-gray-800">Common Mistakes</span>
              </div>
              {expandedSections.mistakes ? <ChevronUp className="w-5 h-5" /> : <ChevronDown className="w-5 h-5" />}
            </button>
            {expandedSections.mistakes && (
              <div className="px-4 pb-4">
                <ul className="space-y-2">
                  {concept.commonMistakes.map((mistake, index) => (
                    <li key={index} className="flex items-start gap-2">
                      <AlertTriangle className="w-4 h-4 text-orange-500 mt-0.5 flex-shrink-0" />
                      <span className="text-gray-700">{mistake}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>

          {/* Visual Aids */}
          {isRevealed && concept.visualAids.length > 0 && (
            <div className="space-y-4">
              <h3 className="font-semibold text-gray-800">Visual Aids</h3>
              <div className="grid gap-4">
                {concept.visualAids.map((aid) => (
                  <div key={aid.id} className="border border-gray-200 rounded-lg p-4">
                    <img
                      src={aid.url}
                      alt={aid.altText}
                      className="w-full h-48 object-cover rounded-lg mb-3"
                    />
                    <h4 className="font-medium text-gray-800 mb-1">{aid.description}</h4>
                    <p className="text-gray-600 text-sm">{aid.altText}</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Understanding Level */}
          <div className="bg-purple-50 rounded-lg p-4">
            <h4 className="font-semibold text-purple-900 mb-3">How well do you understand this concept?</h4>
            <div className="space-y-3">
              <div className="flex justify-between text-sm text-purple-700">
                <span>Not at all</span>
                <span>Completely</span>
              </div>
              <div className="flex gap-2">
                {[0, 25, 50, 75, 100].map(level => (
                  <button
                    key={level}
                    onClick={() => handleUnderstandingChange(level)}
                    className={`flex-1 py-2 px-3 rounded-lg text-sm font-medium transition-colors ${
                      understandingLevel >= level
                        ? 'bg-purple-600 text-white'
                        : 'bg-purple-100 text-purple-700 hover:bg-purple-200'
                    }`}
                  >
                    {level}%
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex justify-between items-center mt-6">
        <button
          onClick={onPrevious}
          disabled={isFirst}
          className={`inline-flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
            isFirst
              ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
              : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
          }`}
        >
          <ChevronLeft className="w-5 h-5" />
          Previous
        </button>

        <div className="flex gap-3">
          <button
            onClick={() => handleMarkUnderstood(false)}
            className="px-4 py-2 bg-orange-100 text-orange-700 rounded-lg hover:bg-orange-200 transition-colors font-medium"
          >
            Need More Practice
          </button>
          <button
            onClick={() => handleMarkUnderstood(true)}
            className="px-4 py-2 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors font-medium"
          >
            Understood!
          </button>
        </div>

        <button
          onClick={onNext}
          disabled={isLast}
          className={`inline-flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
            isLast
              ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
              : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
          }`}
        >
          Next
          <ChevronRight className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
}