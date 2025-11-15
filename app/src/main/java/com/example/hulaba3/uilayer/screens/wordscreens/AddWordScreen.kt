package com.example.hulaba3.uilayer.screens.wordscreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hulaba3.data.database.Word
// SpacedRepetition is handled via NotificationScheduler at ViewModel layer
import com.example.hulaba3.viewmodel.WordViewModel
// Koin not used here; ViewModel is passed from caller

// Define colors matching Figma design
private val GreenPrimary = Color(0xFF4CAF50)
private val GreenSecondary = Color(0xFF66BB6A)
private val BackgroundGray = Color(0xFFF5F5F5)
private val CardBackground = Color.White
private val TextPrimary = Color(0xFF212121)
private val TextSecondary = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddWordScreen(
    wordViewModel: WordViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var germanWord by remember { mutableStateOf("") }
    var englishTranslation by remember { mutableStateOf("") }
    var exampleEnglish by remember { mutableStateOf("") }

    fun saveWord() {
        if (germanWord.isNotBlank() && englishTranslation.isNotBlank()) {
            val newWord = Word(
                germanWord = germanWord,
                englishTranslation = englishTranslation,
                exampleSentenceEnglish = exampleEnglish
            )

            wordViewModel.insertWordWithNotification(context, newWord)
            Toast.makeText(context, "Word saved and notification scheduled!", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        } else {
            Toast.makeText(context, "Please fill out German and English fields", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Word",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundGray,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                // Input Fields Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Word Input
                        Text(
                            text = "German Word",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = germanWord,
                            onValueChange = { germanWord = it },
                            placeholder = { Text("Enter German word here", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                cursorColor = GreenPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Meaning Input
                        Text(
                            text = "English Translation",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = englishTranslation,
                            onValueChange = { englishTranslation = it },
                            placeholder = { Text("Enter English translation", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                cursorColor = GreenPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Example Input
                        Text(
                            text = "Example (English)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = exampleEnglish,
                            onValueChange = { exampleEnglish = it },
                            placeholder = { Text("Enter example sentence (optional)", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                cursorColor = GreenPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = { saveWord() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        "Save",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    )
}