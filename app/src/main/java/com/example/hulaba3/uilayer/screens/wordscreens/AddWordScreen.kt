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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.utils.SpacedRepetitionHelper
import com.example.hulaba3.viewmodel.WordViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddWordScreen(
    wordViewModel: WordViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var word by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }

    fun saveWord() {
        if (word.isNotBlank() && meaning.isNotBlank()) {
            val currentTime = System.currentTimeMillis()
            val reviewCount = 0
            val nextReviewTime = SpacedRepetitionHelper.getNextReviewTime(currentTime, reviewCount)

            val newWord = Word(
                word = word,
                meaning = meaning,
                example = example,
                lastReviewed = currentTime, // Word added now
                reviewCount = reviewCount,
                nextReviewTime = nextReviewTime // First review interval
            )

            coroutineScope.launch {
                wordViewModel.insertWord(newWord)
                Toast.makeText(context, "Word saved!", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            }
        } else {
            Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
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
                    label = { Text("Example") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { saveWord() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Save Word")
                }
            }
        }
    )
}
