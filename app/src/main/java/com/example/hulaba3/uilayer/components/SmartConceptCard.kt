package com.example.hulaba3.uilayer.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hulaba3.data.database.Concept
import com.example.hulaba3.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SmartConceptCard(
    concept: Concept,
    conceptProgress: com.example.hulaba3.data.database.UserConceptProgress?,
    onAudioPlay: () -> Unit,
    onNoteAdd: (String) -> Unit,
    onDifficultyRating: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isRevealed: Boolean = false,
    onReveal: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(isRevealed) }
    var userNote by remember { mutableStateOf(conceptProgress?.notes ?: "") }
    var showNoteInput by remember { mutableStateOf(false) }
    val safeTitle = remember(concept.title) { concept.title.ifBlank { "this concept" } }
    val progressValue = remember(conceptProgress?.confidenceLevel) {
        ((conceptProgress?.confidenceLevel ?: 0).coerceIn(0, 10)) / 10f
    }
    val progressColor = remember(conceptProgress?.status) {
        when (conceptProgress?.status.orEmpty()) {
            "new" -> Color(0xFFE57373)
            "learning" -> Color(0xFFFFB74D)
            "reviewing" -> Color(0xFF81C784)
            "mastered" -> OceanTeal
            else -> OceanTeal
        }
    }
    
    val scale by animateFloatAsState(
        targetValue = if (showAnswer) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val cardBackground = Brush.verticalGradient(
        colors = listOf(
            SoftCream.copy(alpha = 0.9f),
            MistBlue.copy(alpha = 0.7f)
        )
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (showAnswer) 12.dp else 6.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(cardBackground)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column {
                // Header with progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Concept category chip-like label
                    if (concept.sourcePageNumber != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = CoralPink.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Concept ${concept.sourcePageNumber}",
                                color = CoralPink,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    // Progress indicator
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (conceptProgress != null) {
                            LinearProgressIndicator(
                                progress = { progressValue },
                                modifier = Modifier.width(60.dp).height(4.dp),
                                color = progressColor,
                                trackColor = RichCharcoal.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${(conceptProgress.confidenceLevel).coerceIn(0, 10)}/10",
                                style = MaterialTheme.typography.bodySmall,
                                color = RichCharcoal.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Main concept content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Question prompt
                    Text(
                        text = "What are ${safeTitle}?",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = RichCharcoal,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Answer reveal section
                    AnimatedVisibility(
                        visible = showAnswer,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HorizontalDivider(
                                color = RichCharcoal.copy(alpha = 0.2f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            
                            // Definition
                            if (!concept.definition.isNullOrEmpty()) {
                                Text(
                                    text = concept.definition ?: "",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = OceanTeal,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Use case
                            if (!concept.useCase.isNullOrEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = OceanTeal.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                tint = OceanTeal,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Use Case",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = OceanTeal
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = concept.useCase.orEmpty(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = RichCharcoal.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            
                            // Code example
                            if (!concept.codeExample.isNullOrEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = RichCharcoal.copy(alpha = 0.05f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Code,
                                                contentDescription = null,
                                                tint = RichCharcoal.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Code Example",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = RichCharcoal.copy(alpha = 0.7f)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = concept.codeExample ?: "",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                            ),
                                            color = RichCharcoal.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            
                            // German translation
                            if (!concept.germanTranslation.isNullOrEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = CoralPink.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Translate,
                                                contentDescription = null,
                                                tint = CoralPink,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "German Translation",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = CoralPink
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = concept.germanTranslation ?: "",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = RichCharcoal
                                        )
                                        if (!concept.germanDefinition.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = concept.germanDefinition ?: "",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = RichCharcoal.copy(alpha = 0.8f)
                                            )
                                        }
                                        if (!concept.germanUseCase.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = concept.germanUseCase ?: "",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                                ),
                                                color = RichCharcoal.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            
                            // Source reference
                            if (concept.sourcePageNumber != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Book,
                                        contentDescription = null,
                                        tint = RichCharcoal.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Source: Page ${concept.sourcePageNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RichCharcoal.copy(alpha = 0.6f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            // Difficulty rating buttons
                            Text(
                                text = "How well did you understand this?",
                                style = MaterialTheme.typography.bodySmall,
                                color = RichCharcoal.copy(alpha = 0.7f)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DifficultyRatingButton(
                                    text = "Again",
                                    color = Color(0xFFE57373),
                                    onClick = { onDifficultyRating(1) },
                                    modifier = Modifier.weight(1f)
                                )
                                DifficultyRatingButton(
                                    text = "Hard",
                                    color = Color(0xFFFFB74D),
                                    onClick = { onDifficultyRating(2) },
                                    modifier = Modifier.weight(1f)
                                )
                                DifficultyRatingButton(
                                    text = "Good",
                                    color = Color(0xFF81C784),
                                    onClick = { onDifficultyRating(3) },
                                    modifier = Modifier.weight(1f)
                                )
                                DifficultyRatingButton(
                                    text = "Easy",
                                    color = Color(0xFF4DB6AC),
                                    onClick = { onDifficultyRating(4) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    
                    // Reveal button
                    if (!showAnswer) {
                        Spacer(modifier = Modifier.height(24.dp))
                        HulabaButton(
                            text = "Tap to Reveal Answer",
                            onClick = { 
                                showAnswer = true
                                onReveal()
                            },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    }
                }
                
                // Notes and actions section
                if (showAnswer) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { isExpanded = !isExpanded }
                        ) {
                            Text(
                                text = if (isExpanded) "Hide Details" else "Show Details",
                                color = OceanTeal
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = OceanTeal
                            )
                        }
                        
                        Row {
                            IconButton(
                                onClick = { showNoteInput = !showNoteInput },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                                    contentDescription = "Add Note",
                                    tint = OceanTeal
                                )
                            }
                            
                            IconButton(
                                onClick = onAudioPlay,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Play Audio",
                                    tint = OceanTeal
                                )
                            }
                        }
                    }
                    
                    // Note input section
                    AnimatedVisibility(visible = showNoteInput) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = userNote,
                                onValueChange = { userNote = it },
                                label = { Text("Add your notes") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OceanTeal,
                                    unfocusedBorderColor = RichCharcoal.copy(alpha = 0.3f)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { 
                                        showNoteInput = false
                                        userNote = ""
                                    }
                                ) {
                                    Text("Cancel", color = RichCharcoal.copy(alpha = 0.7f))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { 
                                        onNoteAdd(userNote)
                                        showNoteInput = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = OceanTeal
                                    )
                                ) {
                                    Text("Save Note")
                                }
                            }
                        }
                    }
                    
                    // Expanded details
                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            HorizontalDivider(
                                color = RichCharcoal.copy(alpha = 0.2f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            
                            // Progress information
                            if (conceptProgress != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Progress",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = RichCharcoal
                                        )
                                        Text(
                                            text = "Status: ${conceptProgress.status}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = RichCharcoal.copy(alpha = 0.7f)
                                        )
                                    }
                                    
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Reviews: ${conceptProgress.reviewCount}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = RichCharcoal.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = "Time: ${conceptProgress.timeSpentMinutes} min",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = RichCharcoal.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                
                                if (userNote.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MistBlue.copy(alpha = 0.2f)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Note,
                                                    contentDescription = null,
                                                    tint = OceanTeal,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Your Notes",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = OceanTeal
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = userNote,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = RichCharcoal.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}