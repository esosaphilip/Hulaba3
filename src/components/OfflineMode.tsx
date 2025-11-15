import { useState, useEffect } from 'react';
import { Wifi, WifiOff, Download, Cloud, Database, Settings, AlertCircle, CheckCircle } from 'lucide-react';
import { OfflineContent, CachedResource } from '@/types';

interface OfflineModeProps {
  onContentUpdate: (content: OfflineContent) => void;
  enabled: boolean;
  onToggle: (enabled: boolean) => void;
}

export default function OfflineMode({
  onContentUpdate,
  enabled,
  onToggle
}: OfflineModeProps) {
  const [isOnline, setIsOnline] = useState(navigator.onLine);
  const [cachedContent, setCachedContent] = useState<CachedResource[]>([]);
  const [syncStatus, setSyncStatus] = useState<'synced' | 'syncing' | 'error'>('synced');
  const [storageUsed, setStorageUsed] = useState(0);
  const [storageQuota, setStorageQuota] = useState(0);
  const [autoSync, setAutoSync] = useState(true);
  const [syncInterval, setSyncInterval] = useState(30); // minutes

  // Check online status
  useEffect(() => {
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  // Check storage usage
  useEffect(() => {
    if ('storage' in navigator && 'estimate' in navigator.storage) {
      navigator.storage.estimate().then(estimate => {
        setStorageUsed(estimate.usage || 0);
        setStorageQuota(estimate.quota || 0);
      });
    }
  }, []);

  // Auto-sync when online
  useEffect(() => {
    if (!autoSync || !isOnline) return;

    const interval = setInterval(() => {
      syncContent();
    }, syncInterval * 60 * 1000);

    return () => clearInterval(interval);
  }, [autoSync, syncInterval, isOnline]);

  // Load cached content on mount
  useEffect(() => {
    loadCachedContent();
  }, []);

  const loadCachedContent = async () => {
    try {
      // Load from localStorage/IndexedDB
      const cached = localStorage.getItem('hulaba_offline_content');
      if (cached) {
        const content = JSON.parse(cached);
        setCachedContent(content.resources || []);
      }
    } catch (error) {
      console.error('Failed to load cached content:', error);
    }
  };

  const syncContent = async () => {
    if (!isOnline) return;

    setSyncStatus('syncing');
    try {
      // Simulate API call to sync content
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      const newContent: CachedResource[] = [
        {
          id: 'vocab_set_1',
          type: 'vocabulary',
          title: 'German Tech Vocabulary',
          size: 1024 * 100, // 100KB
          lastUpdated: new Date(),
          priority: 'high',
          content: {
            words: ['Algorithmus', 'Datenstruktur', 'Funktion', 'Variable'],
            translations: ['Algorithm', 'Data Structure', 'Function', 'Variable']
          }
        },
        {
          id: 'grammar_lesson_1',
          type: 'grammar',
          title: 'German Grammar Basics',
          size: 1024 * 200, // 200KB
          lastUpdated: new Date(),
          priority: 'medium',
          content: {
            rules: ['Verb conjugation', 'Noun declension', 'Article usage'],
            examples: ['Ich gehe', 'Der Hund', 'Die Katze']
          }
        },
        {
          id: 'audio_lesson_1',
          type: 'audio',
          title: 'Pronunciation Practice',
          size: 1024 * 1024, // 1MB
          lastUpdated: new Date(),
          priority: 'high',
          content: {
            audioFiles: ['lesson1.mp3', 'lesson2.mp3'],
            transcripts: ['Hello world', 'Good morning']
          }
        },
        {
          id: 'quiz_set_1',
          type: 'quiz',
          title: 'Tech Terminology Quiz',
          size: 1024 * 50, // 50KB
          lastUpdated: new Date(),
          priority: 'medium',
          content: {
            questions: 25,
            difficulty: 'intermediate',
            topics: ['Programming', 'Web Development', 'Database']
          }
        }
      ];

      setCachedContent(newContent);
      
      // Save to localStorage
      localStorage.setItem('hulaba_offline_content', JSON.stringify({
        resources: newContent,
        lastSync: new Date().toISOString()
      }));

      setSyncStatus('synced');
      onContentUpdate({
        resources: newContent,
        lastSync: new Date(),
        isOffline: !isOnline
      });
    } catch (error) {
      setSyncStatus('error');
      console.error('Sync failed:', error);
    }
  };

  const clearCache = async () => {
    try {
      localStorage.removeItem('hulaba_offline_content');
      setCachedContent([]);
      setStorageUsed(0);
    } catch (error) {
      console.error('Failed to clear cache:', error);
    }
  };

  const downloadSpecificContent = async (resourceType: string) => {
    // Simulate downloading specific content
    const newResource: CachedResource = {
      id: `${resourceType}_${Date.now()}`,
      type: resourceType as any,
      title: `Downloaded ${resourceType}`,
      size: 1024 * Math.floor(Math.random() * 500) + 100,
      lastUpdated: new Date(),
      priority: 'high',
      content: {
        message: `Downloaded ${resourceType} content for offline use`
      }
    };

    const updatedContent = [...cachedContent, newResource];
    setCachedContent(updatedContent);
    
    localStorage.setItem('hulaba_offline_content', JSON.stringify({
      resources: updatedContent,
      lastSync: new Date().toISOString()
    }));
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const getStoragePercentage = () => {
    return storageQuota > 0 ? Math.round((storageUsed / storageQuota) * 100) : 0;
  };

  const getResourceIcon = (type: string) => {
    switch (type) {
      case 'vocabulary': return '📚';
      case 'grammar': return '✏️';
      case 'audio': return '🎧';
      case 'quiz': return '❓';
      case 'video': return '🎥';
      default: return '📄';
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="bg-white rounded-2xl shadow-lg p-8">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Offline Mode</h1>
            <p className="text-gray-600">Download and access content without internet connection</p>
          </div>
          <div className="flex items-center gap-4">
            <div className={`flex items-center gap-2 px-3 py-1 rounded-full ${
              isOnline ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
            }`}>
              {isOnline ? <Wifi className="w-4 h-4" /> : <WifiOff className="w-4 h-4" />}
              <span className="text-sm font-medium">{isOnline ? 'Online' : 'Offline'}</span>
            </div>
            <button
              onClick={() => onToggle(!enabled)}
              className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                enabled ? 'bg-purple-600' : 'bg-gray-200'
              }`}
            >
              <span
                className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                  enabled ? 'translate-x-6' : 'translate-x-1'
                }`}
              />
            </button>
          </div>
        </div>

        {/* Sync Status */}
        <div className="mb-8 p-4 bg-gray-50 rounded-xl">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              {syncStatus === 'synced' && <CheckCircle className="w-5 h-5 text-green-600" />}
              {syncStatus === 'syncing' && <Cloud className="w-5 h-5 text-blue-600 animate-pulse" />}
              {syncStatus === 'error' && <AlertCircle className="w-5 h-5 text-red-600" />}
              <div>
                <p className="font-medium text-gray-900">
                  {syncStatus === 'synced' && 'Content is up to date'}
                  {syncStatus === 'syncing' && 'Syncing content...'}
                  {syncStatus === 'error' && 'Sync failed'}
                </p>
                <p className="text-sm text-gray-600">
                  Last sync: {new Date().toLocaleString()}
                </p>
              </div>
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={syncContent}
                disabled={!isOnline || syncStatus === 'syncing'}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
              >
                Sync Now
              </button>
            </div>
          </div>
        </div>

        {enabled && (
          <div className="space-y-8">
            {/* Storage Overview */}
            <div className="bg-gradient-to-r from-blue-50 to-purple-50 rounded-xl p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-bold text-gray-900">Storage Usage</h2>
                <Database className="w-6 h-6 text-purple-600" />
              </div>
              <div className="space-y-3">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Used</span>
                  <span className="font-medium">{formatFileSize(storageUsed)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Available</span>
                  <span className="font-medium">{formatFileSize(storageQuota)}</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div 
                    className="bg-gradient-to-r from-blue-500 to-purple-500 h-2 rounded-full transition-all duration-300"
                    style={{ width: `${getStoragePercentage()}%` }}
                  ></div>
                </div>
                <p className="text-xs text-gray-600 text-center">
                  {getStoragePercentage()}% of available storage used
                </p>
              </div>
            </div>

            {/* Cached Content */}
            <div>
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-bold text-gray-900">Cached Content</h2>
                <div className="flex items-center gap-2">
                  <button
                    onClick={clearCache}
                    className="px-3 py-1 text-sm text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                  >
                    Clear All
                  </button>
                  <button
                    onClick={syncContent}
                    className="px-3 py-1 text-sm text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                  >
                    Refresh
                  </button>
                </div>
              </div>
              
              {cachedContent.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {cachedContent.map((resource) => (
                    <div key={resource.id} className="bg-gray-50 rounded-lg p-4 hover:shadow-md transition-shadow">
                      <div className="flex items-start gap-3">
                        <span className="text-2xl">{getResourceIcon(resource.type)}</span>
                        <div className="flex-1 min-w-0">
                          <h3 className="font-medium text-gray-900 truncate">{resource.title}</h3>
                          <p className="text-sm text-gray-600 capitalize">{resource.type}</p>
                          <div className="flex items-center gap-4 mt-2 text-xs text-gray-500">
                            <span>{formatFileSize(resource.size)}</span>
                            <span className={`px-2 py-1 rounded-full ${
                              resource.priority === 'high' ? 'bg-red-100 text-red-800' :
                              resource.priority === 'medium' ? 'bg-yellow-100 text-yellow-800' :
                              'bg-green-100 text-green-800'
                            }`}>
                              {resource.priority}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-12 bg-gray-50 rounded-xl">
                  <Download className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <h3 className="text-lg font-medium text-gray-900 mb-2">No offline content</h3>
                  <p className="text-gray-600 mb-6">Sync content to use offline mode</p>
                  <button
                    onClick={syncContent}
                    className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
                  >
                    Download Content
                  </button>
                </div>
              )}
            </div>

            {/* Quick Downloads */}
            <div className="bg-gray-50 rounded-xl p-6">
              <h2 className="text-xl font-bold text-gray-900 mb-4">Quick Downloads</h2>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {[
                  { type: 'vocabulary', label: 'Vocabulary', icon: '📚' },
                  { type: 'grammar', label: 'Grammar', icon: '✏️' },
                  { type: 'audio', label: 'Audio', icon: '🎧' },
                  { type: 'quiz', label: 'Quiz', icon: '❓' }
                ].map((item) => (
                  <button
                    key={item.type}
                    onClick={() => downloadSpecificContent(item.type)}
                    className="flex flex-col items-center gap-2 p-4 bg-white rounded-lg hover:shadow-md transition-all hover:scale-105"
                  >
                    <span className="text-2xl">{item.icon}</span>
                    <span className="text-sm font-medium text-gray-900">{item.label}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* Settings */}
            <div className="bg-gray-50 rounded-xl p-6">
              <div className="flex items-center gap-3 mb-4">
                <Settings className="w-5 h-5 text-gray-600" />
                <h2 className="text-xl font-bold text-gray-900">Offline Settings</h2>
              </div>
              <div className="space-y-4">
                <label className="flex items-center justify-between p-3 bg-white rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">Auto-sync when online</p>
                    <p className="text-sm text-gray-600">Automatically sync content when connection is available</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={autoSync}
                    onChange={(e) => setAutoSync(e.target.checked)}
                    className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
                  />
                </label>
                
                <div className="flex items-center justify-between p-3 bg-white rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">Sync interval</p>
                    <p className="text-sm text-gray-600">How often to check for updates</p>
                  </div>
                  <select
                    value={syncInterval}
                    onChange={(e) => setSyncInterval(Number(e.target.value))}
                    className="px-3 py-1 border border-gray-300 rounded-lg text-sm"
                  >
                    <option value={15}>15 minutes</option>
                    <option value={30}>30 minutes</option>
                    <option value={60}>1 hour</option>
                    <option value={120}>2 hours</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        )}

        {!enabled && (
          <div className="text-center py-12">
            <WifiOff className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Offline Mode Disabled</h3>
            <p className="text-gray-600 mb-6">Enable offline mode to download and access content without internet connection.</p>
            <button
              onClick={() => onToggle(true)}
              className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
            >
              Enable Offline Mode
            </button>
          </div>
        )}
      </div>
    </div>
  );
}