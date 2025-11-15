import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Line, Bar, Doughnut, Radar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  RadialLinearScale,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';
import { 
  TrendingUp, 
  Clock, 
  Target, 
  Award, 
  Brain, 
  BookOpen, 
  Users,
  Calendar,
  Star,
  Zap,
  BarChart3,
  PieChart
} from 'lucide-react';
import { useLearningStore } from '@/store/learningStore';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  RadialLinearScale,
  Title,
  Tooltip,
  Legend,
  Filler
);

interface ProgressAnalyticsProps {
  userId: string;
}

export default function ProgressAnalytics({ userId }: ProgressAnalyticsProps) {
  const [timeRange, setTimeRange] = useState<'week' | 'month' | 'year'>('month');
  const [selectedMetric, setSelectedMetric] = useState<'all' | 'vocabulary' | 'grammar' | 'speaking'>('all');
  
  const { 
    wordProgress, 
    learningSessions, 
    userPreferences,
    getLearningStreak,
    getTotalStudyTime,
    getProgressByCategory
  } = useLearningStore();

  const [streak, setStreak] = useState(0);
  const [totalTime, setTotalTime] = useState(0);
  const [categoryProgress, setCategoryProgress] = useState({});

  useEffect(() => {
    setStreak(getLearningStreak(userId));
    setTotalTime(getTotalStudyTime(userId));
    setCategoryProgress(getProgressByCategory(userId));
  }, [userId, getLearningStreak, getTotalStudyTime, getProgressByCategory]);

  // Calculate statistics
  const totalWords = wordProgress[userId]?.length || 0;
  const masteredWords = wordProgress[userId]?.filter(w => w.status === 'mastered').length || 0;
  const learningWords = wordProgress[userId]?.filter(w => w.status === 'learning').length || 0;
  const newWords = wordProgress[userId]?.filter(w => w.status === 'new').length || 0;

  const masteryRate = totalWords > 0 ? (masteredWords / totalWords) * 100 : 0;
  const learningRate = totalWords > 0 ? (learningWords / totalWords) * 100 : 0;

  // Progress over time data
  const progressData = {
    labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
    datasets: [
      {
        label: 'Words Learned',
        data: [12, 19, 15, 25],
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        fill: true,
        tension: 0.4
      },
      {
        label: 'Speaking Practice',
        data: [8, 12, 18, 22],
        borderColor: 'rgb(16, 185, 129)',
        backgroundColor: 'rgba(16, 185, 129, 0.1)',
        fill: true,
        tension: 0.4
      }
    ]
  };

  // Category distribution
  const categoryData = {
    labels: ['Technology', 'Business', 'Science', 'Arts', 'Sports'],
    datasets: [
      {
        data: [35, 25, 20, 12, 8],
        backgroundColor: [
          'rgba(59, 130, 246, 0.8)',
          'rgba(16, 185, 129, 0.8)',
          'rgba(245, 158, 11, 0.8)',
          'rgba(139, 92, 246, 0.8)',
          'rgba(236, 72, 153, 0.8)'
        ],
        borderWidth: 2,
        borderColor: '#ffffff'
      }
    ]
  };

  // Learning intensity radar chart
  const radarData = {
    labels: ['Vocabulary', 'Grammar', 'Pronunciation', 'Listening', 'Reading', 'Writing'],
    datasets: [
      {
        label: 'Current Level',
        data: [85, 72, 68, 75, 82, 70],
        backgroundColor: 'rgba(59, 130, 246, 0.2)',
        borderColor: 'rgb(59, 130, 246)',
        pointBackgroundColor: 'rgb(59, 130, 246)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgb(59, 130, 246)'
      },
      {
        label: 'Target Level',
        data: [95, 90, 85, 90, 95, 88],
        backgroundColor: 'rgba(16, 185, 129, 0.2)',
        borderColor: 'rgb(16, 185, 129)',
        pointBackgroundColor: 'rgb(16, 185, 129)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgb(16, 185, 129)'
      }
    ]
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top' as const,
      },
      tooltip: {
        mode: 'index' as const,
        intersect: false,
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        grid: {
          color: 'rgba(0, 0, 0, 0.1)'
        }
      },
      x: {
        grid: {
          display: false
        }
      }
    }
  };

  const doughnutOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom' as const,
      }
    }
  };

  const radarOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top' as const,
      }
    },
    scales: {
      r: {
        beginAtZero: true,
        max: 100,
        grid: {
          color: 'rgba(0, 0, 0, 0.1)'
        }
      }
    }
  };

  return (
    <div className="max-w-7xl mx-auto p-6 space-y-8">
      {/* Header */}
      <div className="text-center">
        <h1 className="text-4xl font-bold text-gray-900 mb-2">Learning Analytics</h1>
        <p className="text-xl text-gray-600">
          Track your German learning progress with detailed insights
        </p>
      </div>

      {/* Time Range Selector */}
      <div className="flex justify-center">
        <div className="bg-white rounded-xl shadow-sm p-1 inline-flex">
          {(['week', 'month', 'year'] as const).map((range) => (
            <button
              key={range}
              onClick={() => setTimeRange(range)}
              className={`px-6 py-2 rounded-lg font-medium transition-colors ${
                timeRange === range
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              {range.charAt(0).toUpperCase() + range.slice(1)}
            </button>
          ))}
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-2xl shadow-lg p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <div className="p-3 bg-blue-100 rounded-xl">
              <BookOpen className="w-6 h-6 text-blue-600" />
            </div>
            <TrendingUp className="w-5 h-5 text-green-500" />
          </div>
          <h3 className="text-3xl font-bold text-gray-900 mb-1">{totalWords}</h3>
          <p className="text-gray-600">Total Words</p>
          <p className="text-sm text-green-600 mt-2">+12 this week</p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-2xl shadow-lg p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <div className="p-3 bg-green-100 rounded-xl">
              <Award className="w-6 h-6 text-green-600" />
            </div>
            <Target className="w-5 h-5 text-blue-500" />
          </div>
          <h3 className="text-3xl font-bold text-gray-900 mb-1">{masteredWords}</h3>
          <p className="text-gray-600">Mastered</p>
          <p className="text-sm text-green-600 mt-2">{masteryRate.toFixed(1)}% mastery rate</p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white rounded-2xl shadow-lg p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <div className="p-3 bg-yellow-100 rounded-xl">
              <Zap className="w-6 h-6 text-yellow-600" />
            </div>
            <Star className="w-5 h-5 text-yellow-500" />
          </div>
          <h3 className="text-3xl font-bold text-gray-900 mb-1">{streak}</h3>
          <p className="text-gray-600">Day Streak</p>
          <p className="text-sm text-yellow-600 mt-2">Keep it up!</p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="bg-white rounded-2xl shadow-lg p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <div className="p-3 bg-purple-100 rounded-xl">
              <Clock className="w-6 h-6 text-purple-600" />
            </div>
            <BarChart3 className="w-5 h-5 text-purple-500" />
          </div>
          <h3 className="text-3xl font-bold text-gray-900 mb-1">{Math.floor(totalTime / 60)}h</h3>
          <p className="text-gray-600">Study Time</p>
          <p className="text-sm text-purple-600 mt-2">{totalTime % 60}m this week</p>
        </motion.div>
      </div>

      {/* Progress Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-2xl shadow-lg p-6"
        >
          <h3 className="text-xl font-semibold text-gray-900 mb-6 flex items-center gap-2">
            <TrendingUp className="w-5 h-5" />
            Learning Progress
          </h3>
          <div className="h-80">
            <Line data={progressData} options={chartOptions} />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-2xl shadow-lg p-6"
        >
          <h3 className="text-xl font-semibold text-gray-900 mb-6 flex items-center gap-2">
            <PieChart className="w-5 h-5" />
            Category Distribution
          </h3>
          <div className="h-80">
            <Doughnut data={categoryData} options={doughnutOptions} />
          </div>
        </motion.div>
      </div>

      {/* Skills Radar Chart */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="bg-white rounded-2xl shadow-lg p-6"
      >
        <h3 className="text-xl font-semibold text-gray-900 mb-6 flex items-center gap-2">
          <Brain className="w-5 h-5" />
          Skills Assessment
        </h3>
        <div className="h-96">
          <Radar data={radarData} options={radarOptions} />
        </div>
      </motion.div>

      {/* Learning Insights */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
        className="bg-white rounded-2xl shadow-lg p-6"
      >
        <h3 className="text-xl font-semibold text-gray-900 mb-6 flex items-center gap-2">
          <Brain className="w-5 h-5" />
          AI Learning Insights
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-4">
            <div className="flex items-center gap-3 mb-3">
              <div className="p-2 bg-blue-200 rounded-lg">
                <Target className="w-5 h-5 text-blue-600" />
              </div>
              <h4 className="font-semibold text-blue-900">Focus Areas</h4>
            </div>
            <p className="text-sm text-blue-800">
              Your pronunciation practice is improving. Focus on vocabulary retention this week.
            </p>
          </div>

          <div className="bg-gradient-to-br from-green-50 to-green-100 rounded-xl p-4">
            <div className="flex items-center gap-3 mb-3">
              <div className="p-2 bg-green-200 rounded-lg">
                <Zap className="w-5 h-5 text-green-600" />
              </div>
              <h4 className="font-semibold text-green-900">Optimal Time</h4>
            </div>
            <p className="text-sm text-green-800">
              Your peak learning time is 7-9 PM. Schedule challenging topics during this window.
            </p>
          </div>

          <div className="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl p-4">
            <div className="flex items-center gap-3 mb-3">
              <div className="p-2 bg-purple-200 rounded-lg">
                <Award className="w-5 h-5 text-purple-600" />
              </div>
              <h4 className="font-semibold text-purple-900">Milestones</h4>
            </div>
            <p className="text-sm text-purple-800">
              You're 85% to your monthly goal. Keep your current pace to exceed targets.
            </p>
          </div>
        </div>
      </motion.div>
    </div>
  );
}