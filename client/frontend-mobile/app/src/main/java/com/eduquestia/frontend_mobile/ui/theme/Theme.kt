package com.eduquestia.frontend_mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de colores oscuro para EduQuest
private val DarkColorScheme =
        darkColorScheme(
                primary = EduQuestBlue,
                secondary = EduQuestPurple,
                tertiary = AccentGreen,
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                surfaceVariant = Color(0xFF2C2C2E),
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = Color.White,
                onSurface = Color.White,
                onSurfaceVariant = Color(0xFFB3B3B3)
        )

// Esquema de colores claro para EduQuest
private val LightColorScheme =
        lightColorScheme(
                primary = EduQuestBlue,
                secondary = EduQuestPurple,
                tertiary = AccentGreen,
                background = BackgroundGray,
                surface = BackgroundWhite,
                surfaceVariant = BackgroundGray,
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = TextPrimary,
                onSurface = TextPrimary,
                onSurfaceVariant = TextSecondary
        )

@Composable
fun FrontendmobileTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
