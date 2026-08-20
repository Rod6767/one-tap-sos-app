package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SleekDarkColorScheme = darkColorScheme(
    primary = SleekAccent,
    onPrimary = SleekOnAccent,
    primaryContainer = SleekAccentContainer,
    onPrimaryContainer = SleekAccent,
    secondary = EmergencyAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = SleekNavIndicatorText,
    tertiary = EmergencyGreen,
    onTertiary = Color.Black,
    background = SleekBackground,
    onBackground = SleekTextPrimary,
    surface = SleekSurface,
    onSurface = SleekTextPrimary,
    surfaceVariant = SleekSurfaceVariant,
    onSurfaceVariant = SleekTextSecondary,
    outline = SleekBorder,
    error = SosRedBright,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SleekDarkColorScheme,
        typography = Typography,
        content = content
    )
}

