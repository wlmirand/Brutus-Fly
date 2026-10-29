package william.miranda.brutusfly.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorContrastTest {
    @Test
    fun materialTextColorsHaveReadableContrastInBothThemes() {
        listOf(LightColorScheme, DarkColorScheme).forEach { colors ->
            assertReadable(colors.onBackground, colors.background)
            assertReadable(colors.onSurface, colors.surface)
            assertReadable(colors.onSurfaceVariant, colors.surface)
            assertReadable(colors.onSurfaceVariant, colors.surfaceContainerHighest)
            assertReadable(colors.onPrimary, colors.primary)
            assertReadable(colors.onPrimaryContainer, colors.primaryContainer)
            assertReadable(colors.onSecondary, colors.secondary)
            assertReadable(colors.onSecondaryContainer, colors.secondaryContainer)
            assertReadable(colors.onTertiary, colors.tertiary)
            assertReadable(colors.onTertiaryContainer, colors.tertiaryContainer)
            assertReadable(colors.onError, colors.error)
            assertReadable(colors.onErrorContainer, colors.errorContainer)
        }
    }

    @Test
    fun semanticColorsHaveReadableContrastInBothThemes() {
        listOf(
            LightSemanticColors to LightColorScheme,
            DarkSemanticColors to DarkColorScheme,
        ).forEach { (semantic, material) ->
            assertReadable(semantic.income, material.surface)
            assertReadable(semantic.expense, material.surface)
            assertReadable(semantic.income, semantic.incomeContainer)
            assertReadable(semantic.expense, semantic.expenseContainer)
            assertReadable(semantic.warning, semantic.warningContainer)
            assertReadable(semantic.accent, semantic.accentContainer)
        }
    }

    private fun assertReadable(foreground: Color, background: Color) {
        val foregroundLuminance = foreground.luminance()
        val backgroundLuminance = background.luminance()
        val contrast = (maxOf(foregroundLuminance, backgroundLuminance) + 0.05f) /
            (minOf(foregroundLuminance, backgroundLuminance) + 0.05f)

        assertTrue("Contrast $contrast is below 4.5:1 for $foreground on $background", contrast >= 4.5f)
    }
}
