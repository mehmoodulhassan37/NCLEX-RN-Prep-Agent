package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
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
    primary = TealPrimary,
    secondary = TealLight,
    tertiary = TealDark,
    background = PurpleDarkBackground,
    surface = SurfaceDark,
    surfaceVariant = CardDark,
    onPrimary = PurpleDarkBackground,
    onBackground = TextLight,
    onSurface = TextLight,
    error = ErrorRed
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TealDark,
    secondary = TealPrimary,
    tertiary = TealLight,
    background = Color(0xFFF0F4F8),
    surface = Color.White,
    surfaceVariant = Color(0xFFE2E8F0),
    onPrimary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark,
    error = Color(0xFFD32F2F)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color disabled to ensure our custom aesthetic is perfectly preserved
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
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
