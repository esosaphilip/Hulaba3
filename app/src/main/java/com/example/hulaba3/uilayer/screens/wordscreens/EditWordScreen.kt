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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.viewmodel.WordViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditWordScreen(
    wordViewModel: WordViewModel = koinViewModel(),
    wordId: Long?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var word by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var originalWord by remember { mutableStateOf<Word?>(null) }

    // Fetch the word to be edited
    LaunchedEffect(wordId) {
        wordId?.let {
            val existingWord = wordViewModel.getWordById(it)
            existingWord?.let { fetchedWord ->
                originalWord = fetchedWord
                word = fetchedWord.word
                meaning = fetchedWord.meaning
                example = fetchedWord.example
            }
        }
    }

    fun updateWord() {
        if (word.isNotBlank() && meaning.isNotBlank() && originalWord != null) {
            val updatedWord = originalWord!!.copy(
                word = word,
                meaning = meaning,
                example = example
                // Keep original lastReviewed, reviewCount, and nextReviewTime
            )

            coroutineScope.launch {
                wordViewModel.updateWord(updatedWord)
                Toast.makeText(context, "Word updated successfully!", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            }
        } else {
            Toast.makeText(context, "Please fill out Word and Meaning fields", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Edit Word") }) },
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
                    onClick = { updateWord() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Word")
                }
            }
        }
    )
}