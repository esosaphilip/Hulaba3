import { useState, useEffect } from 'react';
import { Shuffle, Brain, Clock, Target, ChevronLeft, ChevronRight } from 'lucide-react';
import { Word, Concept, GermanTechIntegration, LearningMode, DifficultyLevel } from '@/types';
import { useAppStore } from '@/store/appStore';
import { MixedLearningAlgorithm } from '@/algorithms/mixedLearning';
import WordCard from './WordCard';
import ConceptCard from './ConceptCard';

interface MixedLearningModeProps {
  difficulty: DifficultyLevel;
  onComplete: () => void;
}

export default function MixedLearningMode({ difficulty, onComplete }: MixedLearningModeProps) {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [learningQueue, setLearningQueue] = useState<(Word | Concept | GermanTechIntegration)[]>([]);
  const [sessionStats, setSessionStats] = useState({
    wordsStudied: 0,
    conceptsStudied: 0,
    techContentStudied: 0,
    correctAnswers: 0,
    totalItems: 0,
    startTime: new Date()
  });
  const [isGenerating, setIsGenerating] = useState(true);

  // Get data from store
  const words = useAppStore(state => state.words);
  const concepts = useAppStore(state => state.concepts);
  const germanTechContent = useAppStore(state => state.germanTechContent);
  const userProgress = useAppStore(state => state.userProgress);
  const startSession = useAppStore(state => state.startSession);
  const endSession = useAppStore(state => state.endSession);
  const mixedLearningItems = useAppStore(state => state.mixedLearningItems);
  const generateMixedLearningQueue = useAppStore(state => state.generateMixedLearningQueue);

  useEffect(() => {
    // Start the session
    startSession(LearningMode.MIXED, difficulty);
    
    // Generate mixed learning queue
    generateMixedLearningQueue();
    
    setIsGenerating(false);
  }, [difficulty, startSession, generateMixedLearningQueue]);

  useEffect(() => {
    if (mixedLearningItems.length > 0) {
      setLearningQueue(mixedLearningItems);
      setSessionStats(prev => ({ ...prev, totalItems: mixedLearningItems.length }));
    }
  }, [mixedLearningItems]);

  const handleNext = () => {
    if (currentIndex < learningQueue.length - 1) {
      setCurrentIndex(currentIndex + 1);
    } else {
      // Session complete
      handleSessionComplete();
    }
  };

  const handlePrevious = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 1);
    }
  };

  const handleMarkKnown = (itemId: string, known: boolean, itemType: string) => {
    setSessionStats(prev => ({
      ...prev,
      correctAnswers: known ? prev.correctAnswers + 1 : prev.correctAnswers,
      wordsStudied: itemType === 'word' ? prev.wordsStudied + 1 : prev.wordsStudied,
      conceptsStudied: itemType === 'concept' ? prev.conceptsStudied + 1 : prev.conceptsStudied,
      techContentStudied: itemType === 'tech' ? prev.techContentStudied + 1 : prev.techContentStudied
    }));
  };

  const handleSessionComplete = () => {
    endSession();
    onComplete();
  };

  const getCurrentItem = () => {
    return learningQueue[currentIndex];
  };

  const getProgressPercentage = () => {
    return learningQueue.length > 0 ? ((currentIndex + 1) / learningQueue.length) * 100 : 0;
  };

  const getSessionDuration = () => {
    const now = new Date();
    const duration = Math.round((now.getTime() - sessionStats.startTime.getTime()) / 60000);
    return duration;
  };

  const currentItem = getCurrentItem();

  if (isGenerating) {
    return (
      <div className="max-w-4xl mx-auto p-6">
        <div className="bg-white rounded-2xl shadow-lg p-8 text-center">
          <Brain className="w-16 h-16 text-purple-600 mx-auto mb-4 animate-pulse" />
          <h2 className="text-2xl font-bold text-gray-800 mb-2">Generating Your Learning Path</h2>
          <p className="text-gray-600">Our AI is creating the perfect mix of words, concepts, and tech content for you...</p>
        </div>
      </div>
    );
  }

  if (!currentItem) {
    return (
      <div className="max-w-4xl mx-auto p-6">
        <div className="bg-white rounded-2xl shadow-lg p-8 text-center">
          <Target className="w-16 h-16 text-green-600 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-gray-800 mb-2">Session Complete!</h2>
          <p className="text-gray-600 mb-6">Great job! You've completed your mixed learning session.</p>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            <div className="bg-blue-50 rounded-lg p-4 text-center">
              <div className="text-2xl font-bold text-blue-600">{sessionStats.wordsStudied}</div>
              <div className="text-sm text-blue-800">Words</div>
            </div>
            <div className="bg-purple-50 rounded-lg p-4 text-center">
              <div className="text-2xl font-bold text-purple-600">{sessionStats.conceptsStudied}</div>
              <div className="text-sm text-purple-800">Concepts</div>
            </div>
            <div className="bg-green-50 rounded-lg p-4 text-center">
              <div className="text-2xl font-bold text-green-600">{sessionStats.techContentStudied}</div>
              <div className="text-sm text-green-800">Tech Items</div>
            </div>
            <div className="bg-orange-50 rounded-lg p-4 text-center">
              <div className="text-2xl font-bold text-orange-600">{getSessionDuration()}m</div>
              <div className="text-sm text-orange-800">Duration</div>
            </div>
          </div>
          
          <button
            onClick={handleSessionComplete}
            className="px-6 py-3 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors font-medium"
          >
            Continue Learning
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      {/* Session Header */}
      <div className="bg-white rounded-2xl shadow-lg p-6 mb-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-3">
            <Shuffle className="w-8 h-8 text-purple-600" />
            <div>
              <h2 className="text-xl font-bold text-gray-800">Mixed Learning Mode</h2>
              <p className="text-gray-600">Difficulty: {difficulty}</p>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2 text-sm text-gray-600">
              <Clock className="w-4 h-4" />
              <span>{getSessionDuration()}m</span>
            </div>
            <div className="text-sm text-gray-600">
              {currentIndex + 1} / {learningQueue.length}
            </div>
          </div>
        </div>
        
        {/* Progress Bar */}
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div 
            className="bg-gradient-to-r from-purple-500 to-pink-500 h-2 rounded-full transition-all duration-300"
            style={{ width: `${getProgressPercentage()}%` }}
          />
        </div>
      </div>

      {/* Learning Content */}
      {currentItem && (
        <div className="space-y-6">
          {/* Content Type Indicator */}
          <div className="flex justify-center mb-4">
            <div className={`inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium ${
              currentItem.type === 'word' ? 'bg-blue-100 text-blue-800' :
              currentItem.type === 'concept' ? 'bg-purple-100 text-purple-800' :
              'bg-green-100 text-green-800'
            }`}>
              {currentItem.type === 'word' ? '📝 Word' :
               currentItem.type === 'concept' ? '🧠 Concept' :
               '💻 Tech Integration'}
            </div>
          </div>

          {/* Render appropriate card based on type */}
          {currentItem.type === 'word' && (
            <WordCard
              word={currentItem.item as Word}
              onNext={handleNext}
              onPrevious={handlePrevious}
              onMarkKnown={(wordId, known) => handleMarkKnown(wordId, known, 'word')}
              isFirst={currentIndex === 0}
              isLast={currentIndex === learningQueue.length - 1}
              showProgress={true}
            />
          )}

          {currentItem.type === 'concept' && (
            <ConceptCard
              concept={currentItem.item as Concept}
              onNext={handleNext}
              onPrevious={handlePrevious}
              onMarkUnderstood={(conceptId, understood) => handleMarkKnown(conceptId, understood, 'concept')}
              isFirst={currentIndex === 0}
              isLast={currentIndex === learningQueue.length - 1}
              showProgress={true}
            />
          )}

          {currentItem.type === 'tech' && (
            <GermanTechCard
              techContent={currentItem.item as GermanTechIntegration}
              onNext={handleNext}
              onPrevious={handlePrevious}
              onMarkUnderstood={(techId, understood) => handleMarkKnown(techId, understood, 'tech')}
              isFirst={currentIndex === 0}
              isLast={currentIndex === learningQueue.length - 1}
            />
          )}
        </div>
      )}
    </div>
  );
}

