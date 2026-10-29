package william.miranda.brutusfly.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Decorative icon colors, unrelated to income, expenses or status. */
@Immutable
data class BrutusIconColors(
    val background: Color,
    val content: Color
)

@Immutable
data class BrutusIconPalette(
    val neutral: BrutusIconColors,
    val blue: BrutusIconColors,
    val teal: BrutusIconColors,
    val amber: BrutusIconColors,
    val orange: BrutusIconColors,
    val purple: BrutusIconColors,
    val rose: BrutusIconColors
)

internal val LightIconPalette = BrutusIconPalette(
    neutral = BrutusIconColors(
        background = Color(color = 0xFFE5E6EC),
        content = Color(color = 0xFF535B69)
    ),
    blue = BrutusIconColors(
        background = Color(color = 0xFFE1EDFF),
        content = Color(color = 0xFF24569A)
    ),
    teal = BrutusIconColors(
        background = Color(color = 0xFFD9F2EE),
        content = Color(color = 0xFF14685E)
    ),
    amber = BrutusIconColors(
        background = Color(color = 0xFFFFEFC7),
        content = Color(color = 0xFF785600)
    ),
    orange = BrutusIconColors(
        background = Color(color = 0xFFFFE6D5),
        content = Color(color = 0xFF934514)
    ),
    purple = BrutusIconColors(
        background = Color(color = 0xFFECE4FF),
        content = Color(color = 0xFF65419B)
    ),
    rose = BrutusIconColors(
        background = Color(color = 0xFFFBE1EE),
        content = Color(color = 0xFF943D67)
    )
)

internal val DarkIconPalette = BrutusIconPalette(
    neutral = BrutusIconColors(
        background = Color(color = 0xFF33343B),
        content = Color(color = 0xFFC4C6D2)
    ),
    blue = BrutusIconColors(
        background = Color(color = 0xFF203753),
        content = Color(color = 0xFFA9CFFF)
    ),
    teal = BrutusIconColors(
        background = Color(color = 0xFF163D38),
        content = Color(color = 0xFF8CD9CD)
    ),
    amber = BrutusIconColors(
        background = Color(color = 0xFF44381B),
        content = Color(color = 0xFFF2D17E)
    ),
    orange = BrutusIconColors(
        background = Color(color = 0xFF4B3021),
        content = Color(color = 0xFFFFBB90)
    ),
    purple = BrutusIconColors(
        background = Color(color = 0xFF382B50),
        content = Color(color = 0xFFD5BAFF)
    ),
    rose = BrutusIconColors(
        background = Color(color = 0xFF4A293A),
        content = Color(color = 0xFFF5B4D3)
    )
)
