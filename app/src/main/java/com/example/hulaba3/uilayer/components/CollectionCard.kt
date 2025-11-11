package com.example.hulaba3.uilayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hulaba3.ui.theme.PureWhite
import com.example.hulaba3.ui.theme.RichCharcoal

@Composable
fun CollectionCard(
    title: String,
    countLabel: String,
    mastery: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PureWhite
    ) {
        Column(
            modifier = modifier
                .size(100.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = RichCharcoal)
            Text(countLabel, style = MaterialTheme.typography.labelMedium, color = RichCharcoal.copy(alpha = 0.7f))
            Spacer(Modifier.height(8.dp))
            // mastery dots ● ● ● ● ○
            val total = 5
            Box(Modifier.clip(RoundedCornerShape(8.dp))) {
                Column {
                    val filled = mastery.coerceIn(0, total)
                    val dotFilled = RichCharcoal
                    val dotEmpty = RichCharcoal.copy(alpha = 0.2f)
                    Text(
                        (1..total).joinToString(" ") { if (it <= filled) "●" else "○" },
                        color = dotFilled,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}