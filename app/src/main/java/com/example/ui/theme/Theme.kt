package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlueLight,
    onPrimary = Color(0xFF002E69),
    primaryContainer = Color(0xFF00438E),
    onPrimaryContainer = ElectricBlueContainer,
    secondary = TealAccent,
    onSecondary = Color.White,
    tertiary = PurpleAI,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Color(0xFFE2E2E9),
    surface = DarkSurface,
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC4C6CF),
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = ElectricBlueContainer,
    onPrimaryContainer = ElectricBlueOnContainer,
    secondary = TealAccent,
    onSecondary = Color.White,
    tertiary = PurpleAI,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

@Composable
fun TalentryTheme(
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

