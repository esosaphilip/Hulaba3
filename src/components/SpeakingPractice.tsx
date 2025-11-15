import { useState, useEffect, useRef } from 'react';
import { Mic, MicOff, Play, Stop, Volume2, RotateCcw, ChevronLeft, ChevronRight } from 'lucide-react';
import { SpeakingExercise, SpeakingAttempt, PronunciationScore, FluencyScore, VocabularyScore, GrammarScore } from '@/types';
import { useAppStore } from '@/store/appStore';

interface SpeakingPracticeProps {
  exercise: SpeakingExercise;
  onNext: () => void;
  onPrevious: () => void;
  onCompleteAttempt: (attempt: SpeakingAttempt) => void;
  isFirst: boolean;
  isLast: boolean;
}

export default function SpeakingPractice({
  exercise,
  onNext,
  onPrevious,
  onCompleteAttempt,
  isFirst,
  isLast
}: SpeakingPracticeProps) {
  const [isRecording, setIsRecording] = useState(false);
  const [audioBlob, setAudioBlob] = useState<Blob | null>(null);
  const [audioUrl, setAudioUrl] = useState<string>('');
  const [isPlaying, setIsPlaying] = useState(false);
  const [attempt, setAttempt] = useState<SpeakingAttempt | null>(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [showResults, setShowResults] = useState(false);
  
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const streamRef = useRef<MediaStream | null>(null);

  const addSpeakingAttempt = useAppStore(state => state.addSpeakingAttempt);

  useEffect(() => {
    return () => {
      // Cleanup on unmount
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
      }
      if (audioUrl) {
        URL.revokeObjectURL(audioUrl);
      }
    };
  }, [audioUrl]);

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;
      
      const mediaRecorder = new MediaRecorder(stream);
      mediaRecorderRef.current = mediaRecorder;
      
      const chunks: BlobPart[] = [];
      
      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          chunks.push(event.data);
        }
      };
      
      mediaRecorder.onstop = () => {
        const blob = new Blob(chunks, { type: 'audio/webm' });
        setAudioBlob(blob);
        const url = URL.createObjectURL(blob);
        setAudioUrl(url);
      };
      
      mediaRecorder.start();
      setIsRecording(true);
    } catch (error) {
      console.error('Error starting recording:', error);
      // Fallback: Use Web Speech API for pronunciation feedback
      if ('speechSynthesis' in window) {
        const utterance = new SpeechSynthesisUtterance(exercise.expectedResponse);
        utterance.lang = 'de-DE';
        speechSynthesis.speak(utterance);
      }
    }
  };

  const stopRecording = () => {
    if (mediaRecorderRef.current && isRecording) {
      mediaRecorderRef.current.stop();
      setIsRecording(false);
      
      // Stop all tracks in the stream
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
      }
    }
  };

  const playAudio = () => {
    if (audioUrl && audioRef.current) {
      audioRef.current.play();
      setIsPlaying(true);
    }
  };

  const playExpectedAudio = () => {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(exercise.expectedResponse);
      utterance.lang = 'de-DE';
      utterance.rate = 0.8; // Slower for learning
      speechSynthesis.speak(utterance);
    }
  };

  const analyzeSpeech = async () => {
    if (!audioBlob) return;

    setIsAnalyzing(true);

    // Simulate AI analysis (in a real app, this would call an API)
    setTimeout(() => {
      const mockAnalysis: SpeakingAttempt = {
        id: `attempt_${Date.now()}`,
        exerciseId: exercise.id,
        userAudioUrl: audioUrl,
        duration: Math.floor(Math.random() * 10) + 5,
        accuracy: Math.random() * 0.3 + 0.6, // 60-90%
        pronunciation: {
          overall: Math.random() * 0.3 + 0.6,
          phonemes: [
            { phoneme: 'aʊ', accuracy: Math.random() * 0.3 + 0.6, suggestions: ['Open your mouth wider'] },
            { phoneme: 'ɐ', accuracy: Math.random() * 0.3 + 0.6, suggestions: ['Relax your tongue'] }
          ],
          stress: Math.random() * 0.3 + 0.6,
          intonation: Math.random() * 0.3 + 0.6
        },
        fluency: {
          overall: Math.random() * 0.3 + 0.6,
          pace: Math.random() * 0.3 + 0.6,
          pauses: Math.floor(Math.random() * 3) + 1,
          flow: Math.random() * 0.3 + 0.6
        },
        vocabulary: {
          overall: Math.random() * 0.3 + 0.6,
          wordChoice: Math.random() * 0.3 + 0.6,
          variety: Math.random() * 0.3 + 0.6,
          appropriateness: Math.random() * 0.3 + 0.6
        },
        grammar: {
          overall: Math.random() * 0.3 + 0.6,
          accuracy: Math.random() * 0.3 + 0.6,
          complexity: Math.random() * 0.3 + 0.6,
          wordOrder: Math.random() * 0.3 + 0.6
        },
        feedback: {
          strengths: [
            'Good pronunciation of consonants',
            'Clear articulation'
          ],
          improvements: [
            'Work on vowel pronunciation',
            'Practice stress patterns'
          ],
          specificSuggestions: [
            'Listen to native speakers more',
            'Practice tongue twisters'
          ],
          germanFeedback: 'Gute Aussprache der Konsonanten. Arbeiten Sie an den Vokalen.',
          englishFeedback: 'Good pronunciation of consonants. Work on your vowels.',
          nextSteps: [
            'Practice this phrase 5 more times',
            'Try similar phrases'
          ]
        },
        createdAt: new Date()
      };

      setAttempt(mockAnalysis);
      addSpeakingAttempt(mockAnalysis);
      onCompleteAttempt(mockAnalysis);
      setIsAnalyzing(false);
      setShowResults(true);
    }, 2000);
  };

  const resetExercise = () => {
    setAudioBlob(null);
    setAudioUrl('');
    setAttempt(null);
    setShowResults(false);
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
    }
  };

  const getScoreColor = (score: number) => {
    if (score >= 0.8) return 'text-green-600';
    if (score >= 0.6) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getScoreLabel = (score: number) => {
    if (score >= 0.8) return 'Excellent';
    if (score >= 0.6) return 'Good';
    if (score >= 0.4) return 'Fair';
    return 'Needs Work';
  };

  return (
    <div className="max-w-3xl mx-auto p-6">
      {/* Exercise Header */}
      <div className="bg-white rounded-2xl shadow-lg p-6 mb-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-blue-100 rounded-xl">
              <Mic className="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">Speaking Practice</h2>
              <p className="text-gray-600">{exercise.type.replace('_', ' ')}</p>
            </div>
          </div>
          <div className="flex gap-2">
            <button
              onClick={playExpectedAudio}
              className="p-2 rounded-lg bg-gray-100 hover:bg-gray-200 transition-colors"
              title="Play expected pronunciation"
            >
              <Volume2 className="w-5 h-5 text-gray-600" />
            </button>
            {attempt && (
              <button
                onClick={resetExercise}
                className="p-2 rounded-lg bg-gray-100 hover:bg-gray-200 transition-colors"
                title="Reset exercise"
              >
                <RotateCcw className="w-5 h-5 text-gray-600" />
              </button>
            )}
          </div>
        </div>

        {/* Exercise Content */}
        <div className="space-y-6">
          <div className="text-center">
            <p className="text-lg text-gray-700 mb-2">{exercise.prompt}</p>
            <p className="text-sm text-gray-500 mb-4">{exercise.germanPrompt}</p>
            
            <div className="bg-blue-50 rounded-xl p-6 mb-4">
              <p className="text-xl font-semibold text-blue-900 mb-2">{exercise.expectedResponse}</p>
              <p className="text-sm text-blue-700">[{exercise.pronunciationGuide}]</p>
            </div>
          </div>

          {/* Recording Section */}
          {!showResults && (
            <div className="text-center space-y-4">
              <div className="flex justify-center">
                <button
                  onClick={isRecording ? stopRecording : startRecording}
                  className={`p-6 rounded-full transition-all ${
                    isRecording
                      ? 'bg-red-500 hover:bg-red-600 animate-pulse'
                      : 'bg-blue-500 hover:bg-blue-600'
                  } text-white shadow-lg`}
                >
                  {isRecording ? <MicOff className="w-8 h-8" /> : <Mic className="w-8 h-8" />}
                </button>
              </div>
              
              <p className="text-sm text-gray-600">
                {isRecording ? 'Click to stop recording' : 'Click and speak the phrase above'}
              </p>

              {audioBlob && !isAnalyzing && (
                <div className="space-y-4">
                  <div className="bg-gray-50 rounded-lg p-4">
                    <audio
                      ref={audioRef}
                      src={audioUrl}
                      onEnded={() => setIsPlaying(false)}
                      className="w-full"
                    />
                    <button
                      onClick={playAudio}
                      disabled={isPlaying}
                      className="inline-flex items-center gap-2 px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg transition-colors disabled:opacity-50"
                    >
                      <Play className="w-4 h-4" />
                      {isPlaying ? 'Playing...' : 'Play your recording'}
                    </button>
                  </div>
                  
                  <button
                    onClick={analyzeSpeech}
                    className="px-6 py-3 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors font-medium"
                  >
                    Analyze My Speech
                  </button>
                </div>
              )}

              {isAnalyzing && (
                <div className="text-center py-8">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-600 mx-auto mb-4"></div>
                  <p className="text-gray-600">Analyzing your speech...</p>
                </div>
              )}
            </div>
          )}

          {/* Results Section */}
          {showResults && attempt && (
            <div className="space-y-6">
              {/* Overall Score */}
              <div className="bg-gradient-to-r from-purple-500 to-blue-500 rounded-xl p-6 text-white text-center">
                <h3 className="text-lg font-semibold mb-2">Overall Score</h3>
                <div className="text-4xl font-bold mb-2">{(attempt.accuracy * 100).toFixed(0)}%</div>
                <p className="text-purple-100">{getScoreLabel(attempt.accuracy)}</p>
              </div>

              {/* Detailed Scores */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <ScoreCard
                  title="Pronunciation"
                  score={attempt.pronunciation.overall}
                  details={[
                    `Stress: ${(attempt.pronunciation.stress * 100).toFixed(0)}%`,
                    `Intonation: ${(attempt.pronunciation.intonation * 100).toFixed(0)}%`
                  ]}
                />
                <ScoreCard
                  title="Fluency"
                  score={attempt.fluency.overall}
                  details={[
                    `Pace: ${(attempt.fluency.pace * 100).toFixed(0)}%`,
                    `Pauses: ${attempt.fluency.pauses}`
                  ]}
                />
                <ScoreCard
                  title="Vocabulary"
                  score={attempt.vocabulary.overall}
                  details={[
                    `Word Choice: ${(attempt.vocabulary.wordChoice * 100).toFixed(0)}%`,
                    `Variety: ${(attempt.vocabulary.variety * 100).toFixed(0)}%`
                  ]}
                />
                <ScoreCard
                  title="Grammar"
                  score={attempt.grammar.overall}
                  details={[
                    `Accuracy: ${(attempt.grammar.accuracy * 100).toFixed(0)}%`,
                    `Word Order: ${(attempt.grammar.wordOrder * 100).toFixed(0)}%`
                  ]}
                />
              </div>

              {/* Feedback */}
              <div className="space-y-4">
                {attempt.feedback.strengths.length > 0 && (
                  <div className="bg-green-50 rounded-lg p-4">
                    <h4 className="font-semibold text-green-900 mb-2">Strengths</h4>
                    <ul className="space-y-1">
                      {attempt.feedback.strengths.map((strength, index) => (
                        <li key={index} className="text-green-800 text-sm">• {strength}</li>
                      ))}
                    </ul>
                  </div>
                )}

                {attempt.feedback.improvements.length > 0 && (
                  <div className="bg-orange-50 rounded-lg p-4">
                    <h4 className="font-semibold text-orange-900 mb-2">Areas for Improvement</h4>
                    <ul className="space-y-1">
                      {attempt.feedback.improvements.map((improvement, index) => (
                        <li key={index} className="text-orange-800 text-sm">• {improvement}</li>
                      ))}
                    </ul>
                  </div>
                )}

                <div className="bg-blue-50 rounded-lg p-4">
                  <h4 className="font-semibold text-blue-900 mb-2">Next Steps</h4>
                  <ul className="space-y-1">
                    {attempt.feedback.nextSteps.map((step, index) => (
                      <li key={index} className="text-blue-800 text-sm">• {step}</li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Navigation */}
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

        <div className="text-center">
          <p className="text-sm text-gray-500">
            {exercise.type.replace('_', ' ')} • {exercise.difficulty}
          </p>
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

function ScoreCard({ title, score, details }: {
  title: string;
  score: number;
  details: string[];
}) {
  const getScoreColor = (score: number) => {
    if (score >= 0.8) return 'text-green-600';
    if (score >= 0.6) return 'text-yellow-600';
    return 'text-red-600';
  };

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-4">
      <div className="flex items-center justify-between mb-2">
        <h4 className="font-semibold text-gray-900">{title}</h4>
        <span className={`text-lg font-bold ${getScoreColor(score)}`}>
          {(score * 100).toFixed(0)}%
        </span>
      </div>
      <div className="space-y-1">
        {details.map((detail, index) => (
          <p key={index} className="text-sm text-gray-600">{detail}</p>
        ))}
      </div>
    </div>
  );
}