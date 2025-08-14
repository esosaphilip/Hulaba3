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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    topicViewModel: TopicViewModel = koinViewModel(),
    navController: NavController
) {
    val topics by topicViewModel.allTopics.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
        containerColor = Color(0xFFF5F9FF)
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
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topics) { topic ->
                    TopicItem(
                        topic,
                        onDelete = { topicViewModel.deleteTopic(it) },
                        onClick = { selectedTopic ->
                            selectedTopic.pdfUri?.let { pdfUri ->
                                coroutineScope.launch {
                                    openPdfViewer(context = context, pdfUri = pdfUri.toUri())
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TopicItem(topic: Topic, onClick: (Topic) -> Unit, onDelete: (Topic) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val topicViewModel: TopicViewModel = koinViewModel()

    val nextReviewDate = remember {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = topic.nextReviewTime
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.time)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
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
                    Text("Next Review: $nextReviewDate", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF25D366))
                    topic.pdfUri?.let {
                        Text("PDF Path: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Button(
                        onClick = { onClick(topic) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Text("Open PDF", color = Color.White)
                    }
                    Button(
                        onClick = {
                            val updatedTopic = topic.copy(
                                lastReviewed = System.currentTimeMillis(),
                                nextReviewTime = System.currentTimeMillis() + 86_400_000
                            )
                            topicViewModel.updateTopic(updatedTopic)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Mark as Reviewed", color = Color.White)
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
