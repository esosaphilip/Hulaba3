package com.example.hulaba3.uilayer.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SmartWordCard(
    word: Word,
    onAudioPlay: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDifficultyRating: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isRevealed: Boolean = false,
    onReveal: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showTranslation by remember { mutableStateOf(isRevealed) }
    // Favorite flag not present on Word entity; omit favorite state
    // (No state variable needed here)
    
    val scale by animateFloatAsState(
        targetValue = if (showTranslation) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
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
                elevation = if (showTranslation) 12.dp else 6.dp,
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
                // Header with category and difficulty
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = OceanTeal.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Level: ${word.difficultyLevel}",
                            color = OceanTeal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Main word content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // German word
                    Text(
                        text = word.germanWord,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = RichCharcoal
                    )
                    
                    // Part of speech indicator
                    if (!word.partOfSpeech.isNullOrEmpty()) {
                        Text(
                            text = word.partOfSpeech ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = RichCharcoal.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Translation reveal section
                    AnimatedVisibility(
                        visible = showTranslation,
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
                            
                            // English translation
                            Text(
                                text = word.englishTranslation,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = OceanTeal,
                                textAlign = TextAlign.Center
                            )
                            
                            // Example sentence (English)
                            if (!word.exampleSentenceEnglish.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "\"${word.exampleSentenceEnglish ?: ""}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = RichCharcoal.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }

                            // Example sentence (German)
                            if (!word.exampleSentenceGerman.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = word.exampleSentenceGerman ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = RichCharcoal.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Difficulty rating buttons
                            Text(
                                text = "How well did you know this?",
                                style = MaterialTheme.typography.bodySmall,
                                color = RichCharcoal.copy(alpha = 0.7f)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
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
                    if (!showTranslation) {
                        Spacer(modifier = Modifier.height(24.dp))
                        HulabaButton(
                            text = "Tap to Reveal",
                            onClick = { 
                                showTranslation = true
                                onReveal()
                            },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    }
                }
                
                // Expandable details section
                if (showTranslation) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
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
                    }
                    
                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            HorizontalDivider(
                                color = RichCharcoal.copy(alpha = 0.2f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            
                            // Additional details removed (Word entity does not contain notes)
                            
                            // Metadata (aligned to actual Word entity fields)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Level: ${word.difficultyLevel}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RichCharcoal.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "Part of speech: ${word.partOfSpeech ?: "—"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RichCharcoal.copy(alpha = 0.7f)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Gender: ${word.gender ?: "—"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RichCharcoal.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "Frequency: ${word.frequencyRank?.toString() ?: "—"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RichCharcoal.copy(alpha = 0.7f)
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

@Composable
fun DifficultyRatingButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}