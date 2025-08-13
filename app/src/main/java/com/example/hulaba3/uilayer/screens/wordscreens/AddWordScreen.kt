package com.example.hulaba3.uilayer.screens.wordscreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.utils.SpacedRepetitionHelper
import com.example.hulaba3.viewmodel.WordViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddWordScreen(
    wordViewModel: WordViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var word by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }

    fun saveWord() {
        if (word.isNotBlank() && meaning.isNotBlank()) {
            val reviewCount = 0
            val nextReviewTime = SpacedRepetitionHelper.getNextReviewTime(null, reviewCount) // Pass null for new word

            val newWord = Word(
                word = word,
                meaning = meaning,
                example = example,
                lastReviewed = null, // New word - not reviewed yet
                reviewCount = reviewCount,
                nextReviewTime = nextReviewTime
            )

            // FIXED: Use the method that schedules notifications
            wordViewModel.insertWordWithNotification(context, newWord)
            Toast.makeText(context, "Word saved and notification scheduled!", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        } else {
            Toast.makeText(context, "Please fill out Word and Meaning fields", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add New Word") }) },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(90.dp))

                TextField(
                    value = word,
                    onValueChange = { word = it },
                    label = { Text("Word") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("Meaning") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = example,
                    onValueChange = { example = it },
                    label = { Text("Example (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { saveWord() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Word")
                }
            }
        }
    )
}
