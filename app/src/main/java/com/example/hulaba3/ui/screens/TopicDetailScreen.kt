package com.example.hulaba3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.hulaba3.data.database.LearningConcept
import com.example.hulaba3.data.database.LearningTopic
import com.example.hulaba3.viewmodel.TopicDetailViewModel
import com.example.hulaba3.viewmodel.TopicDetailState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    topicId: Long,
    viewModel: TopicDetailViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onStartLearning: (Long, Long) -> Unit,
    onNavigateToMixedLearning: (Long) -> Unit
) {
    LaunchedEffect(topicId) {
        viewModel.loadTopicDetails(topicId)
    }
    
    val topicState by viewModel.topicState.collectAsState()
    
    Scaffold(
        topBar = {
            TopicDetailTopBar(
                topic = topicState.topic,
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            if (topicState.topic != null) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToMixedLearning(topicId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = "Mixed Learning")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mixed Learning")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                topicState.isLoading && topicState.topic == null -> {
                    LoadingScreen()
                }
                topicState.error != null -> {
                    ErrorScreen(
                        message = topicState.error!!,
                        onRetry = { viewModel.loadTopicDetails(topicId) }
                    )
                }
                else -> {
                    topicState.topic?.let { topic ->
                        TopicDetailContent(
                            topic = topic,
                            concepts = topicState.concepts,
                            progress = topicState.progress,
                            onStartLearning = onStartLearning
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailTopBar(
    topic: LearningTopic?,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            topic?.let {
                Column {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${it.totalConcepts} concepts • ${it.masteredConcepts} mastered",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun TopicDetailContent(
    topic: LearningTopic,
    concepts: List<LearningConcept>,
    progress: TopicDetailState.TopicProgress,
    onStartLearning: (Long, Long) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Topic Overview Card
        TopicOverviewCard(topic, progress)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress Statistics
        TopicProgressStats(progress)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Concepts Section
        ConceptsSection(
            topicId = topic.id,
            concepts = concepts,
            onStartLearning = onStartLearning
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Learning Recommendations
        LearningRecommendations(topic)
    }
}

@Composable
fun TopicOverviewCard(
    topic: LearningTopic,
    progress: TopicDetailState.TopicProgress
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Topic Title
            Text(
                text = topic.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Overall Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${topic.masteredConcepts}/${topic.totalConcepts}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Concepts Mastered",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                
                val progressPercentage = if (topic.totalConcepts > 0) {
                    (topic.masteredConcepts.toFloat() / topic.totalConcepts * 100).toInt()
                } else 0
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$progressPercentage%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = if (topic.totalConcepts > 0) {
                    topic.masteredConcepts.toFloat() / topic.totalConcepts
                } else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Difficulty display removed: LearningTopic does not include difficulty level
        }
    }
}

@Composable
fun TopicProgressStats(
    progress: TopicDetailState.TopicProgress
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Progress Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Mastered Concepts
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Mastered",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = progress.masteredConcepts.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mastered",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Learning Concepts
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = "Learning",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = progress.learningConcepts.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Learning",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // New Concepts
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(
                            Icons.Default.NewReleases,
                            contentDescription = "New",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = progress.newConcepts.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "New",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Learning Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "${progress.streakDays} day learning streak",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ConceptsSection(
    topicId: Long,
    concepts: List<LearningConcept>,
    onStartLearning: (Long, Long) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Concepts (${concepts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            TextButton(onClick = { /* TODO: Show all concepts */ }) {
                Text("See All")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (concepts.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Default.School,
                title = "No concepts yet",
                message = "Concepts will appear here as you add them to this topic."
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                concepts.take(5).forEach { concept ->
                    ConceptCard(
                        concept = concept,
                        onStartLearning = { onStartLearning(topicId, concept.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ConceptCard(
    concept: LearningConcept,
    onStartLearning: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Column {
                    Text(
                        text = concept.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "Type: ${concept.type} • Mastery: ${(concept.masteryLevel * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // LearningConcept does not include description; omitted
                
                // Progress bar removed: Concept entity does not include mastery state
            }
            
            FilledTonalButton(
                onClick = onStartLearning,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Learn")
            }
        }
    }
}

@Composable
fun LearningRecommendations(
    topic: LearningTopic
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = "Recommendation",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Learning Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val progressRatio = if (topic.totalConcepts > 0) topic.masteredConcepts.toFloat() / topic.totalConcepts else 0f
            val recommendations = when {
                progressRatio < 0.33f -> listOf(
                    "Start with basic concepts to build a strong foundation",
                    "Practice regularly with short sessions",
                    "Use visual aids and examples to understand concepts"
                )
                progressRatio < 0.66f -> listOf(
                    "Focus on concepts you find challenging",
                    "Review previously learned material regularly",
                    "Try to connect new concepts with what you already know"
                )
                else -> listOf(
                    "Challenge yourself with advanced concepts",
                    "Apply knowledge to real-world scenarios",
                    "Teach concepts to others to reinforce learning"
                )
            }
            
            recommendations.forEach { recommendation ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Tip",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}