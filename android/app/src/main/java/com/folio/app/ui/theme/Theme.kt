package com.folio.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Calm, bookish palette — warm oatmeal, sage, clay terracotta, dusty lavender.
val Oatmeal = Color(0xFFF6F2EA)
val Cream = Color(0xFFFDFBF6)
val Sage = Color(0xFF7E9276)
val SageDeep = Color(0xFF5E7259)
val SageSoft = Color(0xFFE2E8DC)
val Clay = Color(0xFFC8825F)
val ClaySoft = Color(0xFFF2E0D3)
val Lavender = Color(0xFF9A8FB8)
val LavenderSoft = Color(0xFFE8E3F0)
val Gold = Color(0xFFD9A441)
val Ink = Color(0xFF33302A)
val InkSoft = Color(0xFF6E675C)
val InkFaint = Color(0xFFA39A8C)

// The neural map is always deep near-black, in both themes (per spec).
val MapBackground = Color(0xFF0D0D14)

private val LightColors = lightColorScheme(
    primary = Sage,
    onPrimary = Color.White,
    primaryContainer = SageSoft,
    onPrimaryContainer = SageDeep,
    secondary = Clay,
    onSecondary = Color.White,
    secondaryContainer = ClaySoft,
    onSecondaryContainer = Color(0xFF7A4A30),
    tertiary = Lavender,
    onTertiary = Color.White,
    tertiaryContainer = LavenderSoft,
    onTertiaryContainer = Color(0xFF55487A),
    background = Oatmeal,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = Color(0xFFF3EDE2),
    onSurfaceVariant = InkSoft,
    outline = Color(0xFFE5DDCE),
    outlineVariant = Color(0xFFEDE6D8),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF94A98B),
    onPrimary = Color(0xFF1C241A),
    primaryContainer = Color(0xFF262E24),
    onPrimaryContainer = Color(0xFFAFC2A6),
    secondary = Color(0xFFD6976F),
    onSecondary = Color(0xFF2A1C12),
    secondaryContainer = Color(0xFF33271F),
    onSecondaryContainer = Color(0xFFE8B998),
    tertiary = Color(0xFFA99EC9),
    onTertiary = Color(0xFF1F1A2E),
    tertiaryContainer = Color(0xFF272334),
    onTertiaryContainer = Color(0xFFC8BFE0),
    background = Color(0xFF15141B),
    onBackground = Color(0xFFECE7DD),
    surface = Color(0xFF1F1E27),
    onSurface = Color(0xFFECE7DD),
    surfaceVariant = Color(0xFF262430),
    onSurfaceVariant = Color(0xFFA8A199),
    outline = Color(0xFF2E2C38),
    outlineVariant = Color(0xFF2A2833),
)

// Serif for book titles, sans-serif for UI (per spec).
val BookSerif = FontFamily.Serif

val FolioTypography = Typography(
    headlineMedium = TextStyle(fontFamily = BookSerif, fontWeight = FontWeight.SemiBold, fontSize = 26.sp),
    headlineSmall = TextStyle(fontFamily = BookSerif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleLarge = TextStyle(fontFamily = BookSerif, fontWeight = FontWeight.SemiBold, fontSize = 19.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.2.sp),
)

@Composable
fun FolioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FolioTypography,
        content = content
    )
}
