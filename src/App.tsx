import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Toaster } from 'react-hot-toast';
import Home from "@/pages/Home";
import LearningDashboard from "@/pages/LearningDashboard";
import MixedLearningMode from "@/components/MixedLearningMode";
import { useEffect } from 'react';
import { dataService } from './services/dataService';
import { useAppStore } from './store/appStore';

export default function App() {
  const setWords = useAppStore(state => state.setWords);
  const setTopics = useAppStore(state => state.setTopics);
  const setConcepts = useAppStore(state => state.setConcepts);
  const setGermanTechContent = useAppStore(state => state.setGermanTechContent);
  const setSpeakingExercises = useAppStore(state => state.setSpeakingExercises);
  const setUserProgress = useAppStore(state => state.setUserProgress);

  useEffect(() => {
    // Initialize app data
    const initializeData = async () => {
      try {
        const [words, topics, userProgress] = await Promise.all([
          dataService.getWords(),
          dataService.getTopics(),
          dataService.getUserProgress('user_1')
        ]);

        setWords(words);
        setTopics(topics);
        
        // Extract concepts from topics
        const allConcepts = topics.flatMap(topic => topic.concepts);
        setConcepts(allConcepts);
        
        // Get German tech content
        const techContent = await dataService.getGermanTechContent();
        setGermanTechContent(techContent);
        
        // Get speaking exercises
        const speakingExercises = await dataService.getSpeakingExercises();
        setSpeakingExercises(speakingExercises);
        
        // Set user progress
        if (userProgress) {
          setUserProgress(userProgress);
        }
      } catch (error) {
        console.error('Error initializing app data:', error);
      }
    };

    initializeData();
  }, [setWords, setTopics, setConcepts, setGermanTechContent, setSpeakingExercises, setUserProgress]);

  return (
    <>
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#363636',
            color: '#fff',
          },
        }}
      />
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/dashboard" element={<LearningDashboard />} />
          <Route path="/learn/mixed" element={<MixedLearningMode difficulty="beginner" onComplete={() => window.history.back()} />} />
          <Route path="/other" element={<div className="text-center text-xl">Other Page - Coming Soon</div>} />
        </Routes>
      </Router>
    </>
  );
}