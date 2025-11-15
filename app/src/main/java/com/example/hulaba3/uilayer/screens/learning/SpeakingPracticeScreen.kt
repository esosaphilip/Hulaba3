package com.example.hulaba3.uilayer.screens.learning

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hulaba3.uilayer.viewmodels.SpeakingPracticeViewModel
import com.example.hulaba3.uilayer.viewmodels.SpeakingPracticeUiState
import com.example.hulaba3.uilayer.viewmodels.SessionPerformance
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpeakingPracticeScreen(
    viewModel: SpeakingPracticeViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSessionComplete: () -> Unit = {}
) {
    val uiState by viewModel.speakingUiState.collectAsState()
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            val practiceState = uiState as? SpeakingPracticeUiState.Practicing
            val currentIndex = (practiceState?.currentWordIndex ?: 0) + if (practiceState != null) 1 else 0
            val total = practiceState?.totalWords ?: 0
            SpeakingPracticeTopBar(
                currentWord = currentIndex,
                totalWords = total,
                onBackClick = onBackClick
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
                SpeakingPracticeUiState.ScreenState.LOADING -> {
                    LoadingState()
                }
                SpeakingPracticeUiState.ScreenState.PRACTICING -> {
                    PracticingState(
                        uiState = uiState as SpeakingPracticeUiState.Practicing,
                        onStartRecording = {
                            scope.launch {
                                viewModel.startRecording()
                            }
                        },
                        onStopRecording = {
                            scope.launch {
                                viewModel.stopRecording()
                            }
                        },
                        onPlayAudio = {
                            scope.launch {
                                viewModel.playAudio()
                            }
                        },
                        onNextWord = {
                            scope.launch {
                                viewModel.nextWord()
                            }
                        },
                        onSkipWord = {
                            scope.launch {
                                viewModel.skipWord()
                            }
                        }
                    )
                }
                SpeakingPracticeUiState.ScreenState.COMPLETED -> {
                    val completed = uiState as SpeakingPracticeUiState.Completed
                    SessionCompleteState(
                        performance = completed.sessionPerformance,
                        onContinue = {
                            scope.launch {
                                viewModel.startNewSession()
                            }
                        },
                        onFinish = onSessionComplete
                    )
                }
                SpeakingPracticeUiState.ScreenState.ERROR -> {
                    val err = uiState as SpeakingPracticeUiState.Error
                    ErrorState(
                        message = err.errorMessage,
                        onRetry = {
                            scope.launch {
                                viewModel.loadPracticeSession()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeakingPracticeTopBar(
    currentWord: Int,
    totalWords: Int,
    onBackClick: () -> Unit
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
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$currentWord/$totalWords",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = OceanTeal
                        )
                    }
                }
            }
            
            // Progress bar
            LinearProgressIndicator(
                progress = currentWord.toFloat() / totalWords.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = OceanTeal,
                trackColor = RichCharcoal.copy(alpha = 0.1f)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PracticingState(
    uiState: SpeakingPracticeUiState.Practicing,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayAudio: () -> Unit,
    onNextWord: () -> Unit,
    onSkipWord: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Word display card
        WordDisplayCard(
            word = uiState.currentWord,
            pronunciation = uiState.currentWord?.pronunciation ?: "",
            onPlayAudio = onPlayAudio
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Recording controls
        RecordingControls(
            isRecording = uiState.isRecording,
            recordingTime = uiState.recordingTime,
            onStartRecording = onStartRecording,
            onStopRecording = onStopRecording
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Feedback section
        AnimatedVisibility(
            visible = uiState.recordingComplete,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            RecordingFeedback(
                confidence = uiState.recordingConfidence,
                transcription = uiState.recordingTranscription,
                feedback = uiState.recordingFeedback
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onSkipWord,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = RichCharcoal.copy(alpha = 0.7f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Skip"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Skip")
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Button(
                onClick = onNextWord,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OceanTeal
                ),
                enabled = uiState.recordingComplete
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
private fun WordDisplayCard(
    word: Word?,
    pronunciation: String,
    onPlayAudio: () -> Unit
) {
    if (word == null) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // German word
            Text(
                text = word.germanWord,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = RichCharcoal,
                textAlign = TextAlign.Center
            )
            
            // Part of speech chip-like label
            if (!word.partOfSpeech.isNullOrEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = OceanTeal.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = word.partOfSpeech,
                        color = OceanTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Pronunciation
            if (pronunciation.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = CoralPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = pronunciation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CoralPink,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
            
            // Play audio button
            IconButton(
                onClick = onPlayAudio,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = OceanTeal.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Play Audio",
                    tint = OceanTeal,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // Context hint
            if (word.englishTranslation.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MistBlue.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = OceanTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Translation: ${word.englishTranslation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = RichCharcoal.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordingControls(
    isRecording: Boolean,
    recordingTime: Int,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recording button
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if (isRecording) Color(0xFFF44336) else OceanTeal,
                            if (isRecording) Color(0xFFD32F2F) else OceanTeal.copy(alpha = 0.7f)
                        )
                    )
                )
                .clickable {
                    if (isRecording) {
                        onStopRecording()
                    } else {
                        onStartRecording()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = if (isRecording) "Stop" else "Record",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
        
        // Recording time
        if (isRecording) {
            Text(
                text = String.format("%02d:%02d", recordingTime / 60, recordingTime % 60),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFF44336)
            )
        }
        
        // Instructions
        Text(
            text = if (isRecording) {
                "Speak clearly into your microphone"
            } else {
                "Tap to start recording your pronunciation"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = RichCharcoal.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RecordingFeedback(
    confidence: Float,
    transcription: String,
    feedback: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Confidence score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Confidence:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = RichCharcoal
                )
                LinearProgressIndicator(
                    progress = confidence,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = when {
                        confidence >= 0.8f -> Color(0xFF4CAF50)
                        confidence >= 0.6f -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    },
                    trackColor = RichCharcoal.copy(alpha = 0.1f)
                )
                Text(
                    text = "${(confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = when {
                        confidence >= 0.8f -> Color(0xFF4CAF50)
                        confidence >= 0.6f -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            }
            
            // Transcription
            if (transcription.isNotEmpty()) {
                Divider(color = RichCharcoal.copy(alpha = 0.1f))
                
                Text(
                    text = "Your pronunciation:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = RichCharcoal
                )
                Text(
                    text = transcription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OceanTeal,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            
            // Feedback
            if (feedback.isNotEmpty()) {
                Divider(color = RichCharcoal.copy(alpha = 0.1f))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            confidence >= 0.8f -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            confidence >= 0.6f -> Color(0xFFFF9800).copy(alpha = 0.1f)
                            else -> Color(0xFFF44336).copy(alpha = 0.1f)
                        }
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                confidence >= 0.8f -> Icons.Default.CheckCircle
                                confidence >= 0.6f -> Icons.Default.Info
                                else -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = when {
                                confidence >= 0.8f -> Color(0xFF4CAF50)
                                confidence >= 0.6f -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = feedback,
                            style = MaterialTheme.typography.bodySmall,
                            color = RichCharcoal.copy(alpha = 0.9f)
                        )
                    }
                }
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
                text = "Loading practice session...",
                style = MaterialTheme.typography.bodyLarge,
                color = RichCharcoal.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun SessionCompleteState(
    performance: SessionPerformance,
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
                            shape = CircleShape
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
                    text = "Practice Complete!",
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
                            text = "${performance.wordsPracticed}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = OceanTeal
                        )
                        Text(
                            text = "Words Practiced",
                            style = MaterialTheme.typography.bodySmall,
                            color = RichCharcoal.copy(alpha = 0.7f)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(performance.averageConfidence * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = when {
                                performance.averageConfidence >= 0.8f -> Color(0xFF4CAF50)
                                performance.averageConfidence >= 0.6f -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            }
                        )
                        Text(
                            text = "Avg Confidence",
                            style = MaterialTheme.typography.bodySmall,
                            color = RichCharcoal.copy(alpha = 0.7f)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${performance.timeSpent / 60}",
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