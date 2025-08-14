package com.example.hulaba3.uilayer.screens.wordscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.viewmodel.WordViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Define colors matching Figma design
private val GreenPrimary = Color(0xFF4CAF50)
private val GreenSecondary = Color(0xFF66BB6A)
private val GreenLight = Color(0xFFE8F5E8)
private val BackgroundGray = Color(0xFFF5F5F5)
private val CardBackground = Color.White
private val TextPrimary = Color(0xFF212121)
private val TextSecondary = Color(0xFF757575)
private val RedAccent = Color(0xFFF44336)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListScreen(
    wordViewModel: WordViewModel = koinViewModel(),
    navController: NavController
) {
    val words by wordViewModel.allWords.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Word Pool",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                },
                actions = {
                    FloatingActionButton(
                        onClick = { navController.navigate("addWord") },
                        modifier = Modifier.size(40.dp),
                        containerColor = GreenPrimary,
                        contentColor = Color.White
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Word",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundGray,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Header section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Newly Learned Words",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            "${words.size} words in your collection",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Words list
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(words) { word ->
                        WordItem(
                            word = word,
                            onDelete = { wordViewModel.deleteWord(it) },
                            onReviewed = { wordViewModel.updateLastReviewed(context, it) },
                            navController = navController
                        )
                    }

                    // Add bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun WordItem(
    word: Word,
    onDelete: (Word) -> Unit,
    onReviewed: (Word) -> Unit,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Word status indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Word text
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = word.word,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Review count: ${word.reviewCount}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Action buttons
                Row {
                    IconButton(
                        onClick = { navController.navigate("editWord/${word.id}") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onDelete(word) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RedAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Expanded content
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    // Divider
                    Divider(
                        color = Color(0xFFE0E0E0),
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Meaning section
                    Text(
                        text = "Meaning/Definitions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = word.meaning,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Example section (if exists)
                    if (word.example.isNotBlank()) {
                        Text(
                            text = "Example",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = word.example,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Review info
                    val nextReviewDate = remember {
                        derivedStateOf {
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = word.nextReviewTime
                            SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(calendar.time)
                        }
                    }.value

                    Text(
                        text = "Next Review",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = nextReviewDate,
                        fontSize = 14.sp,
                        color = GreenPrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Action button
                    Button(
                        onClick = { onReviewed(word) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Mark as Reviewed",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}