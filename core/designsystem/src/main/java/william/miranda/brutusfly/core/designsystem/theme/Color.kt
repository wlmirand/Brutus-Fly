package william.miranda.brutusfly.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

internal val LightColorScheme = lightColorScheme(
    primary = Color(color = 0xFF443CB8),
    onPrimary = Color.White,
    primaryContainer = Color(color = 0xFFE4E0FF),
    onPrimaryContainer = Color(color = 0xFF19105E),
    secondary = Color(color = 0xFF5D5D75),
    onSecondary = Color.White,
    secondaryContainer = Color(color = 0xFFE3E0F9),
    onSecondaryContainer = Color(color = 0xFF1A1A2E),
    tertiary = Color(color = 0xFF79536B),
    onTertiary = Color.White,
    tertiaryContainer = Color(color = 0xFFFFD8EC),
    onTertiaryContainer = Color(color = 0xFF2F1126),
    background = Color(color = 0xFFF2F3F7),
    onBackground = Color(color = 0xFF0A0B0F),
    surface = Color(color = 0xFFFBFBFC),
    onSurface = Color(color = 0xFF0A0B0F),
    surfaceVariant = Color(color = 0xFFE5E6EC),
    onSurfaceVariant = Color(color = 0xFF535B69),
    surfaceDim = Color(color = 0xFFDAD9E1),
    surfaceBright = Color(color = 0xFFFBFBFC),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(color = 0xFFF5F5F9),
    surfaceContainer = Color(color = 0xFFEEEEF4),
    surfaceContainerHigh = Color(color = 0xFFE8E8EF),
    surfaceContainerHighest = Color(color = 0xFFE2E2E9),
    surfaceTint = Color(color = 0xFF443CB8),
    outline = Color(color = 0xFF747C8B),
    outlineVariant = Color(color = 0xFFE1E2E8),
    error = Color(color = 0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(color = 0xFFFFDAD6),
    onErrorContainer = Color(color = 0xFF410002),
    inverseSurface = Color(color = 0xFF303038),
    inverseOnSurface = Color(color = 0xFFF2F0F7),
    inversePrimary = Color(color = 0xFFC6BFFF),
    scrim = Color.Black
)

internal val DarkColorScheme = darkColorScheme(
    primary = Color(color = 0xFFC6BFFF),
    onPrimary = Color(color = 0xFF292078),
    primaryContainer = Color(color = 0xFF40379F),
    onPrimaryContainer = Color(color = 0xFFE4E0FF),
    secondary = Color(color = 0xFFC7C4DD),
    onSecondary = Color(color = 0xFF2F2E43),
    secondaryContainer = Color(color = 0xFF454459),
    onSecondaryContainer = Color(color = 0xFFE3E0F9),
    tertiary = Color(color = 0xFFE9B9D3),
    onTertiary = Color(color = 0xFF47263B),
    tertiaryContainer = Color(color = 0xFF603C53),
    onTertiaryContainer = Color(color = 0xFFFFD8EC),
    background = Color(color = 0xFF121318),
    onBackground = Color(color = 0xFFE5E5ED),
    surface = Color(color = 0xFF191A20),
    onSurface = Color(color = 0xFFE5E5ED),
    surfaceVariant = Color(color = 0xFF444650),
    onSurfaceVariant = Color(color = 0xFFC4C6D2),
    surfaceDim = Color(color = 0xFF121318),
    surfaceBright = Color(color = 0xFF383940),
    surfaceContainerLowest = Color(color = 0xFF0D0E13),
    surfaceContainerLow = Color(color = 0xFF191A20),
    surfaceContainer = Color(color = 0xFF1E1F25),
    surfaceContainerHigh = Color(color = 0xFF282930),
    surfaceContainerHighest = Color(color = 0xFF33343B),
    surfaceTint = Color(color = 0xFFC6BFFF),
    outline = Color(color = 0xFF8E909D),
    outlineVariant = Color(color = 0xFF444650),
    error = Color(color = 0xFFFFB4AB),
    onError = Color(color = 0xFF690005),
    errorContainer = Color(color = 0xFF93000A),
    onErrorContainer = Color(color = 0xFFFFDAD6),
    inverseSurface = Color(color = 0xFFE5E5ED),
    inverseOnSurface = Color(color = 0xFF303038),
    inversePrimary = Color(color = 0xFF443CB8),
    scrim = Color.Black
)

/** Semantic colors not represented by Material's color scheme. */
@Immutable
data class BrutusSemanticColors(
    val income: Color,
    val incomeContainer: Color,
    val expense: Color,
    val expenseContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val accent: Color,
    val accentContainer: Color
)

internal val LightSemanticColors = BrutusSemanticColors(
    income = Color(color = 0xFF0F7752),
    incomeContainer = Color(color = 0xFFE0F3EA),
    expense = Color(color = 0xFFBA1A1A),
    expenseContainer = Color(color = 0xFFF9DEDC),
    warning = Color(color = 0xFF994500),
    warningContainer = Color(color = 0xFFF8E8DA),
    accent = Color(color = 0xFF6337CC),
    accentContainer = Color(color = 0xFFEEE9FE)
)

internal val DarkSemanticColors = BrutusSemanticColors(
    income = Color(color = 0xFF78DBAD),
    incomeContainer = Color(color = 0xFF123E2C),
    expense = Color(color = 0xFFFFB4AB),
    expenseContainer = Color(color = 0xFF5C211D),
    warning = Color(color = 0xFFFFB77D),
    warningContainer = Color(color = 0xFF512C10),
    accent = Color(color = 0xFFD0BCFF),
    accentContainer = Color(color = 0xFF392365)
)
