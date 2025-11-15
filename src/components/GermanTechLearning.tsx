import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Play, 
  Pause, 
  RotateCcw, 
  Check, 
  X, 
  Volume2, 
  Mic, 
  BookOpen,
  Code,
  Zap,
  Target,
  ArrowRight,
  Star,
  TrendingUp
} from 'lucide-react';
import { useLearningStore } from '@/store/learningStore';
import { GermanTechWord } from '@/types/models';

interface GermanTechLearningProps {
  userId: string;
  onComplete?: (score: number) => void;
}

export default function GermanTechLearning({ userId, onComplete }: GermanTechLearningProps) {
  const [currentWordIndex, setCurrentWordIndex] = useState(0);
  const [showAnswer, setShowAnswer] = useState(false);
  const [userAnswer, setUserAnswer] = useState('');
  const [isCorrect, setIsCorrect] = useState<boolean | null>(null);
  const [score, setScore] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [showExplanation, setShowExplanation] = useState(false);
  const [learningMode, setLearningMode] = useState<'vocabulary' | 'pronunciation' | 'context'>('vocabulary');

  const { germanTechWords, updateWordProgress, addLearningSession } = useLearningStore();

  // Filter German+Tech words for the current user
  const userWords = germanTechWords[userId] || [];
  const currentWord = userWords[currentWordIndex];

  useEffect(() => {
    if (userWords.length === 0) {
      // Initialize with sample German+Tech words if none exist
      const sampleWords: GermanTechWord[] = [
        {
          id: '1',
          german: 'der Algorithmus',
          english: 'algorithm',
          techContext: 'Computer Science',
          pronunciation: '/ˌalɡoˈrɪtmʊs/',
          exampleGerman: 'Der Algorithmus sortiert die Daten effizient.',
          exampleEnglish: 'The algorithm sorts the data efficiently.',
          relatedTerms: ['die Datenstruktur', 'die Komplexität', 'optimieren'],
          difficulty: 'intermediate',
          category: 'programming'
        },
        {
          id: '2',
          german: 'die Variable',
          english: 'variable',
          techContext: 'Programming',
          pronunciation: '/vaˈʁi̯aːblə/',
          exampleGerman: 'Die Variable speichert temporäre Werte.',
          exampleEnglish: 'The variable stores temporary values.',
          relatedTerms: ['der Datentyp', 'initialisieren', 'der Speicher'],
          difficulty: 'beginner',
          category: 'basics'
        },
        {
          id: '3',
          german: 'das Framework',
          english: 'framework',
          techContext: 'Software Development',
          pronunciation: '/ˈfreːmˌvœʁk/',
          exampleGerman: 'Dieses Framework beschleunigt die Entwicklung.',
          exampleEnglish: 'This framework speeds up development.',
          relatedTerms: ['die Bibliothek', 'modular', 'erweiterbar'],
          difficulty: 'intermediate',
          category: 'development'
        }
      ];
      
      // In a real app, this would be handled by the store
      console.log('Sample German+Tech words:', sampleWords);
    }
  }, [userWords, userId]);

  if (!currentWord) {
    return (
      <div className="max-w-4xl mx-auto p-6 text-center">
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <BookOpen className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-900 mb-2">No German+Tech Words Available</h3>
          <p className="text-gray-600">Add some German technology vocabulary to get started!</p>
        </div>
      </div>
    );
  }

  const handleNext = () => {
    if (currentWordIndex < userWords.length - 1) {
      setCurrentWordIndex(currentWordIndex + 1);
      resetCard();
    } else {
      // Session complete
      const finalScore = Math.round((score / userWords.length) * 100);
      onComplete?.(finalScore);
      
      addLearningSession({
        id: Date.now().toString(),
        userId,
        type: 'german_tech',
        duration: Date.now() - startTime,
        score: finalScore,
        wordsStudied: userWords.length,
        accuracy: finalScore,
        timestamp: Date.now()
      });
    }
  };

  const resetCard = () => {
    setShowAnswer(false);
    setUserAnswer('');
    setIsCorrect(null);
    setShowExplanation(false);
  };

  const handleCheckAnswer = () => {
    const correct = userAnswer.toLowerCase().trim() === currentWord.english.toLowerCase().trim();
    setIsCorrect(correct);
    setShowAnswer(true);
    
    if (correct) {
      setScore(score + 1);
      updateWordProgress(userId, currentWord.id, 'correct');
    } else {
      updateWordProgress(userId, currentWord.id, 'incorrect');
    }
  };

  const handlePronunciation = () => {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(currentWord.german);
      utterance.lang = 'de-DE';
      utterance.rate = 0.8;
      speechSynthesis.speak(utterance);
    }
  };

  const handleRecord = () => {
    setIsRecording(!isRecording);
    // Implement speech recording logic here
  };

  const startTime = Date.now();

  return (
    <div className="max-w-4xl mx-auto p-6">
      {/* Header */}
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">German + Tech Learning</h1>
        <p className="text-gray-600">Master German technology vocabulary with context</p>
      </div>

      {/* Progress Bar */}
      <div className="mb-8">
        <div className="flex justify-between items-center mb-2">
          <span className="text-sm font-medium text-gray-700">
            Progress: {currentWordIndex + 1} / {userWords.length}
          </span>
          <span className="text-sm font-medium text-gray-700">
            Score: {score} / {currentWordIndex + 1}
          </span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div 
            className="bg-gradient-to-r from-blue-500 to-purple-600 h-2 rounded-full transition-all duration-300"
            style={{ width: `${((currentWordIndex + 1) / userWords.length) * 100}%` }}
          />
        </div>
      </div>

      {/* Learning Mode Selector */}
      <div className="flex justify-center mb-6">
        <div className="bg-white rounded-xl shadow-sm p-1 inline-flex">
          {(['vocabulary', 'pronunciation', 'context'] as const).map((mode) => (
            <button
              key={mode}
              onClick={() => setLearningMode(mode)}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                learningMode === mode
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              {mode.charAt(0).toUpperCase() + mode.slice(1)}
            </button>
          ))}
        </div>
      </div>

      {/* Main Learning Card */}
      <AnimatePresence mode="wait">
        <motion.div
          key={currentWord.id}
          initial={{ opacity: 0, x: 50 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: -50 }}
          className="bg-white rounded-2xl shadow-xl p-8 mb-6"
        >
          {/* Tech Context Badge */}
          <div className="flex items-center gap-2 mb-6">
            <Code className="w-5 h-5 text-blue-600" />
            <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">
              {currentWord.techContext}
            </span>
            <span className={`px-2 py-1 rounded text-xs font-medium ${
              currentWord.difficulty === 'beginner' ? 'bg-green-100 text-green-800' :
              currentWord.difficulty === 'intermediate' ? 'bg-yellow-100 text-yellow-800' :
              'bg-red-100 text-red-800'
            }`}>
              {currentWord.difficulty}
            </span>
          </div>

          {/* German Word */}
          <div className="text-center mb-8">
            <h2 className="text-4xl font-bold text-gray-900 mb-2">
              {currentWord.german}
            </h2>
            <p className="text-lg text-gray-500 mb-2">
              {currentWord.pronunciation}
            </p>
            <div className="flex justify-center gap-4">
              <button
                onClick={handlePronunciation}
                className="p-2 bg-blue-100 hover:bg-blue-200 rounded-lg transition-colors"
              >
                <Volume2 className="w-5 h-5 text-blue-600" />
              </button>
              {learningMode === 'pronunciation' && (
                <button
                  onClick={handleRecord}
                  className={`p-2 rounded-lg transition-colors ${
                    isRecording ? 'bg-red-100 hover:bg-red-200' : 'bg-gray-100 hover:bg-gray-200'
                  }`}
                >
                  <Mic className={`w-5 h-5 ${isRecording ? 'text-red-600' : 'text-gray-600'}`} />
                </button>
              )}
            </div>
          </div>

          {/* Learning Content Based on Mode */}
          {learningMode === 'vocabulary' && (
            <div className="space-y-6">
              {/* Translation Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  What does this word mean in English?
                </label>
                <input
                  type="text"
                  value={userAnswer}
                  onChange={(e) => setUserAnswer(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && !showAnswer && handleCheckAnswer()}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Type your answer..."
                  disabled={showAnswer}
                />
              </div>

              {/* Answer Feedback */}
              {showAnswer && (
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  className={`p-4 rounded-lg ${
                    isCorrect ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'
                  }`}
                >
                  <div className="flex items-center gap-2 mb-2">
                    {isCorrect ? (
                      <Check className="w-5 h-5 text-green-600" />
                    ) : (
                      <X className="w-5 h-5 text-red-600" />
                    )}
                    <span className={`font-semibold ${
                      isCorrect ? 'text-green-800' : 'text-red-800'
                    }`}>
                      {isCorrect ? 'Correct!' : 'Not quite right'}
                    </span>
                  </div>
                  <p className={isCorrect ? 'text-green-700' : 'text-red-700'}>
                    {currentWord.english}
                  </p>
                </motion.div>
              )}
            </div>
          )}

          {learningMode === 'pronunciation' && (
            <div className="text-center space-y-6">
              <div className="bg-gray-50 rounded-xl p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Practice Pronunciation</h3>
                <p className="text-gray-600 mb-4">
                  Listen to the word and practice saying it aloud. Use the record button when you're ready.
                </p>
                <div className="flex justify-center gap-4">
                  <button
                    onClick={handlePronunciation}
                    className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
                  >
                    <Volume2 className="w-5 h-5" />
                    Listen Again
                  </button>
                  <button
                    onClick={handleRecord}
                    className={`px-6 py-3 rounded-lg transition-colors flex items-center gap-2 ${
                      isRecording 
                        ? 'bg-red-600 text-white hover:bg-red-700' 
                        : 'bg-gray-600 text-white hover:bg-gray-700'
                    }`}
                  >
                    <Mic className="w-5 h-5" />
                    {isRecording ? 'Stop Recording' : 'Record Yourself'}
                  </button>
                </div>
              </div>
            </div>
          )}

          {learningMode === 'context' && (
            <div className="space-y-6">
              <div className="bg-gray-50 rounded-xl p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Context Example</h3>
                <div className="space-y-4">
                  <div>
                    <p className="text-gray-800 font-medium mb-2">German:</p>
                    <p className="text-gray-900 text-lg">{currentWord.exampleGerman}</p>
                  </div>
                  <div>
                    <p className="text-gray-800 font-medium mb-2">English:</p>
                    <p className="text-gray-900">{currentWord.exampleEnglish}</p>
                  </div>
                </div>
              </div>

              {/* Related Terms */}
              <div>
                <h4 className="font-semibold text-gray-900 mb-3">Related Terms</h4>
                <div className="flex flex-wrap gap-2">
                  {currentWord.relatedTerms.map((term, index) => (
                    <span
                      key={index}
                      className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm"
                    >
                      {term}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* Explanation Toggle */}
          <div className="mt-6">
            <button
              onClick={() => setShowExplanation(!showExplanation)}
              className="text-blue-600 hover:text-blue-800 font-medium flex items-center gap-2"
            >
              <BookOpen className="w-4 h-4" />
              {showExplanation ? 'Hide' : 'Show'} Detailed Explanation
            </button>
            
            {showExplanation && (
              <motion.div
                initial={{ opacity: 0, height: 0 }}
                animate={{ opacity: 1, height: 'auto' }}
                className="mt-4 p-4 bg-blue-50 rounded-lg"
              >
                <p className="text-blue-800">
                  This word is commonly used in {currentWord.techContext.toLowerCase()} contexts. 
                  Understanding these terms will help you read German technical documentation and communicate effectively in tech environments.
                </p>
              </motion.div>
            )}
          </div>
        </motion.div>
      </AnimatePresence>

      {/* Action Buttons */}
      <div className="flex justify-between items-center">
        <button
          onClick={resetCard}
          className="px-6 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition-colors flex items-center gap-2"
        >
          <RotateCcw className="w-4 h-4" />
          Reset
        </button>

        <div className="flex gap-4">
          {!showAnswer && learningMode === 'vocabulary' && (
            <button
              onClick={handleCheckAnswer}
              disabled={!userAnswer.trim()}
              className="px-6 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 text-white rounded-lg transition-colors"
            >
              Check Answer
            </button>
          )}
          
          {showAnswer && (
            <button
              onClick={handleNext}
              className="px-6 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors flex items-center gap-2"
            >
              Next
              <ArrowRight className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>

      {/* Achievement Popup */}
      <AnimatePresence>
        {score > 0 && score % 5 === 0 && (
          <motion.div
            initial={{ opacity: 0, scale: 0.8, y: 50 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.8, y: 50 }}
            className="fixed bottom-8 right-8 bg-gradient-to-r from-yellow-400 to-orange-500 text-white p-6 rounded-2xl shadow-2xl"
          >
            <div className="flex items-center gap-3">
              <Star className="w-8 h-8" />
              <div>
                <h3 className="font-bold text-lg">Great Progress!</h3>
                <p className="text-sm opacity-90">You've mastered {score} words!</p>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}