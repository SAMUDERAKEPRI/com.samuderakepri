package com.samuderakepri.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OceanCyanBright,
    onPrimary = MaritimeNavyDark,
    primaryContainer = MaritimeNavy,
    onPrimaryContainer = Color.White,
    secondary = MaritimeGold,
    onSecondary = MaritimeNavyDark,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkOnSurface,
    tertiary = InvestigativeCrimson,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = MaritimeNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4F5),
    onPrimaryContainer = MaritimeNavy,
    secondary = OceanCyan,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightOnSurface,
    tertiary = InvestigativeCrimson,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

@Composable
fun SamuderaKepriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