// German Tech Integration Card Component
function GermanTechCard({
  techContent,
  onNext,
  onPrevious,
  onMarkUnderstood,
  isFirst,
  isLast
}: {
  techContent: GermanTechIntegration;
  onNext: () => void;
  onPrevious: () => void;
  onMarkUnderstood: (id: string, understood: boolean) => void;
  isFirst: boolean;
  isLast: boolean;
}) {
  const [understandingLevel, setUnderstandingLevel] = useState(0);

  const handleUnderstandingChange = (level: number) => {
    setUnderstandingLevel(level);
  };

  return (
    <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
      {/* Header */}
      <div className="bg-gradient-to-r from-green-500 to-teal-600 p-6 text-white">
        <div className="flex justify-between items-start mb-4">
          <div className="flex gap-2">
            <span className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-xs font-medium">
              {techContent.category}
            </span>
            <span className="px-3 py-1 bg-teal-100 text-teal-800 rounded-full text-xs font-medium">
              {techContent.difficulty}
            </span>
          </div>
          <div className="text-2xl">💻</div>
        </div>
        
        <div className="text-center">
          <h2 className="text-2xl font-bold mb-2">{techContent.title}</h2>
          <p className="text-lg opacity-90">{techContent.description}</p>
        </div>
      </div>

      {/* Content */}
      <div className="p-6 space-y-6">
        {/* German Terms */}
        <div>
          <h3 className="text-lg font-semibold text-gray-800 mb-3">Key German Terms</h3>
          <div className="grid gap-3">
            {techContent.germanTerms.map((term) => (
              <div key={term.id} className="bg-green-50 rounded-lg p-4">
                <div className="flex justify-between items-start mb-2">
                  <h4 className="font-semibold text-green-900">{term.german}</h4>
                  <span className="text-sm text-green-700">[{term.pronunciation}]</span>
                </div>
                <p className="text-green-800 mb-2">{term.english}</p>
                <p className="text-green-600 text-sm mb-2">Context: {term.context}</p>
                <div className="flex flex-wrap gap-2">
                  {term.relatedTerms.map((relatedTerm, index) => (
                    <span key={index} className="px-2 py-1 bg-green-100 text-green-700 rounded text-xs">
                      {relatedTerm}
                    </span>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Code Examples */}
        {techContent.codeExamples.length > 0 && (
          <div>
            <h3 className="text-lg font-semibold text-gray-800 mb-3">Code Examples</h3>
            <div className="space-y-4">
              {techContent.codeExamples.map((example) => (
                <div key={example.id} className="bg-gray-900 rounded-lg p-4 text-green-400 font-mono text-sm overflow-x-auto">
                  <pre>{example.code}</pre>
                  <div className="mt-3 pt-3 border-t border-gray-700">
                    <p className="text-gray-300 text-xs mb-2">German Comments:</p>
                    {example.germanComments.map((comment, index) => (
                      <p key={index} className="text-gray-400 text-xs mb-1">// {comment}</p>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Understanding Level */}
        <div className="bg-green-50 rounded-lg p-4">
          <h4 className="font-semibold text-green-900 mb-3">How well do you understand this tech integration?</h4>
          <div className="space-y-3">
            <div className="flex justify-between text-sm text-green-700">
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
                      ? 'bg-green-600 text-white'
                      : 'bg-green-100 text-green-700 hover:bg-green-200'
                  }`}
                >
                  {level}%
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <div className="flex justify-between items-center p-6 bg-gray-50">
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
            onClick={() => onMarkUnderstood(techContent.id, false)}
            className="px-4 py-2 bg-orange-100 text-orange-700 rounded-lg hover:bg-orange-200 transition-colors font-medium"
          >
            Need More Practice
          </button>
          <button
            onClick={() => onMarkUnderstood(techContent.id, true)}
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