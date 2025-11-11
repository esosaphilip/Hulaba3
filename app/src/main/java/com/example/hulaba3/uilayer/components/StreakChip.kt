package com.example.hulaba3.uilayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hulaba3.ui.theme.SoftGold
import com.example.hulaba3.ui.theme.PureWhite
import com.example.hulaba3.ui.theme.RichCharcoal

@Composable
fun StreakChip(days: Int, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    listOf(SoftGold.copy(alpha = 0.8f), SoftGold.copy(alpha = 0.6f))
                ),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text("🔥 $days Days", color = RichCharcoal)
    }
}