package com.example.hulaba3.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// Brand-aligned color schemes
private val DarkColorScheme = darkColorScheme(
    primary = DeepOceanBlue,
    onPrimary = Color.White,
    secondary = VibrantCoral,
    onSecondary = Color.White,
    tertiary = SoftGold,
    onTertiary = RichCharcoal,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFE5E7EB)
)

private val LightColorScheme = lightColorScheme(
    primary = DeepOceanBlue,
    onPrimary = Color.White,
    secondary = VibrantCoral,
    onSecondary = Color.White,
    tertiary = SoftGold,
    onTertiary = RichCharcoal,
    background = SoftCream,
    onBackground = RichCharcoal,
    surface = PureWhite,
    onSurface = RichCharcoal

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun Hulaba3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color to preserve branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}