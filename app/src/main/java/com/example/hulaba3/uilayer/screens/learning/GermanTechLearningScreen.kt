package com.example.hulaba3.uilayer.screens.learning

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import org.koin.androidx.compose.koinViewModel
import com.example.hulaba3.R
// GermanTechWord is in the same package; no import needed
import com.example.hulaba3.uilayer.components.GlassCard
// Removed SmartWordCard import – using GlassCard-based layout in this screen
import com.example.hulaba3.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GermanTechLearningScreen(
    viewModel: GermanTechLearningViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("German + Tech Learning") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            when (uiState.screenState) {
                GermanTechUiState.ScreenState.IDLE -> {
                    StartScreen(
                        onStart = { viewModel.startLearningSession() }
                    )
                }
                GermanTechUiState.ScreenState.LOADING -> {
                    LoadingScreen()
                }
                GermanTechUiState.ScreenState.LEARNING -> {
                    LearningContent(
                        uiState = uiState,
                        onSubmitAnswer = { viewModel.submitAnswer(it) },
                        onToggleAnswer = { viewModel.toggleAnswer() },
                        onNextWord = { viewModel.nextWord() },
                        onStartRecording = { viewModel.startRecording() },
                        onStopRecording = { viewModel.stopRecording() }
                    )
                }
                GermanTechUiState.ScreenState.COMPLETED -> {
                    CompletionScreen(
                        performance = uiState.sessionPerformance,
                        onFinish = onBack
                    )
                }
                GermanTechUiState.ScreenState.ERROR -> {
                    ErrorScreen(
                        message = uiState.errorMessage ?: "An error occurred",
                        onRetry = { viewModel.startLearningSession() }

                    )
                }
            }
        }
    }
}

@Composable
private fun StartScreen(
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "German + Tech Learning",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Master German vocabulary with tech context",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FeatureChip("Vocabulary")
                    FeatureChip("Pronunciation")
                    FeatureChip("Context")
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Start Learning Session")
                }
            }
        }
    }
}

