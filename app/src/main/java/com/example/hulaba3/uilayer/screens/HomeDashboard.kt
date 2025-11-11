package com.example.hulaba3.uilayer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hulaba3.uilayer.components.GlassCard
import com.example.hulaba3.uilayer.components.HulabaButton
import com.example.hulaba3.uilayer.components.StreakChip
import com.example.hulaba3.uilayer.components.ProgressBarStylized
import com.example.hulaba3.uilayer.components.CollectionCard
import com.example.hulaba3.ui.theme.SoftCream
import com.example.hulaba3.ui.theme.MistBlue
import com.example.hulaba3.ui.theme.RichCharcoal

@Composable
fun HomeDashboard(
    streakDays: Int = 12,
    progress: Float = 0.8f,
    onStartQuickLearning: () -> Unit,
    onOpenSpeakingPractice: () -> Unit,
    onOpenInsights: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SoftCream, MistBlue)
                )
            )
            .padding(20.dp)
    ) {
        Text("Guten Morgen, Learner!", style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp))
        Spacer(Modifier.height(8.dp))
        Row {
            StreakChip(streakDays)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Today's Progress", style = MaterialTheme.typography.labelMedium, color = RichCharcoal.copy(alpha = 0.8f))
                ProgressBarStylized(progress)
            }
        }

        Spacer(Modifier.height(20.dp))
        GlassCard {
            Text("🎯 Quick Learning (5 min)", style = MaterialTheme.typography.titleMedium)
            Text("3 words • 2 topics ready", style = MaterialTheme.typography.bodyMedium, color = RichCharcoal.copy(alpha = 0.7f))
            Spacer(Modifier.height(12.dp))
            HulabaButton(text = "START NOW →", onClick = onStartQuickLearning)
        }

        Spacer(Modifier.height(20.dp))
        Text("📚 Your Collections:", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CollectionCard(title = "German", countLabel = "68 W", mastery = 4)
            CollectionCard(title = "AI", countLabel = "24 T", mastery = 3)
            CollectionCard(title = "Code", countLabel = "15 T", mastery = 2)
        }

        Spacer(Modifier.height(20.dp))
        Text("🎤 Speaking Practice", style = MaterialTheme.typography.titleSmall)
        HulabaButton(text = "Practice Conversation →", onClick = onOpenSpeakingPractice)

        Spacer(Modifier.height(20.dp))
        Text("📊 Weekly Insights", style = MaterialTheme.typography.titleSmall)
        HulabaButton(text = "View Analytics", onClick = onOpenInsights)
    }
}