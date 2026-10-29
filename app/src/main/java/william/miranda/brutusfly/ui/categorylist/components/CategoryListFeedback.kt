package william.miranda.brutusfly.ui.categorylist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import william.miranda.brutusfly.R
import william.miranda.brutusfly.core.designsystem.components.BrutusButton
import william.miranda.brutusfly.core.designsystem.components.BrutusErrorMessage
import william.miranda.brutusfly.core.designsystem.components.BrutusLoading
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.ui.categorylist.CategoryListAction
import william.miranda.brutusfly.ui.categorylist.CategoryListError
import william.miranda.brutusfly.ui.categorylist.CategoryListState

@Composable
internal fun CategoryListFailure(
    error: CategoryListError,
    onRetry: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.medium)
    ) {
        BrutusErrorMessage(
            message = stringResource(
                id = when (error) {
                    CategoryListError.Network -> R.string.categories_error_network
                    CategoryListError.Unauthorized -> R.string.categories_error_unauthorized
                    CategoryListError.Server -> R.string.categories_error_server
                    CategoryListError.Unknown -> R.string.categories_error_unknown
                }
            )
        )
        BrutusButton(
            label = stringResource(id = R.string.configuration_retry),
            onClick = onRetry
        )
    }
}

@Composable
internal fun CategoryListFooter(
    state: CategoryListState,
    onAction: (CategoryListAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = BrutusTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            state.isLoading || state.isLoadingMore -> BrutusLoading()
            state.loadMoreError != null -> CategoryListFailure(
                error = state.loadMoreError,
                onRetry = { onAction(CategoryListAction.RetryNextPage) }
            )
            state.error != null || !state.hasLoaded -> Unit
            state.nextPage != null -> Text(
                text = stringResource(id = R.string.categories_more),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            state.categories.isNotEmpty() -> Text(
                text = stringResource(id = R.string.categories_end),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> Unit
        }
    }
}
