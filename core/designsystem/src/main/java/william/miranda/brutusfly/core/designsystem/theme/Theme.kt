package william.miranda.brutusfly.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalSemanticColors = staticCompositionLocalOf { LightSemanticColors }
private val LocalIconPalette = staticCompositionLocalOf { LightIconPalette }
private val LocalSpacing = staticCompositionLocalOf { BrutusSpacing() }

/** MaterialTheme provides colors, typography and shapes; this object provides app-specific tokens. */
object BrutusTheme {
    val iconPalette: BrutusIconPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalIconPalette.current

    val colors: BrutusSemanticColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSemanticColors.current

    val spacing: BrutusSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current
}

@Composable
fun BrutusFlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    spacing: BrutusSpacing = BrutusSpacing(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSemanticColors provides if (darkTheme) DarkSemanticColors else LightSemanticColors,
        LocalIconPalette provides if (darkTheme) DarkIconPalette else LightIconPalette,
        LocalSpacing provides spacing
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = BrutusTypography,
            shapes = BrutusShapes,
            content = content
        )
    }
}
