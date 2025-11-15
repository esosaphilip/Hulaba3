package com.example.hulaba3.uilayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.hulaba3.uilayer.screens.topicscreens.AddTopicScreen
import com.example.hulaba3.uilayer.screens.topicscreens.TopicScreen
import com.example.hulaba3.uilayer.screens.wordscreens.AddWordScreen
import com.example.hulaba3.uilayer.screens.wordscreens.EditWordScreen
import com.example.hulaba3.uilayer.screens.wordscreens.WordListScreen
import com.example.hulaba3.uilayer.screens.quiz.QuizScreen
import com.example.hulaba3.uilayer.screens.quiz.QuizResultScreen
import com.example.hulaba3.viewmodel.TopicViewModel
import com.example.hulaba3.viewmodel.WordViewModel
import com.example.hulaba3.viewmodel.QuizViewModel
// Removed Koin dependency; instantiate ViewModels manually using Room repositories
import com.example.hulaba3.data.database.AppDatabase
import com.example.hulaba3.data.repository.WordRepository
import com.example.hulaba3.data.repository.TopicRepository
import com.example.hulaba3.data.repository.QuestionRepository
import com.example.hulaba3.data.repository.StudySessionRepository
import com.example.hulaba3.utils.PdfTextExtractor
import com.example.hulaba3.utils.GeminiApiService
import com.example.hulaba3.uilayer.screens.HomeDashboard
import com.example.hulaba3.uilayer.screens.learning.SmartWordCardScreen
import com.example.hulaba3.uilayer.screens.learning.SpeakingPracticeScreen
import com.example.hulaba3.uilayer.screens.learning.OnTheGoModeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getDatabase(context) }
    val wordViewModel: WordViewModel = remember { WordViewModel(WordRepository(db.wordDao())) }
    val topicViewModel: TopicViewModel = remember {
        TopicViewModel(
            TopicRepository(db.topicDao()),
            com.example.hulaba3.data.repository.StudyMaterialRepository(db.studyMaterialDao())
        )
    }
    val questionRepository = remember { QuestionRepository(db.questionDao(), db.answerDao()) }
    val studySessionRepository = remember { StudySessionRepository(db.studySessionDao(), db.questionAttemptDao()) }
    val quizGenerationService = remember {
        com.example.hulaba3.utils.QuizGenerationService(
            context,
            PdfTextExtractor(context),
            GeminiApiService(),
            questionRepository,
            com.example.hulaba3.data.repository.StudyMaterialRepository(db.studyMaterialDao())
        )
    }
    val quizViewModel: QuizViewModel = remember { QuizViewModel(questionRepository, studySessionRepository, quizGenerationService) }
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        containerColor = Color(0xFFF8F9FA),
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Main Navigation Area
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                composable("home") {
                    HomeDashboard(
                        streakDays = 12,
                        progress = 0.8f,
                        onStartQuickLearning = { navController.navigate("smartWord") },
                        onOpenSpeakingPractice = { navController.navigate("speakingPractice") },
                        onOpenInsights = { navController.navigate("settings") }
                    )
                }
                composable("smartWord") {
                    SmartWordCardScreen(navController = navController, wordViewModel = wordViewModel)
                }
                composable(
                    route = "smartWord/{wordId}",
                    deepLinks = listOf(navDeepLink { uriPattern = "hulaba://smartWord/{wordId}" })
                ) { backStackEntry ->
                    val wordId = backStackEntry.arguments?.getString("wordId")?.toLongOrNull()
                    SmartWordCardScreen(navController = navController, wordId = wordId, wordViewModel = wordViewModel)
                }
                composable("speakingPractice") {
                    SpeakingPracticeScreen()
                }
                composable("onTheGo") {
                    OnTheGoModeScreen(
                        onStartLightning = { /* TODO */ },
                        onStartAudioOnly = { /* TODO */ },
                        onEnablePassive = { /* TODO */ }
                    )
                }
                composable("wordList") {
                    WordListScreen(
                        wordViewModel = wordViewModel,
                        navController = navController
                    )
                }

                composable("addWord") {
                    AddWordScreen(
                        wordViewModel = wordViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("topicList") {
                    TopicScreen(
                        topicViewModel = topicViewModel,
                        quizViewModel = quizViewModel,
                        navController = navController
                    )
                }

                composable("addTopic") {
                    AddTopicScreen(
                        topicViewModel = topicViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("editWord/{wordId}") { backStackEntry ->
                    val wordId = backStackEntry.arguments?.getString("wordId")?.toLong()
                    EditWordScreen(
                        wordViewModel = wordViewModel,
                        wordId = wordId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // NEW: Quiz routes
                composable("quiz/{topicId}") { backStackEntry ->
                    val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
                    QuizScreen(
                        topicId = topicId,
                        isReviewMode = false,
                        quizViewModel = quizViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onQuizComplete = { sessionId ->
                            navController.navigate("quizResult/$sessionId") {
                                popUpTo("topicList") { inclusive = false }
                            }
                        }
                    )
                }

                composable("quiz/{topicId}/review") { backStackEntry ->
                    val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
                    QuizScreen(
                        topicId = topicId,
                        isReviewMode = true,
                        quizViewModel = quizViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onQuizComplete = { sessionId ->
                            navController.navigate("quizResult/$sessionId") {
                                popUpTo("topicList") { inclusive = false }
                            }
                        }
                    )
                }

                composable("quizResult/{sessionId}") { backStackEntry ->
                    val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLongOrNull()
                    if (sessionId != null) {
                        QuizResultScreen(
                            sessionId = sessionId,
                            onNavigateBack = {
                                navController.navigate("topicList") {
                                    popUpTo("topicList") { inclusive = true }
                                }
                            },
                            onRetakeQuiz = { topicId ->
                                navController.navigate("quiz/$topicId") {
                                    popUpTo("topicList") { inclusive = false }
                                }
                            }
                        )
                    }
                }

                composable("settings") {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth(),
        containerColor = Color(0xFF10B981), // Matching Figma green color
        contentColor = Color.White,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0) // This prevents double padding from system insets
    ) {
        val items = listOf(
            BottomNavItem("Home", "home", Icons.Filled.Home),
            BottomNavItem("Speak", "speakingPractice", Icons.Filled.PlayArrow),
            BottomNavItem("Words", "wordList", Icons.AutoMirrored.Filled.List),
            BottomNavItem("Topics", "topicList", Icons.Filled.Info),
            BottomNavItem("Settings", "settings", Icons.Filled.Settings)
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.name,
                        modifier = Modifier.size(28.dp),
                        tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                },
                label = {
                    Text(
                        item.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color.White.copy(alpha = 0.15f)
                )
            )
        }
    }
}

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)