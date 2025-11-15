import { useState, useEffect } from 'react';
import { BookOpen, Code, Globe, Zap, Target, Volume2, Mic, CheckCircle, XCircle } from 'lucide-react';
import { TechGermanItem, LearningSession } from '@/types';

interface GermanTechIntegratedModeProps {
  userId: string;
  onSessionComplete: (session: LearningSession) => void;
}

export default function GermanTechIntegratedMode({
  userId,
  onSessionComplete
}: GermanTechIntegratedModeProps) {
  const [currentItem, setCurrentItem] = useState<TechGermanItem | null>(null);
  const [userAnswer, setUserAnswer] = useState('');
  const [showResult, setShowResult] = useState(false);
  const [isCorrect, setIsCorrect] = useState(false);
  const [sessionItems, setSessionItems] = useState<TechGermanItem[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [sessionStartTime, setSessionStartTime] = useState<Date | null>(null);
  const [score, setScore] = useState(0);
  const [isListening, setIsListening] = useState(false);
  const [showHint, setShowHint] = useState(false);
  const [difficulty, setDifficulty] = useState<'beginner' | 'intermediate' | 'advanced'>('intermediate');

  // Mock German+Tech content
  const germanTechContent: Record<string, TechGermanItem[]> = {
    beginner: [
      {
        id: 'gt_1',
        german: 'der Computer',
        english: 'the computer',
        category: 'hardware',
        pronunciation: 'dare com-POO-ter',
        example: 'Ich benutze den Computer für die Arbeit.',
        techContext: 'Basic computing device',
        difficulty: 'beginner',
        relatedTerms: ['die Tastatur', 'die Maus', 'der Bildschirm']
      },
      {
        id: 'gt_2',
        german: 'das Internet',
        english: 'the internet',
        category: 'network',
        pronunciation: 'dass IN-ter-net',
        example: 'Das Internet ist heute sehr wichtig.',
        techContext: 'Global network connection',
        difficulty: 'beginner',
        relatedTerms: ['die Website', 'der Browser', 'online']
      },
      {
        id: 'gt_3',
        german: 'die E-Mail',
        english: 'the email',
        category: 'communication',
        pronunciation: 'dee EE-mail',
        example: 'Ich schreibe eine E-Mail an meinen Kollegen.',
        techContext: 'Electronic mail communication',
        difficulty: 'beginner',
        relatedTerms: ['senden', 'empfangen', 'Postfach']
      }
    ],
    intermediate: [
      {
        id: 'gt_4',
        german: 'die Datenbank',
        english: 'the database',
        category: 'software',
        pronunciation: 'dee DAH-ten-bank',
        example: 'Die Datenbank speichert alle Kundeninformationen.',
        techContext: 'Structured data storage system',
        difficulty: 'intermediate',
        relatedTerms: ['SQL', 'Tabelle', 'Abfrage']
      },
      {
        id: 'gt_5',
        german: 'der Algorithmus',
        english: 'the algorithm',
        category: 'programming',
        pronunciation: 'dare al-go-RIT-mus',
        example: 'Der Algorithmus löst das Problem effizient.',
        techContext: 'Step-by-step problem solving procedure',
        difficulty: 'intermediate',
        relatedTerms: ['Funktion', 'Schleife', 'Bedingung']
      },
      {
        id: 'gt_6',
        german: 'die Cloud',
        english: 'the cloud',
        category: 'infrastructure',
        pronunciation: 'dee cloud',
        example: 'Wir speichern unsere Dateien in der Cloud.',
        techContext: 'Remote server storage and computing',
        difficulty: 'intermediate',
        relatedTerms: ['Server', 'Speicher', 'online']
      }
    ],
    advanced: [
      {
        id: 'gt_7',
        german: 'die Maschinenlernen',
        english: 'machine learning',
        category: 'ai',
        pronunciation: 'dee mah-SHI-nen-ler-nen',
        example: 'Maschinelles Lernen verbessert die Erkennung.',
        techContext: 'AI algorithm training and prediction',
        difficulty: 'advanced',
        relatedTerms: ['KI', 'Training', 'Modell']
      },
      {
        id: 'gt_8',
        german: 'die API',
        english: 'the API',
        category: 'programming',
        pronunciation: 'dee ah-peh-EE',
        example: 'Die API verbindet verschiedene Anwendungen.',
        techContext: 'Application Programming Interface',
        difficulty: 'advanced',
        relatedTerms: ['Schnittstelle', 'Endpunkt', 'JSON']
      },
      {
        id: 'gt_9',
        german: 'das Framework',
        english: 'the framework',
        category: 'development',
        pronunciation: 'dass FRAME-work',
        example: 'Dieses Framework beschleunigt die Entwicklung.',
        techContext: 'Software development framework',
        difficulty: 'advanced',
        relatedTerms: ['Bibliothek', 'Werkzeug', 'Plattform']
      }
    ]
  };

  useEffect(() => {
    startNewSession();
  }, [difficulty]);

  const startNewSession = () => {
    const items = [...germanTechContent[difficulty]];
    setSessionItems(items);
    setCurrentItem(items[0]);
    setCurrentIndex(0);
    setScore(0);
    setUserAnswer('');
    setShowResult(false);
    setSessionStartTime(new Date());
  };

  const handleNextItem = () => {
    if (currentIndex < sessionItems.length - 1) {
      const nextIndex = currentIndex + 1;
      setCurrentItem(sessionItems[nextIndex]);
      setCurrentIndex(nextIndex);
      setUserAnswer('');
      setShowResult(false);
      setShowHint(false);
    } else {
      // Session complete
      const session: LearningSession = {
        id: `session_${Date.now()}`,
        userId,
        mode: 'german_tech_integrated',
        startTime: sessionStartTime!,
        endTime: new Date(),
        itemsStudied: sessionItems.length,
        correctAnswers: score,
        totalItems: sessionItems.length,
        accuracy: (score / sessionItems.length) * 100,
        difficulty,
        metadata: {
          category: 'mixed',
          focusAreas: ['german', 'tech']
        }
      };
      onSessionComplete(session);
    }
  };

  const handleSubmit = () => {
    if (!currentItem || !userAnswer.trim()) return;

    const correct = userAnswer.toLowerCase().trim() === currentItem.english.toLowerCase().trim();
    setIsCorrect(correct);
    setShowResult(true);
    
    if (correct) {
      setScore(score + 1);
    }
  };

  const handleVoiceInput = () => {
    // Simulate voice recognition
    setIsListening(true);
    setTimeout(() => {
      setIsListening(false);
      // Mock voice input result
      if (currentItem) {
        setUserAnswer(currentItem.english);
      }
    }, 2000);
  };

  const playPronunciation = () => {
    if (currentItem) {
      // Mock pronunciation playback
      const utterance = new SpeechSynthesisUtterance(currentItem.german);
      utterance.lang = 'de-DE';
      speechSynthesis.speak(utterance);
    }
  };

  const getCategoryIcon = (category: string) => {
    switch (category) {
      case 'hardware': return <Code className="w-5 h-5" />;
      case 'network': return <Globe className="w-5 h-5" />;
      case 'communication': return <BookOpen className="w-5 h-5" />;
      case 'software': return <Code className="w-5 h-5" />;
      case 'programming': return <Code className="w-5 h-5" />;
      case 'infrastructure': return <Globe className="w-5 h-5" />;
      case 'ai': return <Zap className="w-5 h-5" />;
      case 'development': return <Target className="w-5 h-5" />;
      default: return <BookOpen className="w-5 h-5" />;
    }
  };

  const getCategoryColor = (category: string) => {
    switch (category) {
      case 'hardware': return 'bg-blue-100 text-blue-800';
      case 'network': return 'bg-green-100 text-green-800';
      case 'communication': return 'bg-purple-100 text-purple-800';
      case 'software': return 'bg-orange-100 text-orange-800';
      case 'programming': return 'bg-red-100 text-red-800';
      case 'infrastructure': return 'bg-indigo-100 text-indigo-800';
      case 'ai': return 'bg-yellow-100 text-yellow-800';
      case 'development': return 'bg-pink-100 text-pink-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  if (!currentItem) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="w-12 h-12 border-4 border-purple-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">Loading German+Tech content...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      {/* Header */}
      <div className="bg-white rounded-2xl shadow-lg p-6 mb-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-4">
            <div className="p-3 bg-gradient-to-r from-purple-100 to-blue-100 rounded-xl">
              <Globe className="w-8 h-8 text-purple-600" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">German + Tech</h1>
              <p className="text-gray-600">Learn tech terminology in German</p>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <select
              value={difficulty}
              onChange={(e) => setDifficulty(e.target.value as any)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            >
              <option value="beginner">Beginner</option>
              <option value="intermediate">Intermediate</option>
              <option value="advanced">Advanced</option>
            </select>
            <div className="text-right">
              <div className="text-2xl font-bold text-purple-600">{score}/{sessionItems.length}</div>
              <div className="text-sm text-gray-600">Item {currentIndex + 1} of {sessionItems.length}</div>
            </div>
          </div>
        </div>

        {/* Progress Bar */}
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div 
            className="bg-gradient-to-r from-purple-500 to-blue-500 h-2 rounded-full transition-all duration-300"
            style={{ width: `${((currentIndex + 1) / sessionItems.length) * 100}%` }}
          ></div>
        </div>
      </div>

      {/* Main Content */}
      <div className="bg-white rounded-2xl shadow-lg p-8">
        <div className="max-w-2xl mx-auto">
          {/* Category Badge */}
          <div className="flex items-center justify-center mb-6">
            <div className={`flex items-center gap-2 px-4 py-2 rounded-full ${getCategoryColor(currentItem.category)}`}>
              {getCategoryIcon(currentItem.category)}
              <span className="font-medium capitalize">{currentItem.category}</span>
            </div>
          </div>

          {/* German Word */}
          <div className="text-center mb-8">
            <h2 className="text-4xl font-bold text-gray-900 mb-2">{currentItem.german}</h2>
            <p className="text-lg text-gray-600 mb-4">{currentItem.pronunciation}</p>
            <button
              onClick={playPronunciation}
              className="inline-flex items-center gap-2 px-4 py-2 bg-purple-100 text-purple-800 rounded-lg hover:bg-purple-200 transition-colors"
            >
              <Volume2 className="w-4 h-4" />
              Play Pronunciation
            </button>
          </div>

          {/* Example Sentence */}
          <div className="bg-gray-50 rounded-xl p-4 mb-8">
            <p className="text-gray-700 italic text-center">"{currentItem.example}"</p>
          </div>

          {/* Tech Context */}
          <div className="bg-blue-50 rounded-xl p-4 mb-8">
            <h3 className="font-semibold text-blue-900 mb-2">Tech Context:</h3>
            <p className="text-blue-800">{currentItem.techContext}</p>
          </div>

          {/* Input Section */}
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                What does "{currentItem.german}" mean in English?
              </label>
              <div className="flex gap-2">
                <input
                  type="text"
                  value={userAnswer}
                  onChange={(e) => setUserAnswer(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSubmit()}
                  className="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Type your answer..."
                  disabled={showResult}
                />
                <button
                  onClick={handleVoiceInput}
                  disabled={isListening || showResult}
                  className="px-4 py-3 bg-purple-100 text-purple-700 rounded-lg hover:bg-purple-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {isListening ? (
                    <div className="w-5 h-5 border-2 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
                  ) : (
                    <Mic className="w-5 h-5" />
                  )}
                </button>
              </div>
            </div>

            {/* Hint Button */}
            <div className="text-center">
              <button
                onClick={() => setShowHint(!showHint)}
                className="text-purple-600 hover:text-purple-700 font-medium"
              >
                {showHint ? 'Hide Hint' : 'Show Hint'}
              </button>
              {showHint && (
                <div className="mt-2 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                  <p className="text-yellow-800 text-sm">
                    Related terms: {currentItem.relatedTerms.join(', ')}
                  </p>
                </div>
              )}
            </div>

            {/* Submit Button */}
            {!showResult && (
              <button
                onClick={handleSubmit}
                disabled={!userAnswer.trim()}
                className="w-full py-3 bg-gradient-to-r from-purple-600 to-blue-600 text-white rounded-lg hover:from-purple-700 hover:to-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all font-medium"
              >
                Check Answer
              </button>
            )}

            {/* Result */}
            {showResult && (
              <div className={`p-4 rounded-lg ${
                isCorrect ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'
              }`}>
                <div className="flex items-center gap-2 mb-2">
                  {isCorrect ? (
                    <CheckCircle className="w-5 h-5 text-green-600" />
                  ) : (
                    <XCircle className="w-5 h-5 text-red-600" />
                  )}
                  <span className={`font-semibold ${
                    isCorrect ? 'text-green-900' : 'text-red-900'
                  }`}>
                    {isCorrect ? 'Correct!' : 'Incorrect'}
                  </span>
                </div>
                {!isCorrect && (
                  <p className={`text-sm ${isCorrect ? 'text-green-700' : 'text-red-700'}`}>
                    The correct answer is: <strong>{currentItem.english}</strong>
                  </p>
                )}
                <button
                  onClick={handleNextItem}
                  className="mt-3 w-full py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors font-medium"
                >
                  {currentIndex < sessionItems.length - 1 ? 'Next Item' : 'Complete Session'}
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}