package com.example.myapplication12345678.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Глобальное состояние темы
object ThemeState {
    var isDarkTheme by mutableStateOf(true)
}

private val ModernDarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnDark,
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = OnDark,
    secondary = SecondaryBlue,
    onSecondary = OnAccent,
    secondaryContainer = DarkSurfaceLight,
    onSecondaryContainer = OnDark,
    tertiary = AccentPink,
    onTertiary = OnAccent,
    tertiaryContainer = DarkSurfaceLight,
    onTertiaryContainer = OnDark,
    background = DarkBackground,
    onBackground = OnDark,
    surface = DarkSurface,
    onSurface = OnDark,
    surfaceVariant = DarkSurfaceLight,
    onSurfaceVariant = OnDarkSecondary,
    outline = OnDarkTertiary
)

private val ModernLightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnDark,
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = OnDark,
    secondary = SecondaryBlue,
    onSecondary = OnLight,
    secondaryContainer = LightSurfaceLight,
    onSecondaryContainer = OnLight,
    tertiary = AccentPink,
    onTertiary = OnLight,
    tertiaryContainer = LightSurfaceLight,
    onTertiaryContainer = OnLight,
    background = LightBackground,
    onBackground = OnLight,
    surface = LightSurface,
    onSurface = OnLight,
    surfaceVariant = LightSurfaceLight,
    onSurfaceVariant = OnLightSecondary,
    outline = OnLightTertiary
)

@Composable
fun MyApplication12345678Theme(
    darkTheme: Boolean = ThemeState.isDarkTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ModernDarkColorScheme else ModernLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}