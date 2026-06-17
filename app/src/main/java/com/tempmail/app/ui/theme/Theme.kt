package com.tempmail.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = BackgroundLight,
    secondary = Secondary,
    background = SurfaceLight,
    surface = BackgroundLight,
    error = ErrorColor,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = DividerColor
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = BackgroundLight,
    secondary = Secondary,
    background = SurfaceDark,
    surface = CardDark,
    error = ErrorColor,
    onBackground = BackgroundLight,
    onSurface = BackgroundLight,
    outline = DividerColor
)

@Composable
fun TempMailTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
