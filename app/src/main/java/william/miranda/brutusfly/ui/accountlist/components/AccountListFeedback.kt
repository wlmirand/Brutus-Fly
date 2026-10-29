package william.miranda.brutusfly.ui.accountlist.components

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
import william.miranda.brutusfly.ui.accountlist.AccountListAction
import william.miranda.brutusfly.ui.accountlist.AccountListError
import william.miranda.brutusfly.ui.accountlist.AccountListState

@Composable
internal fun AccountListFailure(
    error: AccountListError,
    onRetry: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = BrutusTheme.spacing.medium
        )
    ) {
        BrutusErrorMessage(
            message = stringResource(
                id = when (error) {
                    AccountListError.Network -> R.string.accounts_error_network
                    AccountListError.Unauthorized -> R.string.accounts_error_unauthorized
                    AccountListError.Server -> R.string.accounts_error_server
                    AccountListError.Unknown -> R.string.accounts_error_unknown
                }
            )
        )
        BrutusButton(
            label = stringResource(
                id = R.string.accounts_retry
            ),
            onClick = onRetry
        )
    }
}

@Composable
internal fun AccountListFooter(
    state: AccountListState,
    onAction: (AccountListAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(
            vertical = BrutusTheme.spacing.large
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            state.isLoading || state.isLoadingMore -> BrutusLoading()
            state.loadMoreError != null -> AccountListFailure(
                error = state.loadMoreError
            ) {
                onAction(
                    AccountListAction.RetryNextPage
                )
            }
            state.error != null || !state.hasLoaded -> Unit
            state.nextPage != null -> Text(
                text = stringResource(
                    id = R.string.accounts_more
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            state.accounts.isNotEmpty() -> Text(
                text = stringResource(
                    id = R.string.accounts_end
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> Unit
        }
    }
}
