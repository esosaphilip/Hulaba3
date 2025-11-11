package com.example.hulaba3.uilayer.screens.learning

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hulaba3.uilayer.components.GlassCard
import com.example.hulaba3.uilayer.components.HulabaButton

@Composable
fun OnTheGoModeScreen(onStartLightning: () -> Unit, onStartAudioOnly: () -> Unit, onEnablePassive: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("🚌 Transit Mode", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("⏱️ Time Available: 15 minutes", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))
        GlassCard {
            Text("⚡ Word Lightning Round", style = MaterialTheme.typography.titleMedium)
            Text("10 words • 2 mins", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            HulabaButton(text = "Start Lightning Round", onClick = onStartLightning)
        }

        Spacer(Modifier.height(12.dp))
        GlassCard {
            Text("🎧 Audio-Only Review", style = MaterialTheme.typography.titleMedium)
            Text("Listen & repeat", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            HulabaButton(text = "Start Audio Review", onClick = onStartAudioOnly)
        }

        Spacer(Modifier.height(12.dp))
        GlassCard {
            Text("💭 Passive Learning", style = MaterialTheme.typography.titleMedium)
            Text("Notifications only", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            HulabaButton(text = "Enable Passive Mode", onClick = onEnablePassive)
        }

        Spacer(Modifier.height(12.dp))
        Text("Offline Mode: ✅ Active", style = MaterialTheme.typography.bodyMedium)
    }
}