package william.miranda.brutusfly.core.designsystem.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreview
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreviews
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme

@Composable
fun BrutusButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(size = 20.dp),
                color = LocalContentColor.current,
                strokeWidth = 2.dp
            )
            Spacer(
                modifier = Modifier.width(
                    width = BrutusTheme.spacing.small
                )
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@BrutusPreviews
@Composable
private fun BrutusButtonPreview() {
    BrutusPreview {
        BrutusButton(
            label = "Save",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
        BrutusButton(
            label = "Disabled",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        BrutusButton(
            label = "Saving…",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            isLoading = true
        )
    }
}
