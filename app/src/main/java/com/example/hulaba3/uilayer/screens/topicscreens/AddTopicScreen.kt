package com.example.hulaba3.uilayer.screens.topicscreens

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// PDF selection removed; Topic entity has no pdfUri
import com.example.hulaba3.data.database.Topic
import com.example.hulaba3.viewmodel.TopicViewModel
// ViewModel is passed in from MainScreen; no Koin here
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopicScreen(
    topicViewModel: TopicViewModel,
    onNavigateBack: () -> Unit,
    topicToEdit: Topic? = null
) {
    var title by remember { mutableStateOf(topicToEdit?.title ?: "") }
    val context = LocalContext.current

    // Selected PDF state
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPdfName by remember { mutableStateOf<String?>(null) }

    fun queryDisplayName(uri: Uri?): String? {
        if (uri == null) return null
        return try {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && it.moveToFirst()) it.getString(nameIndex) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                // Persist read permission for future use
                try {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) { }

                selectedPdfUri = it
                selectedPdfName = queryDisplayName(it) ?: "Selected PDF"
            }
        }
    )

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

            // Attach PDF section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Attach PDF (optional)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedPdfName ?: "No file selected",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF374151)
                    )

                    Button(
                        onClick = { pdfPicker.launch(arrayOf("application/pdf")) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text("Choose PDF", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button - matching Figma blue button style
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        val newTopicId = topicToEdit?.id ?: UUID.randomUUID().toString()
                        if (topicToEdit != null) {
                            topicViewModel.updateTopic(
                                topicToEdit.copy(
                                    title = title
                                )
                            )
                        } else {
                            topicViewModel.insertTopic(
                                context,
                                Topic(
                                    id = newTopicId,
                                    title = title
                                )
                            )
                        }

                        // Attach selected PDF as StudyMaterial
                        selectedPdfUri?.let { uri ->
                            topicViewModel.attachPdfToTopic(
                                topicId = newTopicId,
                                pdfUri = uri,
                                title = selectedPdfName ?: "Study PDF",
                                makePrimary = true
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