@Composable
private fun FeatureChip(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun LearningContent(
    uiState: GermanTechUiState,
    onSubmitAnswer: (String) -> Unit,
    onToggleAnswer: () -> Unit,
    onNextWord: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    val currentWord = uiState.currentWord ?: return
    val answerText = remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicator
        LearningProgressBar(
            current = uiState.currentWordIndex,
            total = uiState.totalWords,
            accuracy = uiState.sessionPerformance?.accuracy ?: 0f
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Word card
        WordLearningCard(
            word = currentWord,
            learningMode = uiState.learningMode,
            showAnswer = uiState.showAnswer,
            isRecording = uiState.isRecording,
            recordingResult = uiState.recordingResult,
            onToggleAnswer = onToggleAnswer,
            onStartRecording = onStartRecording,
            onStopRecording = onStopRecording
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Answer input
        when (uiState.learningMode) {
            "vocabulary" -> {
                VocabularyAnswerInput(
                    answerText = answerText.value,
                    onAnswerChange = { answerText.value = it },
                    onSubmit = {
                        if (answerText.value.isNotBlank()) {
                            onSubmitAnswer(answerText.value)
                        }
                    },
                    enabled = !uiState.showAnswer
                )
            }
            "pronunciation" -> {
                PronunciationAnswerSection(
                    isRecording = uiState.isRecording,
                    recordingResult = uiState.recordingResult,
                    onStartRecording = onStartRecording,
                    onStopRecording = onStopRecording,
                    onNext = onNextWord
                )
            }
            "context" -> {
                ContextAnswerSection(
                    answerText = answerText.value,
                    onAnswerChange = { answerText.value = it },
                    onSubmit = {
                        if (answerText.value.isNotBlank()) {
                            onSubmitAnswer(answerText.value)
                        }
                    },
                    enabled = !uiState.showAnswer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Answer result
        uiState.lastAnswerResult?.let { result ->
            AnswerResultCard(result)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onNextWord) {
                    Text("Next Word")
                }
            }
        }
    }
}

@Composable
private fun LearningProgressBar(
    current: Int,
    total: Int,
    accuracy: Float
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Progress: $current / $total",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                val progressValue = if (total > 0) current.toFloat() / total else 0f
                LinearProgressIndicator(
                    progress = progressValue,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (accuracy >= 0.7f) Color.Green else MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(accuracy * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (accuracy >= 0.7f) Color.Green else Color.Red
                )
                Text(
                    text = "Accuracy",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun WordLearningCard(
    word: GermanTechWord,
    learningMode: String,
    showAnswer: Boolean,
    isRecording: Boolean,
    recordingResult: GermanTechUiState.RecordingResult?,
    onToggleAnswer: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Word and translation
            Text(
                text = word.german,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = RichCharcoal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = word.english,
                style = MaterialTheme.typography.titleMedium,
                color = OceanTeal
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (learningMode) {
                "pronunciation" -> {
                    PronunciationSection(
                        word = word,
                        isRecording = isRecording,
                        recordingResult = recordingResult,
                        onStartRecording = onStartRecording,
                        onStopRecording = onStopRecording
                    )
                }
                "context" -> {
                    ContextSection(
                        word = word,
                        showAnswer = showAnswer,
                        onToggleAnswer = onToggleAnswer
                    )
                }
                else -> {
                    // Default vocabulary view
                    if (showAnswer) {
                        AnswerSection(word)
                    }
                }
            }
        }
    }
}

@Composable
private fun PronunciationSection(
    word: GermanTechWord,
    isRecording: Boolean,
    recordingResult: GermanTechUiState.RecordingResult?,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tap to record your pronunciation",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = if (isRecording) onStopRecording else onStartRecording,
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording) "Stop recording" else "Start recording",
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        recordingResult?.let { result ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (result.pronunciationScore >= 0.8f) {
                        Color.Green.copy(alpha = 0.1f)
                    } else {
                        Color.Red.copy(alpha = 0.1f)
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Score: ${(result.pronunciationScore * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (result.pronunciationScore >= 0.8f) Color.Green else Color.Red
                    )
                    Text(
                        text = result.feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextSection(
    word: GermanTechWord,
    showAnswer: Boolean,
    onToggleAnswer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Use this word in a tech context:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (showAnswer) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Example Context:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = OceanTeal
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = word.contextExplanation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Translation: ${word.english}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            OutlinedButton(onClick = onToggleAnswer) {
                Text("Show Context")
            }
        }
    }
}

@Composable
private fun AnswerSection(word: GermanTechWord) {
    Column {
        Text(
            text = "Translation:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = word.english,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        word.pronunciation?.let { pronunciation ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "[$pronunciation]",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun VocabularyAnswerInput(
    answerText: String,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    enabled: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = answerText,
                onValueChange = onAnswerChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter translation") },
                enabled = enabled,
                singleLine = true,
                // Removed keyboardOptions and keyboardActions to avoid unresolved references with current Compose setup
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled && answerText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Submit Answer")
            }
        }
    }
}

@Composable
private fun PronunciationAnswerSection(
    isRecording: Boolean,
    recordingResult: GermanTechUiState.RecordingResult?,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onNext: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (recordingResult != null) {
                Button(onClick = onNext) {
                    Text("Next Word")
                }
            }
        }
    }
}

@Composable
private fun ContextAnswerSection(
    answerText: String,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    enabled: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = answerText,
                onValueChange = onAnswerChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Use this word in a sentence") },
                enabled = enabled,
                minLines = 2,
                // Removed keyboardOptions and keyboardActions to avoid unresolved references with current Compose setup
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled && answerText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Submit Answer")
            }
        }
    }
}

@Composable
private fun AnswerResultCard(result: GermanTechUiState.AnswerResult) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (result.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (result.isCorrect) Color.Green else Color.Red
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (result.isCorrect) "Correct!" else "Incorrect",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (result.isCorrect) Color.Green else Color.Red
                )
                
                Text(
                    text = result.feedback,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun CompletionScreen(
    performance: GermanTechUiState.SessionPerformance?,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Session Complete!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                performance?.let { perf ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Words Learned: ${perf.wordsLearned}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Accuracy: ${(perf.accuracy * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Pronunciation: ${(perf.pronunciationScore * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
                ) {
                    Text("Finish")
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Red
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
                ) {
                    Text("Retry")
                }
            }
        }
    }
}