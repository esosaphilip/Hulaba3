import { useState, useEffect } from 'react';
import { Bell, MapPin, Clock, Cloud, Calendar, Navigation, Smartphone, WifiOff } from 'lucide-react';
import { NotificationContext, WeatherInfo, CalendarEvent, TransitInfo, DeviceContext } from '@/types';

interface SmartNotificationSystemProps {
  onNotificationUpdate: (context: NotificationContext) => void;
  enabled: boolean;
  onToggle: (enabled: boolean) => void;
}

export default function SmartNotificationSystem({
  onNotificationUpdate,
  enabled,
  onToggle
}: SmartNotificationSystemProps) {
  const [currentContext, setCurrentContext] = useState<NotificationContext>({
    timeOfDay: new Date().getHours(),
    dayOfWeek: new Date().getDay(),
    weather: {
      condition: 'sunny',
      temperature: 22,
      location: 'Current Location'
    },
    calendarEvents: [],
    transitInfo: {
      mode: 'walking',
      duration: 15,
      destination: 'Work',
      arrivalTime: new Date(Date.now() + 15 * 60000)
    },
    deviceContext: {
      batteryLevel: 85,
      isCharging: false,
      isConnectedToWifi: true,
      screenOnTime: 120,
      appUsage: [
        { appName: 'Learning App', usageTime: 25, category: 'education' },
        { appName: 'Email', usageTime: 15, category: 'productivity' }
      ]
    }
  });

  const [notificationSettings, setNotificationSettings] = useState({
    timeBased: true,
    locationBased: true,
    weatherBased: true,
    calendarBased: true,
    transitBased: true,
    deviceBased: true,
    learningBased: true
  });

  const [studySuggestions, setStudySuggestions] = useState<string[]>([]);

  // Simulate real-time context updates
  useEffect(() => {
    if (!enabled) return;

    const interval = setInterval(() => {
      updateContext();
      generateSmartSuggestions();
    }, 30000); // Update every 30 seconds

    return () => clearInterval(interval);
  }, [enabled, currentContext]);

  const updateContext = () => {
    const now = new Date();
    setCurrentContext(prev => ({
      ...prev,
      timeOfDay: now.getHours(),
      dayOfWeek: now.getDay(),
      weather: {
        ...prev.weather!,
        condition: Math.random() > 0.7 ? 'cloudy' : 'sunny',
        temperature: prev.weather!.temperature + (Math.random() - 0.5) * 2
      },
      deviceContext: {
        ...prev.deviceContext,
        batteryLevel: Math.max(20, prev.deviceContext.batteryLevel + (Math.random() - 0.5) * 5),
        isCharging: Math.random() > 0.8
      }
    }));
  };

  const generateSmartSuggestions = () => {
    const suggestions: string[] = [];
    const { timeOfDay, weather, calendarEvents, transitInfo, deviceContext } = currentContext;

    // Time-based suggestions
    if (notificationSettings.timeBased) {
      if (timeOfDay >= 6 && timeOfDay <= 8) {
        suggestions.push('🌅 Perfect morning for 15 minutes of German vocabulary review');
      } else if (timeOfDay >= 12 && timeOfDay <= 14) {
        suggestions.push('🍽️ Lunch break? Great time for a quick German grammar exercise');
      } else if (timeOfDay >= 18 && timeOfDay <= 20) {
        suggestions.push('🌆 Evening study session - your peak learning time based on past data');
      }
    }

    // Weather-based suggestions
    if (notificationSettings.weatherBased && weather) {
      if (weather.condition === 'rainy') {
        suggestions.push('🌧️ Rainy day? Perfect for indoor German learning activities');
      } else if (weather.condition === 'sunny' && weather.temperature > 25) {
        suggestions.push('☀️ Beautiful day! Try learning German vocabulary while enjoying the weather');
      }
    }

    // Transit-based suggestions
    if (notificationSettings.transitBased && transitInfo) {
      if (transitInfo.duration > 20) {
        suggestions.push(`🚇 ${transitInfo.duration}-minute ${transitInfo.mode} ride? Perfect for German audio lessons`);
      }
    }

    // Calendar-based suggestions
    if (notificationSettings.calendarBased && calendarEvents) {
      const upcomingEvents = calendarEvents.filter(event => 
        new Date(event.startTime) > new Date() && 
        new Date(event.startTime) < new Date(Date.now() + 2 * 60 * 60 * 1000)
      );
      
      if (upcomingEvents.length > 0) {
        const nextEvent = upcomingEvents[0];
        const timeUntilEvent = Math.round((new Date(nextEvent.startTime).getTime() - Date.now()) / (1000 * 60));
        suggestions.push(`📅 ${timeUntilEvent} minutes until "${nextEvent.title}" - quick German review?`);
      }
    }

    // Device-based suggestions
    if (notificationSettings.deviceBased && deviceContext) {
      if (deviceContext.batteryLevel < 30 && !deviceContext.isCharging) {
        suggestions.push('🔋 Low battery? Switch to offline German lessons to save power');
      }
      
      if (!deviceContext.isConnectedToWifi) {
        suggestions.push('📶 No WiFi? Download offline German content for uninterrupted learning');
      }
    }

    // Learning pattern suggestions
    if (notificationSettings.learningBased) {
      const lastStudy = new Date(Date.now() - 24 * 60 * 60 * 1000); // 24 hours ago
      suggestions.push('🧠 Maintain your streak! Review yesterday\'s German vocabulary');
      
      if (timeOfDay >= 21) {
        suggestions.push('🌙 Evening review: Go through today\'s new German words before sleep');
      }
    }

    setStudySuggestions(suggestions.slice(0, 3)); // Show top 3 suggestions
  };

  const getContextQuality = () => {
    let score = 0;
    let total = 0;

    if (notificationSettings.timeBased) {
      total += 20;
      score += 20; // Always available
    }

    if (notificationSettings.locationBased) {
      total += 20;
      score += currentContext.transitInfo ? 20 : 10;
    }

    if (notificationSettings.weatherBased) {
      total += 15;
      score += currentContext.weather ? 15 : 0;
    }

    if (notificationSettings.calendarBased) {
      total += 15;
      score += currentContext.calendarEvents && currentContext.calendarEvents.length > 0 ? 15 : 5;
    }

    if (notificationSettings.deviceBased) {
      total += 15;
      score += currentContext.deviceContext.batteryLevel > 50 ? 15 : 10;
    }

    if (notificationSettings.learningBased) {
      total += 15;
      score += 15; // Always available
    }

    return Math.round((score / total) * 100);
  };

  const simulateCalendarEvent = () => {
    const events: CalendarEvent[] = [
      {
        id: 'event_1',
        title: 'Team Meeting',
        startTime: new Date(Date.now() + 30 * 60000),
        endTime: new Date(Date.now() + 90 * 60000),
        type: 'work'
      },
      {
        id: 'event_2',
        title: 'Lunch Break',
        startTime: new Date(Date.now() + 120 * 60000),
        endTime: new Date(Date.now() + 180 * 60000),
        type: 'personal'
      },
      {
        id: 'event_3',
        title: 'Study Session',
        startTime: new Date(Date.now() + 240 * 60000),
        endTime: new Date(Date.now() + 300 * 60000),
        type: 'study'
      }
    ];

    setCurrentContext(prev => ({
      ...prev,
      calendarEvents: events
    }));
  };

  const simulateTransit = () => {
    const modes = ['walking', 'cycling', 'public_transport', 'driving'] as const;
    const destinations = ['Work', 'Home', 'University', 'Library', 'Café'];
    
    setCurrentContext(prev => ({
      ...prev,
      transitInfo: {
        mode: modes[Math.floor(Math.random() * modes.length)],
        duration: Math.floor(Math.random() * 45) + 5,
        destination: destinations[Math.floor(Math.random() * destinations.length)],
        arrivalTime: new Date(Date.now() + (Math.floor(Math.random() * 45) + 5) * 60000)
      }
    }));
  };

  const ContextQualityIndicator = ({ quality }: { quality: number }) => {
    const getColor = () => {
      if (quality >= 80) return 'text-green-600';
      if (quality >= 60) return 'text-yellow-600';
      return 'text-red-600';
    };

    const getLabel = () => {
      if (quality >= 80) return 'Excellent';
      if (quality >= 60) return 'Good';
      if (quality >= 40) return 'Fair';
      return 'Limited';
    };

    return (
      <div className="flex items-center gap-2">
        <div className={`w-3 h-3 rounded-full ${
          quality >= 80 ? 'bg-green-500' :
          quality >= 60 ? 'bg-yellow-500' :
          quality >= 40 ? 'bg-orange-500' : 'bg-red-500'
        }`}></div>
        <span className={`text-sm font-medium ${getColor()}`}>
          Context Quality: {getLabel()} ({quality}%)
        </span>
      </div>
    );
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="bg-white rounded-2xl shadow-lg p-8">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Smart Notifications</h1>
            <p className="text-gray-600">AI-powered learning suggestions based on your context</p>
          </div>
          <div className="flex items-center gap-4">
            <ContextQualityIndicator quality={getContextQuality()} />
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

        {enabled && (
          <div className="space-y-8">
            {/* Context Overview */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              <div className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-4">
                <div className="flex items-center gap-3 mb-2">
                  <Clock className="w-5 h-5 text-blue-600" />
                  <span className="text-sm font-medium text-blue-900">Time Context</span>
                </div>
                <p className="text-lg font-bold text-blue-900">{currentContext.timeOfDay}:00</p>
                <p className="text-xs text-blue-700">Day {currentContext.dayOfWeek}</p>
              </div>

              <div className="bg-gradient-to-br from-green-50 to-green-100 rounded-xl p-4">
                <div className="flex items-center gap-3 mb-2">
                  <Cloud className="w-5 h-5 text-green-600" />
                  <span className="text-sm font-medium text-green-900">Weather</span>
                </div>
                <p className="text-lg font-bold text-green-900">{currentContext.weather?.temperature}°C</p>
                <p className="text-xs text-green-700 capitalize">{currentContext.weather?.condition}</p>
              </div>

              <div className="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl p-4">
                <div className="flex items-center gap-3 mb-2">
                  <Navigation className="w-5 h-5 text-purple-600" />
                  <span className="text-sm font-medium text-purple-900">Transit</span>
                </div>
                <p className="text-lg font-bold text-purple-900">{currentContext.transitInfo?.duration}min</p>
                <p className="text-xs text-purple-700">{currentContext.transitInfo?.mode}</p>
              </div>

              <div className="bg-gradient-to-br from-orange-50 to-orange-100 rounded-xl p-4">
                <div className="flex items-center gap-3 mb-2">
                  <Smartphone className="w-5 h-5 text-orange-600" />
                  <span className="text-sm font-medium text-orange-900">Device</span>
                </div>
                <p className="text-lg font-bold text-orange-900">{currentContext.deviceContext.batteryLevel}%</p>
                <p className="text-xs text-orange-700">
                  {currentContext.deviceContext.isCharging ? 'Charging' : 'Battery'}
                </p>
              </div>
            </div>

            {/* Smart Suggestions */}
            <div className="bg-gradient-to-r from-purple-50 to-blue-50 rounded-xl p-6">
              <div className="flex items-center gap-3 mb-6">
                <Bell className="w-6 h-6 text-purple-600" />
                <h2 className="text-xl font-bold text-gray-900">AI Learning Suggestions</h2>
              </div>
              
              {studySuggestions.length > 0 ? (
                <div className="space-y-4">
                  {studySuggestions.map((suggestion, index) => (
                    <div key={index} className="flex items-start gap-3 p-4 bg-white rounded-lg shadow-sm">
                      <div className="w-2 h-2 bg-purple-500 rounded-full mt-2"></div>
                      <p className="text-gray-800">{suggestion}</p>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-8">
                  <Bell className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-600">No suggestions available right now</p>
                </div>
              )}
            </div>

            {/* Notification Settings */}
            <div className="bg-gray-50 rounded-xl p-6">
              <h2 className="text-xl font-bold text-gray-900 mb-6">Context Sources</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {[
                  { key: 'timeBased', label: 'Time of Day', icon: Clock, desc: 'Morning, afternoon, evening patterns' },
                  { key: 'locationBased', label: 'Location', icon: MapPin, desc: 'Home, work, transit locations' },
                  { key: 'weatherBased', label: 'Weather', icon: Cloud, desc: 'Rainy, sunny, temperature-based' },
                  { key: 'calendarBased', label: 'Calendar', icon: Calendar, desc: 'Upcoming events and schedule' },
                  { key: 'transitBased', label: 'Transit', icon: Navigation, desc: 'Commute time and mode' },
                  { key: 'deviceBased', label: 'Device Status', icon: Smartphone, desc: 'Battery, connectivity, usage' }
                ].map((setting) => {
                  const Icon = setting.icon;
                  const isEnabled = notificationSettings[setting.key as keyof typeof notificationSettings];
                  
                  return (
                    <label key={setting.key} className="flex items-center gap-3 p-3 bg-white rounded-lg cursor-pointer hover:shadow-sm transition-shadow">
                      <Icon className="w-5 h-5 text-gray-600" />
                      <div className="flex-1">
                        <p className="font-medium text-gray-900">{setting.label}</p>
                        <p className="text-sm text-gray-600">{setting.desc}</p>
                      </div>
                      <input
                        type="checkbox"
                        checked={isEnabled}
                        onChange={(e) => setNotificationSettings(prev => ({
                          ...prev,
                          [setting.key]: e.target.checked
                        }))}
                        className="w-4 h-4 text-purple-600 rounded focus:ring-purple-500"
                      />
                    </label>
                  );
                })}
              </div>
            </div>

            {/* Simulation Controls */}
            <div className="bg-gray-50 rounded-xl p-6">
              <h2 className="text-xl font-bold text-gray-900 mb-4">Test Scenarios</h2>
              <div className="flex flex-wrap gap-3">
                <button
                  onClick={simulateCalendarEvent}
                  className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
                >
                  Add Calendar Events
                </button>
                <button
                  onClick={simulateTransit}
                  className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors text-sm font-medium"
                >
                  Simulate Transit
                </button>
                <button
                  onClick={generateSmartSuggestions}
                  className="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors text-sm font-medium"
                >
                  Refresh Suggestions
                </button>
              </div>
            </div>
          </div>
        )}

        {!enabled && (
          <div className="text-center py-12">
            <Bell className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Smart Notifications Disabled</h3>
            <p className="text-gray-600 mb-6">Enable smart notifications to receive personalized learning suggestions based on your context.</p>
            <button
              onClick={() => onToggle(true)}
              className="px-6 py-3 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors font-medium"
            >
              Enable Smart Notifications
            </button>
          </div>
        )}
      </div>
    </div>
  );
}