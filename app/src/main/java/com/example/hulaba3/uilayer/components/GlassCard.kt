package com.example.hulaba3.uilayer.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.hulaba3.ui.theme.PureWhite
import com.example.hulaba3.ui.theme.LightShadow

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val glassColor = PureWhite.copy(alpha = 0.80f)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(glassColor)
            .then(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Modifier.blur(20.dp) else Modifier)
            .drawBehind {
                val gradient = Brush.verticalGradient(
                    colors = listOf(PureWhite.copy(alpha = 0.35f), PureWhite.copy(alpha = 0.10f), PureWhite.copy(alpha = 0f)),
                    startY = 0f,
                    endY = size.height
                )
                drawRect(brush = gradient)
            }
            .padding(16.dp)
    ) {
        Column(content = content)
    }
}