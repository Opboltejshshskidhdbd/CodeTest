package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CyberIndigo,
    onPrimary = Color.White,
    primaryContainer = CyberIndigo.copy(alpha = 0.25f),
    onPrimaryContainer = CyberIndigoGlow,
    secondary = ElectricCyan,
    onSecondary = SpaceDark900,
    secondaryContainer = ElectricCyan.copy(alpha = 0.2f),
    onSecondaryContainer = CyanGlow,
    tertiary = AmethystViolet,
    onTertiary = Color.White,
    background = SpaceDark900,
    onBackground = TextPrimaryDark,
    surface = SpaceDark800,
    onSurface = TextPrimaryDark,
    surfaceVariant = SpaceDark700,
    onSurfaceVariant = TextSecondaryDark,
    outline = SpaceDark600,
    outlineVariant = GlassBorder,
  )

private val VibrantColorScheme =
  lightColorScheme(
    primary = VibrantBlue,
    onPrimary = Color.White,
    primaryContainer = VibrantBlueLight,
    onPrimaryContainer = VibrantBlue,
    secondary = VibrantIndigo,
    onSecondary = Color.White,
    secondaryContainer = VibrantBlueBadge,
    onSecondaryContainer = VibrantIndigo,
    tertiary = VibrantViolet,
    onTertiary = Color.White,
    background = VibrantBg,
    onBackground = VibrantTextDark,
    surface = VibrantSurface,
    onSurface = VibrantTextDark,
    surfaceVariant = VibrantBorder,
    onSurfaceVariant = VibrantTextMuted,
    outline = VibrantBorderActive,
    outlineVariant = VibrantBorder,
  )

private val LightColorScheme = VibrantColorScheme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Default to Vibrant Palette light theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> VibrantColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

