package com.example.hulaba3.uilayer.screens.topicscreens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.hulaba3.data.database.Topic
import com.example.hulaba3.viewmodel.TopicViewModel
import com.example.hulaba3.viewmodel.QuizViewModel
import kotlinx.coroutines.launch
// Koin injection is handled at MainScreen; view models are passed as parameters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    topicViewModel: TopicViewModel,
    quizViewModel: QuizViewModel,
    navController: NavController
) {
    val topics by topicViewModel.allTopics.collectAsState()
    val quizUiState by quizViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Handle quiz generation completion
    LaunchedEffect(quizUiState.isGenerationComplete) {
        if (quizUiState.isGenerationComplete) {
            // Show success message or navigate to quiz
        }
    }

    // Show error snackbar if quiz generation fails
    quizUiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error toast or snackbar
            quizViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Study Topics",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            shadow = Shadow(
                                offset = Offset(1f, 1f),
                                blurRadius = 3f,
                                color = Color.Black.copy(alpha = 0.2f)
                            )
                        ),
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF25D366)
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("addTopic") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Topic", tint = Color.White)
                    }
                },
                modifier = Modifier.shadow(6.dp, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addTopic") },
                containerColor = Color(0xFF25D366)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Topic", tint = Color.White)
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (topics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No topics yet.\nTap + to add your first one!",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(topics) { topic ->
                    EnhancedTopicItem(
                        topic = topic,
                        onDelete = { topicViewModel.deleteTopic(it) },
                        onOpenPdf = { /* PDF not available on Topic entity; feature removed */ },
                        onGenerateQuestions = { selectedTopic ->
                            quizViewModel.generateQuestionsForTopic(selectedTopic, questionCount = 5)
                        },
                        onStartQuiz = { selectedTopic ->
                            navController.navigate("quiz/${selectedTopic.id}")
                        },
                        onStartReview = { selectedTopic ->
                            navController.navigate("quiz/${selectedTopic.id}/review")
                        },
                        isGeneratingQuestions = quizUiState.isLoading
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedTopicItem(
    topic: Topic,
    onDelete: (Topic) -> Unit,
    onOpenPdf: (Topic) -> Unit,
    onGenerateQuestions: (Topic) -> Unit,
    onStartQuiz: (Topic) -> Unit,
    onStartReview: (Topic) -> Unit,
    isGeneratingQuestions: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    var questionCount by remember { mutableStateOf(0) }

    // Load question count for this topic
    LaunchedEffect(topic.id) {
        // You'll need to add this method to QuizViewModel
        // questionCount = quizViewModel.getQuestionCountForTopic(topic.id)
    }

    // Topic entity does not include review scheduling fields

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                // Question count badge
                if (questionCount > 0) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "$questionCount Q",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF25D366)
                    )
                }
                IconButton(onClick = { onDelete(topic) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Optional metadata
                    Text(
                        "Updated: " + SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(topic.updatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Topic actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onGenerateQuestions(topic) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                        ) {
                            Text("Generate Questions", color = Color.White, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onStartQuiz(topic) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Text("Start Quiz", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // AI Quiz Actions
                    if (questionCount == 0) {
                        // Generate Questions Button
                        Button(
                            onClick = { onGenerateQuestions(topic) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isGeneratingQuestions,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                        ) {
                            if (isGeneratingQuestions) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Text("Generating Questions...", color = Color.White)
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text("Generate AI Questions", color = Color.White)
                                }
                            }
                        }
                    } else {
                        // Quiz Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onStartQuiz(topic) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Start Quiz", color = Color.White, fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = { onStartReview(topic) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Review", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }

                        // Regenerate Questions Button
                        OutlinedButton(
                            onClick = { onGenerateQuestions(topic) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isGeneratingQuestions
                        ) {
                            if (isGeneratingQuestions) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Text("Regenerating...")
                                }
                            } else {
                                Text("Regenerate Questions ($questionCount)")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun openPdfViewer(context: Context, pdfUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(pdfUri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY
    }
    context.startActivity(Intent.createChooser(intent, "Open PDF"))
}