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
import com.example.data.model.AppVisualTheme

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

private val SepiaColorScheme =
  lightColorScheme(
    primary = Color(0xFFD97706),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFFB45309),
    secondary = Color(0xFFB45309),
    onSecondary = Color.White,
    background = Color(0xFFFAF4EB),
    onBackground = Color(0xFF3D2E1E),
    surface = Color(0xFFFFFDF9),
    onSurface = Color(0xFF3D2E1E),
    surfaceVariant = Color(0xFFEAE0D0),
    onSurfaceVariant = Color(0xFF786551),
    outline = Color(0xFFD5C7B2),
    outlineVariant = Color(0xFFEAE0D0)
  )

private val AuroraColorScheme =
  darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color.White,
    primaryContainer = Color(0x3310B981),
    onPrimaryContainer = Color(0xFF6EE7B7),
    secondary = Color(0xFF06B6D4),
    onSecondary = Color.White,
    background = Color(0xFF041C15),
    onBackground = Color(0xFFECFDF5),
    surface = Color(0xFF0B3026),
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = Color(0xFF134E3F),
    onSurfaceVariant = Color(0xFF6EE7B7),
    outline = Color(0xFF1D705C),
    outlineVariant = Color(0xFF134E3F)
  )

private val RoseColorScheme =
  lightColorScheme(
    primary = Color(0xFFEC4899),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFCE7F3),
    onPrimaryContainer = Color(0xFFBE185D),
    secondary = Color(0xFFF43F5E),
    onSecondary = Color.White,
    background = Color(0xFFFFF5F7),
    onBackground = Color(0xFF28101E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF28101E),
    surfaceVariant = Color(0xFFFCE7F3),
    onSurfaceVariant = Color(0xFF835A6E),
    outline = Color(0xFFFBCFE8),
    outlineVariant = Color(0xFFFCE7F3)
  )

@Composable
fun MyApplicationTheme(
  appVisualTheme: AppVisualTheme = AppVisualTheme.VIBRANT_LIGHT,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (appVisualTheme.isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    appVisualTheme == AppVisualTheme.MIDNIGHT_OLED -> DarkColorScheme
    appVisualTheme == AppVisualTheme.WARM_SEPIA -> SepiaColorScheme
    appVisualTheme == AppVisualTheme.AURORA_MINT -> AuroraColorScheme
    appVisualTheme == AppVisualTheme.SUNSET_ROSE -> RoseColorScheme
    else -> VibrantColorScheme
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}


