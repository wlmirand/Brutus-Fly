package william.miranda.brutusfly.ui.common.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import william.miranda.brutusfly.core.designsystem.theme.BrutusIconColors
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.domain.model.category.CategoryIcon

/** Decorative tones assigned to catalog groups, not to user-defined categories or transaction types. */
internal enum class CategoryIconTone {
    NEUTRAL,
    BLUE,
    TEAL,
    AMBER,
    ORANGE,
    PURPLE,
    ROSE
}

/** Stable decoration shared by all category surfaces; the default label stays neutral. */
@Composable
@ReadOnlyComposable
internal fun CategoryIcon.colors(): BrutusIconColors {
    val palette = BrutusTheme.iconPalette
    val tone = if (this == CategoryIcon.LABEL) CategoryIconTone.NEUTRAL
    else CategoryIconCatalog.toneFor(icon = this)
    return when (tone) {
        CategoryIconTone.NEUTRAL -> palette.neutral
        CategoryIconTone.BLUE -> palette.blue
        CategoryIconTone.TEAL -> palette.teal
        CategoryIconTone.AMBER -> palette.amber
        CategoryIconTone.ORANGE -> palette.orange
        CategoryIconTone.PURPLE -> palette.purple
        CategoryIconTone.ROSE -> palette.rose
    }
}
