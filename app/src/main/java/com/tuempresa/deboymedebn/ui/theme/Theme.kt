package com.tuempresa.deboymedebn.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// ------------------------- Paleta Oscura -------------------------
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark
)

// ------------------------- Paleta Clara -------------------------
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight
)

// ------------------------- Tipografía -------------------------
val AppTypography = Typography(
    displayLarge = Typography().displayLarge,
    displayMedium = Typography().displayMedium,
    displaySmall = Typography().displaySmall,
    headlineLarge = Typography().headlineLarge,
    headlineMedium = Typography().headlineMedium,
    headlineSmall = Typography().headlineSmall,
    titleLarge = Typography().titleLarge,
    titleMedium = Typography().titleMedium,
    titleSmall = Typography().titleSmall,
    bodyLarge = Typography().bodyLarge,
    bodyMedium = Typography().bodyMedium,
    bodySmall = Typography().bodySmall,
    labelLarge = Typography().labelLarge,
    labelMedium = Typography().labelMedium,
    labelSmall = Typography().labelSmall
)

// ------------------------- Tema Principal -------------------------
@Composable
fun DeboyMedebnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // ❗ Desactivado para evitar problemas en diálogos
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
