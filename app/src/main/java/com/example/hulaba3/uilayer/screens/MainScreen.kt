package com.example.hulaba3.uilayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.hulaba3.uilayer.screens.topicscreens.AddTopicScreen
import com.example.hulaba3.uilayer.screens.topicscreens.TopicScreen
import com.example.hulaba3.uilayer.screens.wordscreens.AddWordScreen
import com.example.hulaba3.uilayer.screens.wordscreens.EditWordScreen
import com.example.hulaba3.uilayer.screens.wordscreens.WordListScreen
import com.example.hulaba3.viewmodel.TopicViewModel
import com.example.hulaba3.viewmodel.WordViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val wordViewModel: WordViewModel = koinViewModel()
    val topicViewModel: TopicViewModel = koinViewModel()
    val navController = rememberNavController()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FA),
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Main Navigation Area
            NavHost(
                navController = navController,
                startDestination = "wordList",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
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
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            BottomNavItem("Words", "wordList", Icons.Filled.AccountBox),
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