import { useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Upload, FileText, Brain, Tag, BookOpen, Download, Eye, X, CheckCircle, AlertCircle } from 'lucide-react';
import { useLearningStore } from '@/store/learningStore';

interface ExtractedConcept {
  id: string;
  concept: string;
  german: string;
  category: string;
  confidence: number;
  context: string;
  difficulty: 'beginner' | 'intermediate' | 'advanced';
}

interface PDFAnalysisProps {
  userId: string;
  onConceptsExtracted: (concepts: ExtractedConcept[]) => void;
}

export default function PDFAnalysis({ userId, onConceptsExtracted }: PDFAnalysisProps) {
  const [isDragActive, setIsDragActive] = useState(false);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [extractedConcepts, setExtractedConcepts] = useState<ExtractedConcept[]>([]);
  const [currentFile, setCurrentFile] = useState<File | null>(null);
  const [analysisProgress, setAnalysisProgress] = useState(0);
  const [showPreview, setShowPreview] = useState(false);
  const [previewContent, setPreviewContent] = useState('');
  
  const { addWordProgress } = useLearningStore();

  // Mock PDF analysis - in real implementation, this would use actual PDF parsing
  const mockAnalyzePDF = async (file: File): Promise<ExtractedConcept[]> => {
    setIsAnalyzing(true);
    setAnalysisProgress(0);

    // Simulate analysis progress
    for (let i = 0; i <= 100; i += 10) {
      setAnalysisProgress(i);
      await new Promise(resolve => setTimeout(resolve, 200));
    }

    // Mock extracted concepts based on filename
    const mockConcepts: ExtractedConcept[] = [
      {
        id: '1',
        concept: 'Machine Learning Algorithm',
        german: 'der Maschinenlernalgorithmus',
        category: 'Artificial Intelligence',
        confidence: 0.95,
        context: 'Used for pattern recognition and data analysis',
        difficulty: 'advanced'
      },
      {
        id: '2',
        concept: 'Data Structure',
        german: 'die Datenstruktur',
        category: 'Computer Science',
        confidence: 0.92,
        context: 'Organized way of storing and managing data',
        difficulty: 'intermediate'
      },
      {
        id: '3',
        concept: 'Neural Network',
        german: 'das neuronale Netz',
        category: 'Artificial Intelligence',
        confidence: 0.89,
        context: 'Computing system inspired by biological neural networks',
        difficulty: 'advanced'
      },
      {
        id: '4',
        concept: 'Programming Language',
        german: 'die Programmiersprache',
        category: 'Software Development',
        confidence: 0.96,
        context: 'Formal language for computer programming',
        difficulty: 'beginner'
      },
      {
        id: '5',
        concept: 'Database Query',
        german: 'die Datenbankabfrage',
        category: 'Database Management',
        confidence: 0.88,
        context: 'Request for data from a database',
        difficulty: 'intermediate'
      }
    ];

    setIsAnalyzing(false);
    return mockConcepts;
  };

  const handleDragEnter = useCallback((e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragActive(true);
  }, []);

  const handleDragLeave = useCallback((e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragActive(false);
  }, []);

  const handleDragOver = useCallback((e) => {
    e.preventDefault();
    e.stopPropagation();
  }, []);

  const handleDrop = useCallback((e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragActive(false);

    const files = Array.from(e.dataTransfer.files);
    const pdfFile = files.find(file => file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf'));
    
    if (pdfFile) {
      handleFileUpload(pdfFile);
    }
  }, []);

  const handleFileInput = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      handleFileUpload(file);
    }
  };

  const handleFileUpload = async (file: File) => {
    setCurrentFile(file);
    
    try {
      const concepts = await mockAnalyzePDF(file);
      setExtractedConcepts(concepts);
      onConceptsExtracted(concepts);
    } catch (error) {
      console.error('Error analyzing PDF:', error);
    }
  };

  const handlePreview = () => {
    if (currentFile) {
      // Mock preview content
      setPreviewContent(`
        Sample PDF Content:
        
        Machine Learning in Modern Software Development
        
        Machine learning algorithms have revolutionized the way we approach data analysis.
        These sophisticated systems can process vast amounts of information and identify
        patterns that would be impossible for humans to detect manually.
        
        Key Concepts:
        - Neural Networks and Deep Learning
        - Data Structures for ML Applications
        - Programming Languages for AI Development
        - Database Management for Large Datasets
      `);
      setShowPreview(true);
    }
  };

  const addToLearningCollection = (concept: ExtractedConcept) => {
    addWordProgress(userId, concept.id, 'new');
  };

  const exportConcepts = () => {
    const dataStr = JSON.stringify(extractedConcepts, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'extracted-concepts.json';
    link.click();
    URL.revokeObjectURL(url);
  };

  const getConfidenceColor = (confidence: number) => {
    if (confidence >= 0.9) return 'text-green-600';
    if (confidence >= 0.7) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'beginner': return 'bg-green-100 text-green-800';
      case 'intermediate': return 'bg-yellow-100 text-yellow-800';
      case 'advanced': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="max-w-6xl mx-auto p-6 space-y-8">
      {/* Header */}
      <div className="text-center">
        <h1 className="text-4xl font-bold text-gray-900 mb-2">PDF Concept Extractor</h1>
        <p className="text-xl text-gray-600">
          Upload technical documents to extract German vocabulary and concepts
        </p>
      </div>

      {/* Upload Area */}
      {!currentFile && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className={`border-2 border-dashed rounded-2xl p-12 text-center transition-colors ${
            isDragActive ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-gray-400'
          }`}
          onDragEnter={handleDragEnter}
          onDragLeave={handleDragLeave}
          onDragOver={handleDragOver}
          onDrop={handleDrop}
        >
          <Upload className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-900 mb-2">
            Upload Technical PDF
          </h3>
          <p className="text-gray-600 mb-6">
            Drag and drop your PDF file here, or click to browse
          </p>
          <input
            type="file"
            accept=".pdf"
            onChange={handleFileInput}
            className="hidden"
            id="pdf-upload"
          />
          <label
            htmlFor="pdf-upload"
            className="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors cursor-pointer"
          >
            <FileText className="w-5 h-5" />
            Choose PDF File
          </label>
        </motion.div>
      )}

      {/* Current File Info */}
      {currentFile && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-2xl shadow-lg p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <FileText className="w-8 h-8 text-blue-600" />
              <div>
                <h3 className="font-semibold text-gray-900">{currentFile.name}</h3>
                <p className="text-sm text-gray-600">
                  {(currentFile.size / 1024 / 1024).toFixed(2)} MB
                </p>
              </div>
            </div>
            <div className="flex gap-2">
              <button
                onClick={handlePreview}
                className="inline-flex items-center gap-2 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
              >
                <Eye className="w-4 h-4" />
                Preview
              </button>
              <button
                onClick={() => setCurrentFile(null)}
                className="inline-flex items-center gap-2 px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors"
              >
                <X className="w-4 h-4" />
                Remove
              </button>
            </div>
          </div>

          {/* Analysis Progress */}
          {isAnalyzing && (
            <div className="mt-4">
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm font-medium text-gray-700">Analyzing PDF...</span>
                <span className="text-sm text-gray-600">{analysisProgress}%</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="h-2 rounded-full bg-gradient-to-r from-blue-500 to-purple-500 transition-all duration-300"
                  style={{ width: `${analysisProgress}%` }}
                />
              </div>
            </div>
          )}
        </motion.div>
      )}

      {/* Extracted Concepts */}
      {extractedConcepts.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-2xl shadow-lg p-6"
        >
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <Brain className="w-8 h-8 text-purple-600" />
              <div>
                <h3 className="text-xl font-semibold text-gray-900">Extracted Concepts</h3>
                <p className="text-sm text-gray-600">{extractedConcepts.length} concepts found</p>
              </div>
            </div>
            <button
              onClick={exportConcepts}
              className="inline-flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
            >
              <Download className="w-4 h-4" />
              Export
            </button>
          </div>

          <div className="grid gap-4">
            <AnimatePresence>
              {extractedConcepts.map((concept, index) => (
                <motion.div
                  key={concept.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: 20 }}
                  transition={{ delay: index * 0.1 }}
                  className="border border-gray-200 rounded-xl p-4 hover:shadow-md transition-shadow"
                >
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <h4 className="font-semibold text-gray-900">{concept.concept}</h4>
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${getDifficultyColor(concept.difficulty)}`}>
                          {concept.difficulty}
                        </span>
                      </div>
                      <p className="text-lg font-medium text-blue-600 mb-1">{concept.german}</p>
                      <p className="text-sm text-gray-600 mb-2">{concept.context}</p>
                      <div className="flex items-center gap-4 text-sm">
                        <span className="flex items-center gap-1">
                          <Tag className="w-4 h-4 text-gray-400" />
                          <span className="text-gray-600">{concept.category}</span>
                        </span>
                        <span className="flex items-center gap-1">
                          {concept.confidence >= 0.9 ? (
                            <CheckCircle className="w-4 h-4 text-green-500" />
                          ) : (
                            <AlertCircle className="w-4 h-4 text-yellow-500" />
                          )}
                          <span className={getConfidenceColor(concept.confidence)}>
                            {(concept.confidence * 100).toFixed(0)}% confidence
                          </span>
                        </span>
                      </div>
                    </div>
                    <button
                      onClick={() => addToLearningCollection(concept)}
                      className="ml-4 px-3 py-2 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors"
                    >
                      <BookOpen className="w-4 h-4" />
                    </button>
                  </div>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>
        </motion.div>
      )}

      {/* Preview Modal */}
      <AnimatePresence>
        {showPreview && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
            onClick={() => setShowPreview(false)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              className="bg-white rounded-2xl shadow-2xl max-w-4xl w-full max-h-[80vh] overflow-hidden"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-center justify-between p-6 border-b">
                <h3 className="text-xl font-semibold text-gray-900">PDF Preview</h3>
                <button
                  onClick={() => setShowPreview(false)}
                  className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>
              <div className="p-6 overflow-y-auto max-h-[60vh]">
                <pre className="whitespace-pre-wrap text-sm text-gray-700">{previewContent}</pre>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}