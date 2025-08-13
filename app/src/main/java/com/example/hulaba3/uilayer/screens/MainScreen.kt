package com.example.hulaba3.uilayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hulaba3.uilayer.screens.topicscreens.AddTopicScreen
import com.example.hulaba3.uilayer.screens.topicscreens.TopicScreen
import com.example.hulaba3.uilayer.screens.wordscreens.AddWordScreen
import com.example.hulaba3.uilayer.screens.wordscreens.EditWordScreen
import com.example.hulaba3.uilayer.screens.wordscreens.WordListScreen
import com.example.hulaba3.utils.NotificationHelper
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
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Test Notification Button (for debugging)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        NotificationHelper.showNotification(
                            context = context,
                            title = "Test Notification",
                            message = "This is a test notification to verify the system works!"
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Test Notification")
                }
            }

            // Main Navigation Area
            NavHost(
                navController = navController,
                startDestination = "wordList",
                modifier = Modifier.weight(1f)
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
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        modifier = Modifier
            .height(72.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        containerColor = Color(0xFF25D366),
        contentColor = Color.White
    ) {
        val items = listOf(
            BottomNavItem("Words", "wordList", Icons.Filled.AccountBox),
            BottomNavItem("Topics", "topicList", Icons.Filled.Info)
        )

        items.forEach { item ->
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(item.route) },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(32.dp)
                    )
                },
                label = {
                    Text(
                        item.name,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)