package com.example.hulaba3.uilayer.screens.wordscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.viewmodel.WordViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListScreen(
    wordViewModel: WordViewModel = koinViewModel(),
    navController: NavController
) {
    val words by wordViewModel.allWords.collectAsState()
    val context = LocalContext.current // Get context here

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Words List",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            shadow = Shadow(offset = Offset(2f, 2f), blurRadius = 5f, color = Color.Gray)
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF25D366)
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("addWord") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Word", tint = Color.White)
                    }
                },
                modifier = Modifier.shadow(8.dp, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            )
        },
        containerColor = Color(0xFFF5F9FF),
        content = { padding ->
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.padding(16.dp)
            ) {
                items(words) { word ->
                    WordItem(
                        word = word,
                        onDelete = { wordViewModel.deleteWord(it) },
                        onReviewed = { wordViewModel.updateLastReviewed(context, it) }, // FIXED: Pass context
                        navController = navController
                    )
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
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = Color(0xFF25D366)
                    )
                }

                IconButton(onClick = { onDelete(word) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }

                IconButton(onClick = { navController.navigate("editWord/${word.id}") }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF25D366))
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("Meaning: ${word.meaning}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    if (word.example.isNotBlank()) {
                        Text("Example: ${word.example}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }

                    val nextReviewDate = remember {
                        derivedStateOf {
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = word.nextReviewTime
                            SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(calendar.time)
                        }
                    }.value

                    Text("Next Review: $nextReviewDate", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF25D366))
                    Text("Review Count: ${word.reviewCount}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onReviewed(word) }, // This now properly reschedules notifications
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Text("Mark as Reviewed", color = Color.Black)
                    }
                }
            }
        }
    }
}