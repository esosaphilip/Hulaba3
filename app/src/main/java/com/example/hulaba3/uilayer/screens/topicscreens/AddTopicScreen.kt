package com.example.hulaba3.uilayer.screens.topicscreens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.hulaba3.data.database.Topic
import com.example.hulaba3.viewmodel.TopicViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopicScreen(
    topicViewModel: TopicViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    topicToEdit: Topic? = null
) {
    var title by remember { mutableStateOf(topicToEdit?.title ?: "") }
    var pdfUri by remember { mutableStateOf<Uri?>(topicToEdit?.pdfUri?.toUri()) }
    val context = LocalContext.current

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { pdfUri = it } }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            // Custom Top Bar matching Figma design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6)
                            )
                        ),
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = if (topicToEdit != null) "Edit Study Note" else "Add Study Note",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Empty space for centering
                    Box(modifier = Modifier.size(40.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Topic Input Field - matching Figma underline style
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = {
                    Text(
                        "Enter Topic",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF6B7280)
                        )
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedLabelColor = Color(0xFF6366F1),
                    unfocusedLabelColor = Color(0xFF6B7280),
                    cursorColor = Color(0xFF6366F1)
                )
            )

            // Upload PDF Button - matching Figma blue button style
            Button(
                onClick = { pdfPickerLauncher.launch("application/pdf") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    "Upload PDF",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }

            // Selected PDF indicator
            pdfUri?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F4F6))
                        .border(
                            1.dp,
                            Color(0xFFE5E7EB),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "✓ PDF Selected: ${it.lastPathSegment ?: "Document"}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button - matching Figma blue button style
            Button(
                onClick = {
                    if (title.isNotEmpty() && pdfUri != null) {
                        if (topicToEdit != null) {
                            topicViewModel.updateTopic(
                                topicToEdit.copy(
                                    title = title,
                                    pdfUri = pdfUri.toString(),
                                    lastReviewed = System.currentTimeMillis(),
                                    nextReviewTime = System.currentTimeMillis() + 86_400_000
                                )
                            )
                        } else {
                            topicViewModel.insertTopic(
                                context,
                                Topic(
                                    id = UUID.randomUUID().toString(),
                                    title = title,
                                    pdfUri = pdfUri.toString(),
                                    lastReviewed = System.currentTimeMillis(),
                                    nextReviewTime = System.currentTimeMillis() + 86_400_000
                                )
                            )
                        }
                        onNavigateBack()
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    if (topicToEdit != null) "Update Note" else "Save Note",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}