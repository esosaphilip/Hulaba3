import { useState, useEffect } from 'react';
import { Volume2, Eye, EyeOff, ChevronLeft, ChevronRight, Star, RotateCcw } from 'lucide-react';
import { Word, MasteryLevel } from '@/types';
import { useAppStore } from '@/store/appStore';

interface WordCardProps {
  word: Word;
  onNext: () => void;
  onPrevious: () => void;
  onMarkKnown: (wordId: string, known: boolean) => void;
  isFirst: boolean;
  isLast: boolean;
  showProgress?: boolean;
}

export default function WordCard({
  word,
  onNext,
  onPrevious,
  onMarkKnown,
  isFirst,
  isLast,
  showProgress = true
}: WordCardProps) {
  const [isRevealed, setIsRevealed] = useState(false);
  const [isFlipped, setIsFlipped] = useState(false);
  const [userProgress, setUserProgress] = useState({
    confidence: 0,
    difficultyRating: 3,
    notes: ''
  });

  const updateWordProgress = useAppStore(state => state.updateWordProgress);
  const userProgressData = useAppStore(state => state.userProgress);

  useEffect(() => {
    // Reset card state when word changes
    setIsRevealed(false);
    setIsFlipped(false);
    
    // Load existing progress for this word
    if (userProgressData?.wordProgress[word.id]) {
      const progress = userProgressData.wordProgress[word.id];
      setUserProgress({
        confidence: progress.confidence || 0,
        difficultyRating: progress.difficultyRating || 3,
        notes: progress.notes || ''
      });
    }
  }, [word.id, userProgressData]);

  const playAudio = () => {
    if (word.audioUrl) {
      const audio = new Audio(word.audioUrl);
      audio.play().catch(error => {
        console.error('Error playing audio:', error);
        // Fallback: use Web Speech API for pronunciation
        if ('speechSynthesis' in window) {
          const utterance = new SpeechSynthesisUtterance(word.german);
          utterance.lang = 'de-DE';
          speechSynthesis.speak(utterance);
        }
      });
    } else {
      // Use Web Speech API as fallback
      if ('speechSynthesis' in window) {
        const utterance = new SpeechSynthesisUtterance(word.german);
        utterance.lang = 'de-DE';
        speechSynthesis.speak(utterance);
      }
    }
  };

  const handleReveal = () => {
    setIsRevealed(!isRevealed);
  };

  const handleFlip = () => {
    setIsFlipped(!isFlipped);
  };

  const handleMarkKnown = (known: boolean) => {
    onMarkKnown(word.id, known);
    
    // Update progress
    const newMastery = known ? MasteryLevel.MASTERED : MasteryLevel.LEARNING;
    const newConfidence = known ? 0.9 : 0.3;
    
    updateWordProgress(word.id, {
      masteryLevel: newMastery,
      confidence: newConfidence,
      lastReviewed: new Date(),
      reviewCount: (userProgressData?.wordProgress[word.id]?.reviewCount || 0) + 1,
      correctCount: known ? 
        (userProgressData?.wordProgress[word.id]?.correctCount || 0) + 1 : 
        (userProgressData?.wordProgress[word.id]?.correctCount || 0)
    });
  };

  const handleDifficultyChange = (rating: number) => {
    setUserProgress(prev => ({ ...prev, difficultyRating: rating }));
    updateWordProgress(word.id, {
      difficultyRating: rating
    });
  };

  const handleNotesChange = (notes: string) => {
    setUserProgress(prev => ({ ...prev, notes }));
    updateWordProgress(word.id, {
      notes
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

  const currentMastery = userProgressData?.wordProgress[word.id]?.masteryLevel || MasteryLevel.NEW;

  return (
    <div className="max-w-2xl mx-auto p-6">
      {/* Progress Bar */}
      {showProgress && (
        <div className="mb-6">
          <div className="flex justify-between items-center mb-2">
            <span className="text-sm font-medium text-gray-700">Progress</span>
            <span className="text-sm text-gray-500">
              {userProgressData?.wordProgress[word.id]?.reviewCount || 0} reviews
            </span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div 
              className="bg-blue-600 h-2 rounded-full transition-all duration-300"
              style={{ width: `${(userProgress.confidence * 100)}%` }}
            />
          </div>
        </div>
      )}

      {/* Main Card */}
      <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
        {/* Card Header */}
        <div className="bg-gradient-to-r from-blue-500 to-purple-600 p-6 text-white">
          <div className="flex justify-between items-start mb-4">
            <div className="flex gap-2">
              <span className={`px-3 py-1 rounded-full text-xs font-medium ${getDifficultyColor(word.difficulty)}`}>
                {word.difficulty}
              </span>
              <span className={`px-3 py-1 rounded-full text-xs font-medium ${getMasteryColor(currentMastery)}`}>
                {currentMastery}
              </span>
            </div>
            <div className="flex gap-2">
              <button
                onClick={playAudio}
                className="p-2 rounded-full bg-white bg-opacity-20 hover:bg-opacity-30 transition-colors"
                title="Play pronunciation"
              >
                <Volume2 className="w-5 h-5" />
              </button>
              <button
                onClick={handleFlip}
                className="p-2 rounded-full bg-white bg-opacity-20 hover:bg-opacity-30 transition-colors"
                title="Flip card"
              >
                <RotateCcw className="w-5 h-5" />
              </button>
            </div>
          </div>
          
          <div className="text-center">
            <h2 className="text-3xl font-bold mb-2">{word.german}</h2>
            <p className="text-lg opacity-90">[{word.pronunciation}]</p>
          </div>
        </div>

        {/* Card Content */}
        <div className="p-6">
          {!isFlipped ? (
            // Front side - German to English
            <div className="space-y-6">
              {/* Reveal Section */}
              <div className="text-center">
                <button
                  onClick={handleReveal}
                  className="inline-flex items-center gap-2 px-6 py-3 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors font-medium"
                >
                  {isRevealed ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  {isRevealed ? 'Hide Translation' : 'Show Translation'}
                </button>
              </div>

              {/* Translation */}
              <div className={`transition-all duration-300 ${isRevealed ? 'opacity-100' : 'opacity-0'}`}>
                {isRevealed && (
                  <div className="bg-gray-50 rounded-lg p-4 text-center">
                    <h3 className="text-2xl font-semibold text-gray-800 mb-2">{word.english}</h3>
                    <p className="text-gray-600">Category: {word.category}</p>
                  </div>
                )}
              </div>

              {/* Example Sentence */}
              <div className="bg-blue-50 rounded-lg p-4">
                <h4 className="font-semibold text-blue-900 mb-2">Example:</h4>
                <p className="text-blue-800 mb-2 italic">"{word.exampleSentence}"</p>
                <p className="text-blue-600 text-sm">"{word.exampleTranslation}"</p>
              </div>

              {/* Tags */}
              <div className="flex flex-wrap gap-2">
                {word.tags.map(tag => (
                  <span
                    key={tag}
                    className="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm"
                  >
                    #{tag}
                  </span>
                ))}
              </div>
            </div>
          ) : (
            // Back side - Additional details
            <div className="space-y-6">
              <h3 className="text-xl font-semibold text-gray-800">Additional Details</h3>
              
              {/* Frequency */}
              <div className="bg-green-50 rounded-lg p-4">
                <h4 className="font-semibold text-green-900 mb-2">Word Frequency</h4>
                <div className="flex items-center gap-2">
                  <div className="flex-1 bg-green-200 rounded-full h-2">
                    <div 
                      className="bg-green-600 h-2 rounded-full"
                      style={{ width: `${word.frequency}%` }}
                    />
                  </div>
                  <span className="text-green-700 text-sm font-medium">{word.frequency}%</span>
                </div>
                <p className="text-green-600 text-sm mt-1">
                  This word is {word.frequency > 80 ? 'very common' : word.frequency > 50 ? 'common' : 'less common'} in German
                </p>
              </div>

              {/* Difficulty Rating */}
              <div>
                <h4 className="font-semibold text-gray-800 mb-2">How difficult is this word for you?</h4>
                <div className="flex gap-2">
                  {[1, 2, 3, 4, 5].map(rating => (
                    <button
                      key={rating}
                      onClick={() => handleDifficultyChange(rating)}
                      className={`p-2 rounded-lg transition-colors ${
                        userProgress.difficultyRating >= rating
                          ? 'bg-yellow-400 text-yellow-900'
                          : 'bg-gray-200 text-gray-600 hover:bg-gray-300'
                      }`}
                    >
                      <Star className="w-5 h-5" fill={userProgress.difficultyRating >= rating ? 'currentColor' : 'none'} />
                    </button>
                  ))}
                </div>
              </div>

              {/* Notes */}
              <div>
                <h4 className="font-semibold text-gray-800 mb-2">Personal Notes</h4>
                <textarea
                  value={userProgress.notes}
                  onChange={(e) => handleNotesChange(e.target.value)}
                  placeholder="Add your own notes about this word..."
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                  rows={3}
                />
              </div>
            </div>
          )}
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
            onClick={() => handleMarkKnown(false)}
            className="px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors font-medium"
          >
            Need Practice
          </button>
          <button
            onClick={() => handleMarkKnown(true)}
            className="px-4 py-2 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors font-medium"
          >
            Got It!
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