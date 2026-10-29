package william.miranda.brutusfly.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreview
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreviews
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme

@Composable
fun BrutusCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(
                all = BrutusTheme.spacing.large
            ),
            content = content
        )
    }
}

@BrutusPreviews
@Composable
private fun BrutusCardPreview() {
    BrutusPreview {
        BrutusCard {
            Text(
                text = "Card title",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Shared surface, shape and padding.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
