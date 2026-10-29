package william.miranda.brutusfly.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun textStyle(
    size: Int,
    height: Int,
    weight: FontWeight = FontWeight.Normal
) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontSize = size.sp,
    lineHeight = height.sp,
    fontWeight = weight
)

internal val BrutusTypography = Typography(
    displayLarge = textStyle(
        size = 57,
        height = 64
    ),
    displayMedium = textStyle(
        size = 45,
        height = 52
    ),
    displaySmall = textStyle(
        size = 36,
        height = 44
    ),
    headlineLarge = textStyle(
        size = 30,
        height = 36,
        weight = FontWeight.Bold
    ).copy(
        letterSpacing = (-0.8f).sp
    ),
    headlineMedium = textStyle(
        size = 28,
        height = 36,
        weight = FontWeight.Bold
    ),
    headlineSmall = textStyle(
        size = 24,
        height = 32,
        weight = FontWeight.Bold
    ),
    titleLarge = textStyle(
        size = 22,
        height = 28,
        weight = FontWeight.SemiBold
    ),
    titleMedium = textStyle(
        size = 16,
        height = 22,
        weight = FontWeight.Bold
    ),
    titleSmall = textStyle(
        size = 15,
        height = 20,
        weight = FontWeight.SemiBold
    ),
    bodyLarge = textStyle(
        size = 16,
        height = 24
    ),
    bodyMedium = textStyle(
        size = 15,
        height = 22
    ),
    bodySmall = textStyle(
        size = 13,
        height = 18
    ),
    labelLarge = textStyle(
        size = 14,
        height = 20,
        weight = FontWeight.Bold
    ),
    labelMedium = textStyle(
        size = 14,
        height = 20,
        weight = FontWeight.Medium
    ),
    labelSmall = textStyle(
        size = 12,
        height = 16,
        weight = FontWeight.Medium
    )
)
