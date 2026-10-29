package william.miranda.brutusfly.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class BrutusSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val huge: Dp = 40.dp,
    val screenHorizontal: Dp = 16.dp
)
