package com.example.hulaba3.uilayer.screens.learning

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hulaba3.data.database.*
import com.example.hulaba3.ui.theme.*
import com.example.hulaba3.uilayer.components.SmartWordCard
import com.example.hulaba3.uilayer.components.SmartConceptCard
import com.example.hulaba3.utils.MixedLearningAlgorithm
import com.example.hulaba3.viewmodel.LearningModeViewModel
import com.example.hulaba3.uilayer.screens.learning.MixedLearningUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MixedLearningScreen(
    viewModel: LearningModeViewModel = viewModel(),
    onBackClick: () -> Unit,
    onSessionComplete: (sessionId: Long) -> Unit
) {
    val uiState by viewModel.mixedLearningUiState.collectAsState()
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            MixedLearningTopBar(
                currentItem = uiState.currentItemIndex + 1,
                totalItems = uiState.totalItems,
                timeRemaining = uiState.timeRemaining,
                onBackClick = onBackClick,
                onEndSession = {
                    scope.launch {
                        viewModel.endMixedLearningSession()
                        onSessionComplete(uiState.sessionId)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SoftCream, MistBlue)
                    )
                )
                .padding(paddingValues)
        ) {
            when (uiState.screenState) {
                MixedLearningUiState.ScreenState.LOADING -> {
                    LoadingState()
                }
                MixedLearningUiState.ScreenState.LEARNING -> {
                    LearningState(
                        uiState = uiState,
                        onWordAnswer = { rating ->
                            scope.launch {
                                viewModel.submitWordAnswer(rating)
                            }
                        },
                        onConceptAnswer = { rating ->
                            scope.launch {
                                viewModel.submitConceptAnswer(rating)
                            }
                        },
                        onNextItem = {
                            scope.launch {
                                viewModel.nextLearningItem()
                            }
                        }
                    )
                }
                MixedLearningUiState.ScreenState.COMPLETED -> {
                    SessionCompleteState(
                        performance = uiState.sessionPerformance,
                        onContinue = {
                            scope.launch {
                                viewModel.startNewMixedSession()
                            }
                        },
                        onFinish = {
                            onSessionComplete(uiState.sessionId)
                        }
                    )
                }
                MixedLearningUiState.ScreenState.ERROR -> {
                    ErrorState(
                        message = uiState.errorMessage,
                        onRetry = {
                            scope.launch {
                                viewModel.loadMixedLearningSession()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MixedLearningTopBar(
    currentItem: Int,
    totalItems: Int,
    timeRemaining: Int,
    onBackClick: () -> Unit,
    onEndSession: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = RichCharcoal
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Progress indicator
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = OceanTeal.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$currentItem/$totalItems",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = OceanTeal
                        )
                    }
                    
                    // Time remaining
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = CoralPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${timeRemaining}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = CoralPink
                        )
                    }
                }
                
                IconButton(onClick = onEndSession) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "End Session",
                        tint = CoralPink
                    )
                }
            }
            
            // Progress bar
            LinearProgressIndicator(
                progress = currentItem.toFloat() / totalItems.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = OceanTeal,
                trackColor = RichCharcoal.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun LearningState(
    uiState: MixedLearningUiState,
    onWordAnswer: (Int) -> Unit,
    onConceptAnswer: (Int) -> Unit,
    onNextItem: () -> Unit
) {
    val currentItem = uiState.currentItem
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Session info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.vocabCompleted}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = OceanTeal
                    )
                    Text(
                        text = "Words",
                        style = MaterialTheme.typography.bodySmall,
                        color = RichCharcoal.copy(alpha = 0.7f)
                    )
                }
                
                Divider(
                    modifier = Modifier.height(40.dp).width(1.dp),
                    color = RichCharcoal.copy(alpha = 0.2f)
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.conceptsCompleted}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = CoralPink
                    )
                    Text(
                        text = "Concepts",
                        style = MaterialTheme.typography.bodySmall,
                        color = RichCharcoal.copy(alpha = 0.7f)
                    )
                }
                
                Divider(
                    modifier = Modifier.height(40.dp).width(1.dp),
                    color = RichCharcoal.copy(alpha = 0.2f)
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.accuracy}%",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = when {
                            uiState.accuracy >= 80 -> Color(0xFF4CAF50)
                            uiState.accuracy >= 60 -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
                    Text(
                        text = "Accuracy",
                        style = MaterialTheme.typography.bodySmall,
                        color = RichCharcoal.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Learning card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (currentItem?.type) {
                MixedLearningAlgorithm.LearningType.VOCABULARY -> {
                    val word = currentItem.content as Word
                    val progress = uiState.vocabProgress[word.id]
                    
                    SmartWordCard(
                        word = word,
                        onAudioPlay = { /* TODO: Implement audio playback */ },
                        onFavoriteToggle = {
                            // TODO: Implement favorite toggle
                        },
                        onDifficultyRating = { rating ->
                            onWordAnswer(rating)
                        },
                        isRevealed = uiState.currentItemRevealed,
                        onReveal = {
                            // Card revealed
                        }
                    )
                }
                MixedLearningAlgorithm.LearningType.TOPIC_CONCEPT -> {
                    val concept = currentItem.content as Concept
                    val progress = uiState.conceptProgress[concept.id]
                    
                    SmartConceptCard(
                        concept = concept,
                        conceptProgress = progress,
                        onAudioPlay = { /* TODO: Implement audio playback */ },
                        onNoteAdd = { note ->
                            // TODO: Implement note addition
                        },
                        onDifficultyRating = { rating ->
                            onConceptAnswer(rating)
                        },
                        isRevealed = uiState.currentItemRevealed,
                        onReveal = {
                            // Card revealed
                        }
                    )
                }
                null -> {
                    // Loading state
                    CircularProgressIndicator(color = OceanTeal)
                }
            }
        }
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (uiState.currentItemIndex > 0) {
                OutlinedButton(
                    onClick = { /* TODO: Implement previous item */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = OceanTeal
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Button(
                onClick = onNextItem,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OceanTeal
                )
            ) {
                Text("Next")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next"
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = OceanTeal,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Preparing your mixed learning session...",
                style = MaterialTheme.typography.bodyLarge,
                color = RichCharcoal.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun SessionCompleteState(
    performance: MixedLearningAlgorithm.SessionPerformance,
    onContinue: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Success icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = OceanTeal.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Session Complete",
                        tint = OceanTeal,
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                Text(
                    text = "Session Complete!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = RichCharcoal
                )
                
                // Performance stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${performance.totalItems}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = OceanTeal
                        )
                        Text(
                            text = "Items Studied",
                            style = MaterialTheme.typography.bodySmall,
                            color = RichCharcoal.copy(alpha = 0.7f)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(performance.overallAccuracy * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = when {
                                performance.overallAccuracy >= 0.8f -> Color(0xFF4CAF50)
                                performance.overallAccuracy >= 0.6f -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            }
                        )
                        Text(
                            text = "Accuracy",
                            style = MaterialTheme.typography.bodySmall,
                            color = RichCharcoal.copy(alpha = 0.7f)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${performance.timeSpent / 1000 / 60}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = CoralPink
                        )
                        Text(
                            text = "Minutes",
                            style = MaterialTheme.typography.bodySmall,
                            color = RichCharcoal.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onFinish,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = RichCharcoal
                        )
                    ) {
                        Text("Finish")
                    }
                    
                    Button(
                        onClick = onContinue,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OceanTeal
                        )
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(48.dp)
                )
                
                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.headlineSmall,
                    color = RichCharcoal
                )
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = RichCharcoal.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OceanTeal
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

