package com.example.hulaba3.uilayer.screens.learning

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.utils.SpacedRepetitionHelper
import com.example.hulaba3.viewmodel.WordViewModel
import com.example.hulaba3.uilayer.components.GlassCard
import com.example.hulaba3.uilayer.components.HulabaButton
import com.example.hulaba3.uilayer.components.RatingButton
import com.example.hulaba3.ui.theme.RichCharcoal
import java.util.Locale

enum class Difficulty { Easy, Good, Hard }

@Composable
fun SmartWordCardScreen(
    navController: NavController,
    wordId: Long? = null,
    wordViewModel: WordViewModel
) {
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var currentWord by remember { mutableStateOf<Word?>(null) }
    val haptics = LocalHapticFeedback.current

    val context = LocalContext.current
    DisposableEffect(context) {
        var localTts: TextToSpeech? = null
        localTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                localTts?.language = Locale.GERMAN
            }
        }
        tts = localTts
        onDispose {
            localTts?.stop()
            localTts?.shutdown()
        }
    }

    LaunchedEffect(wordId) {
        if (wordId != null) {
            currentWord = wordViewModel.getWordById(wordId)
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "smart_word")
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable { navController.popBackStack() }
            )
            Spacer(Modifier.width(8.dp))
            Text("[2/10]", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.weight(1f))
            Text("🔊", modifier = Modifier.clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                speak(currentWord?.germanWord ?: "Fernlicht")
            })
        }

        Spacer(Modifier.height(20.dp))
        Text(
            currentWord?.germanWord ?: "Fernlicht",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp)
        )
        Spacer(Modifier.height(8.dp))
        Text(currentWord?.englishTranslation ?: "High beam / main beam", style = MaterialTheme.typography.titleMedium, color = RichCharcoal)

        Spacer(Modifier.height(16.dp))
        Text("📝 Example:", style = MaterialTheme.typography.labelMedium)
        Text(
            currentWord?.exampleSentenceEnglish ?: "\"Schalte das Fernlicht aus, es blendet den Gegenverkehr.\"",
            style = MaterialTheme.typography.bodyLarge,
            color = RichCharcoal.copy(alpha = 0.8f)
        )

        Spacer(Modifier.height(16.dp))
        Text("🎯 Context: Driving vocabulary", style = MaterialTheme.typography.bodyMedium, color = RichCharcoal.copy(alpha = 0.7f))

        Spacer(Modifier.height(16.dp))
        HulabaButton(text = "🎤 Practice Saying It") {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            speak(currentWord?.germanWord ?: "Fernlicht")
        }

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RatingButton("😊 Easy") {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                currentWord?.let {
                    wordViewModel.rateWord(context, it, SpacedRepetitionHelper.ReviewQuality.EASY)
                    navController.popBackStack()
                }
            }
            RatingButton("🤔 Good") {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                currentWord?.let {
                    wordViewModel.rateWord(context, it, SpacedRepetitionHelper.ReviewQuality.GOOD)
                    navController.popBackStack()
                }
            }
            RatingButton("😓 Hard") {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                currentWord?.let {
                    wordViewModel.rateWord(context, it, SpacedRepetitionHelper.ReviewQuality.HARD)
                    navController.popBackStack()
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        GlassCard {
            Text("Visual Memory Aid", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .pointerInput(currentWord) {
                        var totalDrag = Offset.Zero
                        detectDragGestures(
                            onDragStart = { totalDrag = Offset.Zero },
                            onDrag = { change, dragAmount ->
                                totalDrag += dragAmount
                                change.consume()
                            },
                            onDragEnd = {
                                val threshold = 60f
                                val dx = totalDrag.x
                                val dy = totalDrag.y
                                when {
                                    kotlin.math.abs(dx) > kotlin.math.abs(dy) && dx > threshold -> {
                                        // Swipe right = Easy
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        currentWord?.let {
                                            wordViewModel.rateWord(context, it, SpacedRepetitionHelper.ReviewQuality.EASY)
                                            navController.popBackStack()
                                        }
                                    }
                                    kotlin.math.abs(dx) > kotlin.math.abs(dy) && dx < -threshold -> {
                                        // Swipe left = Hard
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        currentWord?.let {
                                            wordViewModel.rateWord(context, it, SpacedRepetitionHelper.ReviewQuality.HARD)
                                            navController.popBackStack()
                                        }
                                    }
                                    dy < -threshold -> {
                                        // Swipe up = Favorite (no-op; Word no longer has isFavorite)
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    dy > threshold -> {
                                        // Swipe down = Skip
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navController.popBackStack()
                                    }
                                }
                            }
                        )
                    }
                    .pointerInput(currentWord) {
                        detectTapGestures(
                            onDoubleTap = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                speak(currentWord?.germanWord ?: "Fernlicht")
                            },
                            onLongPress = {
                                val id = currentWord?.id
                                if (id != null) {
                                    navController.navigate("editWord/$id")
                                }
                            }
                        )
                    }
                ,
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEEF2F7)
            ) {
                Box(Modifier.fillMaxSize()) {
                    Text("AI-generated image of car headlights", modifier = Modifier.align(Alignment.Center), color = RichCharcoal.copy(alpha = 0.7f))
                }
            }
        }
    }
}