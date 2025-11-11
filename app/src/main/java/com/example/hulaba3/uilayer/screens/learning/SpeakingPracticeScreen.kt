package com.example.hulaba3.uilayer.screens.learning

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.hulaba3.uilayer.components.HulabaButton

@Composable
fun SpeakingPracticeScreen() {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    // Expected phrase for scenario (can be extended to multiple prompts)
    val expectedPhrase = remember { "Guten Tag! Was möchten Sie bestellen?" }
    var score by remember { mutableStateOf<Int?>(null) }
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startListening(speechRecognizer)
        } else {
            errorText = "Microphone permission denied"
            isListening = false
        }
    }
    val scale by animateFloatAsState(targetValue = if (isListening) 1.05f else 1f)

    DisposableEffect(speechRecognizer) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() { isListening = true }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onError(error: Int) {
                errorText = "Recognition error: $error"
                isListening = false
            }
            override fun onResults(results: Bundle?) {
                val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                recognizedText = texts?.firstOrNull().orEmpty()
                score = computePronunciationScore(recognizedText, expectedPhrase)
                isListening = false
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val texts = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                recognizedText = texts?.firstOrNull().orEmpty()
                score = computePronunciationScore(recognizedText, expectedPhrase)
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        onDispose {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
        }
    }

    fun handleMicToggle() {
        if (!isListening) {
            val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                startListening(speechRecognizer)
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        } else {
            speechRecognizer.stopListening()
            isListening = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {
        Text("🎤 Speaking Practice", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Scenario: At the Restaurant", style = MaterialTheme.typography.titleSmall)

        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("🤖 AI Conversation", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("\"Guten Tag! Was möchten Sie bestellen?\"", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(16.dp))
        HulabaButton(text = if (isListening) "Listening… Tap to Stop" else "🎙️ Tap to Respond") {
            handleMicToggle()
        }

        Spacer(Modifier.height(16.dp))
        Card(shape = CircleShape, colors = CardDefaults.cardColors(containerColor = Color(0xFFF6AD55))) {
            Box(Modifier.size(72.dp).scale(scale), contentAlignment = Alignment.Center) {
                Text("🎙️")
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Suggested responses:", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        Text("• Ich hätte gerne…\n• Können Sie empfehlen…\n• Was ist die Spezialität…", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))
        if (recognizedText.isNotEmpty()) {
            Text("You said: $recognizedText", style = MaterialTheme.typography.bodyMedium)
        }
        if (errorText != null) {
            Text("⚠️ ${errorText}", style = MaterialTheme.typography.bodySmall, color = Color.Red)
        }
        if (score != null) {
            Spacer(Modifier.height(8.dp))
            Text("📊 Pronunciation Score: ${score}%", style = MaterialTheme.typography.bodyMedium)
        }
    }

}

private fun startListening(speechRecognizer: SpeechRecognizer) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.GERMAN.toString())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Sprich jetzt")
    }
    speechRecognizer.startListening(intent)
}

// Lightweight on-device scoring: normalized Levenshtein similarity (%).
// This is a proxy for pronunciation quality when using text recognition results.
private fun computePronunciationScore(recognized: String, expected: String): Int {
    val r = recognized.lowercase().trim()
    val e = expected.lowercase().trim()
    if (r.isEmpty() || e.isEmpty()) return 0
    val dist = levenshteinDistance(r, e)
    val maxLen = maxOf(r.length, e.length)
    val similarity = (1.0 - (dist.toDouble() / maxLen.toDouble())).coerceIn(0.0, 1.0)
    return (similarity * 100).toInt()
}

private fun levenshteinDistance(a: String, b: String): Int {
    val dp = Array(a.length + 1) { IntArray(b.length + 1) }
    for (i in 0..a.length) dp[i][0] = i
    for (j in 0..b.length) dp[0][j] = j
    for (i in 1..a.length) {
        for (j in 1..b.length) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,      // deletion
                dp[i][j - 1] + 1,      // insertion
                dp[i - 1][j - 1] + cost // substitution
            )
        }
    }
    return dp[a.length][b.length]
}