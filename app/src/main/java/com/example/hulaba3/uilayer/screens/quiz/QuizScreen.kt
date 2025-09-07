package com.example.hulaba3.uilayer.screens.quiz


import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hulaba3.data.database.QuestionWithAnswers
import com.example.hulaba3.viewmodel.QuizViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    topicId: String,
    isReviewMode: Boolean,
    quizViewModel: QuizViewModel,
    onNavigateBack: () -> Unit,
    onQuizComplete: (Long) -> Unit
) {
    val uiState by quizViewModel.uiState.collectAsState()
    val questionsWithAnswers by quizViewModel.questionsWithAnswers.collectAsState()
    val currentQuestionIndex by quizViewModel.currentQuestionIndex.collectAsState()

    var selectedAnswerId by remember { mutableLongStateOf(-1L) }
    var showResult by remember { mutableStateOf(false) }
    var questionStartTime by remember { mutableLongStateOf(0L) }

    // Load questions when screen starts
    LaunchedEffect(topicId, isReviewMode) {
        if (isReviewMode) {
            quizViewModel.loadQuestionsForReview(topicId)
        } else {
            quizViewModel.loadQuestionsForTopic(topicId)
        }
        quizViewModel.startQuizSession(topicId)
        questionStartTime = System.currentTimeMillis()
    }

    // Handle quiz completion
    LaunchedEffect(uiState.isQuizComplete) {
        if (uiState.isQuizComplete) {
            // Navigate to results with session ID (you'll need to add this to QuizViewModel)
            onQuizComplete(1L) // Placeholder session ID
        }
    }

    // Reset selected answer when question changes
    LaunchedEffect(currentQuestionIndex) {
        selectedAnswerId = -1L
        showResult = false
        questionStartTime = System.currentTimeMillis()
    }

    if (questionsWithAnswers.isEmpty()) {
        QuizLoadingScreen(
            isLoading = uiState.isLoading,
            error = uiState.error,
            onRetry = {
                if (isReviewMode) {
                    quizViewModel.loadQuestionsForReview(topicId)
                } else {
                    quizViewModel.loadQuestionsForTopic(topicId)
                }
            },
            onNavigateBack = onNavigateBack
        )
        return
    }

    val currentQuestion = questionsWithAnswers.getOrNull(currentQuestionIndex)
    if (currentQuestion == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No more questions available")
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isReviewMode) "Review Mode" else "Quiz Mode",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${currentQuestionIndex + 1} of ${questionsWithAnswers.size}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isReviewMode) Color(0xFF8B5CF6) else Color(0xFF6366F1)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress Bar
            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1).toFloat() / questionsWithAnswers.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isReviewMode) Color(0xFF8B5CF6) else Color(0xFF6366F1),
            )

            // Score Display
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Correct: ${uiState.correctAnswers}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        "Answered: ${uiState.answeredQuestions}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = currentQuestion.question.questionText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        ),
                        textAlign = TextAlign.Start
                    )

                    // Answer Options
                    currentQuestion.answers.forEach { answer ->
                        AnswerOption(
                            answer = answer.answerText,
                            isSelected = selectedAnswerId == answer.id,
                            isCorrect = answer.isCorrect,
                            showResult = showResult,
                            onClick = {
                                if (!showResult) {
                                    selectedAnswerId = answer.id
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showResult) {
                    Button(
                        onClick = {
                            if (currentQuestionIndex < questionsWithAnswers.size - 1) {
                                quizViewModel.nextQuestion()
                            } else {
                                quizViewModel.finishQuizSession()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReviewMode) Color(0xFF8B5CF6) else Color(0xFF6366F1)
                        )
                    ) {
                        Text(
                            if (currentQuestionIndex < questionsWithAnswers.size - 1) "Next Question" else "Finish Quiz",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (selectedAnswerId != -1L) {
                                val responseTime = System.currentTimeMillis() - questionStartTime
                                quizViewModel.answerQuestion(selectedAnswerId, responseTime)
                                showResult = true
                            }
                        },
                        enabled = selectedAnswerId != -1L,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReviewMode) Color(0xFF8B5CF6) else Color(0xFF6366F1)
                        )
                    ) {
                        Text("Submit Answer", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerOption(
    answer: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showResult && isCorrect -> Color(0xFFDCFCE7) // Light green
        showResult && isSelected && !isCorrect -> Color(0xFFFEE2E2) // Light red
        isSelected && !showResult -> Color(0xFFEEF2FF) // Light blue
        else -> Color(0xFFF9FAFB) // Light gray
    }

    val borderColor = when {
        showResult && isCorrect -> Color(0xFF059669) // Green
        showResult && isSelected && !isCorrect -> Color(0xFFDC2626) // Red
        isSelected && !showResult -> Color(0xFF6366F1) // Blue
        else -> Color(0xFFE5E7EB) // Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                modifier = Modifier.weight(1f)
            )

            if (showResult) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isCorrect) Color(0xFF059669) else Color(0xFFDC2626),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun QuizLoadingScreen(
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading questions...")
                }
            }
            error != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Failed to load questions",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Red
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                        OutlinedButton(onClick = onNavigateBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("No questions available")
                    Button(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
}