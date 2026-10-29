package william.miranda.brutusfly.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import william.miranda.brutusfly.core.designsystem.R
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreview
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreviews
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme

@Composable
fun BrutusLoading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = BrutusTheme.spacing.large
        )
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(R.string.brutus_loading),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@BrutusPreviews
@Composable
private fun BrutusLoadingPreview() {
    BrutusPreview {
        BrutusLoading(
            modifier = Modifier.fillMaxWidth()
        )
    }
}
