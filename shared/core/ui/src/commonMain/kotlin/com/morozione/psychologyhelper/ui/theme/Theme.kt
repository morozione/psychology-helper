package com.morozione.psychologyhelper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryWhite,
    secondary = SecondaryTeal,
    onSecondary = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    error = ErrorRed,
    onError = OnPrimaryWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreenDark,
    onPrimary = OnPrimaryWhite,
    secondary = SecondaryTealDark,
    onSecondary = TextPrimaryDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    error = ErrorRed,
    onError = OnPrimaryWhite
)

@Composable
fun PsychologyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalDimensions provides Dimensions()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PsychologyTypography,
            content = content
        )
    }
}
