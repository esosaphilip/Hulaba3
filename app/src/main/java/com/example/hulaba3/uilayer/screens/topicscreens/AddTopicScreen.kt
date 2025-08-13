package com.example.hulaba3.uilayer.screens.topicscreens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    topicToEdit: Topic? = null  // Optional parameter to edit an existing topic
) {
    var title by remember { mutableStateOf(topicToEdit?.title ?: "") }
    var pdfUri by remember { mutableStateOf<Uri?>(topicToEdit?.pdfUri?.toUri()) }
    val context = LocalContext.current

    // Activity result launcher for picking a PDF
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            pdfUri = it
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (topicToEdit != null) "Edit Topic" else "Add Topic") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Topic Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { pdfPickerLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select PDF")
            }

            pdfUri?.let {
                Text("Selected PDF: $it", style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = {
                    if (title.isNotEmpty() && pdfUri != null) {
                        if (topicToEdit != null) {
                            // Update the topic if it's being edited
                            val updatedTopic = topicToEdit.copy(
                                title = title,
                                pdfUri = pdfUri.toString(),
                                lastReviewed = System.currentTimeMillis(),
                                nextReviewTime = System.currentTimeMillis() + 86_400_000
                            )
                            topicViewModel.updateTopic(updatedTopic)
                        } else {
                            // Add new topic if it's not being edited
                            val newTopic = Topic(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                pdfUri = pdfUri.toString(),
                                lastReviewed = System.currentTimeMillis(),
                                nextReviewTime = System.currentTimeMillis() + 86_400_000
                            )
                            topicViewModel.insertTopic(context, newTopic)
                        }
                        onNavigateBack() // Go back after saving or updating
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (topicToEdit != null) "Update Topic" else "Save Topic")
            }
        }
    }
}
