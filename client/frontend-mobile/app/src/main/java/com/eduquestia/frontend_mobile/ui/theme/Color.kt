package com.eduquestia.frontend_mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores EduQuest - Estilo Profesional Azul
val EduQuestBlue = Color(0xFF1E88E5) // Azul principal
val EduQuestDarkBlue = Color(0xFF0D47A1) // Azul oscuro (para degradados)
val EduQuestLightBlue = Color(0xFF64B5F6) // Azul claro (para degradados)
val EduQuestPurple = Color(0xFF1565C0) // Ahora es azul medio (reemplaza morado)

// Colores de acento
val AccentGreen = Color(0xFF43A047)
val AccentRed = Color(0xFFE53935)
val AccentOrange = Color(0xFFFFA726)
val AccentGold = Color(0xFFFFD700) // Dorado para monedas/XP

// ===== COLORES DINÁMICOS (cambian según el tema) =====

// Colores de texto - MODO CLARO (por defecto)
val TextPrimary = Color(0xFF1A1A2E) // Azul muy oscuro (casi negro)
val TextSecondary = Color(0xFF546E7A) // Gris azulado
val TextLight = Color(0xFF90A4AE)

// Colores de texto - MODO OSCURO
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFB3B3B3)
val TextLightDark = Color(0xFF757575)

// Colores de fondo - MODO CLARO (menos blanco, más cálido)
val BackgroundWhite = Color(0xFFFAFAFA) // Blanco suave
val BackgroundGray = Color(0xFFECEFF1) // Gris azulado claro

// Colores de fondo - MODO OSCURO
val BackgroundWhiteDark = Color(0xFF1E1E1E)
val BackgroundGrayDark = Color(0xFF121212)

// Colores legacy (para compatibilidad)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ===== FUNCIONES COMPOSABLE PARA COLORES DINÁMICOS =====

@Composable fun textPrimary(): Color = MaterialTheme.colorScheme.onBackground

@Composable fun textSecondary(): Color = MaterialTheme.colorScheme.onSurfaceVariant

@Composable fun backgroundSurface(): Color = MaterialTheme.colorScheme.surface

@Composable fun backgroundContainer(): Color = MaterialTheme.colorScheme.surfaceVariant
