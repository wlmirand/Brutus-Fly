package william.miranda.brutusfly.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreview
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreviews
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme

@Composable
fun BrutusScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        if (navigationIcon != null) {
            navigationIcon()
            Spacer(
                modifier = Modifier.width(BrutusTheme.spacing.medium)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(BrutusTheme.spacing.xs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        actions()
    }
}

@BrutusPreviews
@Composable
private fun BrutusScreenHeaderPreview() {
    BrutusPreview {
        BrutusScreenHeader(
            title = "Accounts",
            subtitle = "Your connected accounts."
        )
        BrutusScreenHeader(
            title = "Screen title"
        )
    }
}
