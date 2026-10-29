package william.miranda.brutusfly.ui.transactionlist.components

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
import william.miranda.brutusfly.ui.transactionlist.TransactionListAction
import william.miranda.brutusfly.ui.transactionlist.TransactionListError
import william.miranda.brutusfly.ui.transactionlist.TransactionListState

@Composable
internal fun TransactionPaginationFooter(
    state: TransactionListState,
    onAction: (TransactionListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(
            vertical = BrutusTheme.spacing.large
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = BrutusTheme.spacing.medium
        )
    ) {
        when {
            state.selectionReadError -> Unit
            !state.hasLoadedSelection -> BrutusLoading()
            state.selectedAccountId == null -> Unit
            state.isLoading || state.isLoadingMore -> BrutusLoading()
            state.loadMoreError != null -> {
                BrutusErrorMessage(
                    message = transactionErrorMessage(
                        error = state.loadMoreError
                    )
                )
                BrutusButton(
                    label = stringResource(
                        id = R.string.transactions_retry_page
                    ),
                    onClick = {
                        onAction(
                            TransactionListAction.RetryNextPage
                        )
                    }
                )
            }
            state.error != null || !state.hasLoaded -> Unit
            state.nextPage != null -> Text(
                text = stringResource(
                    id = R.string.transactions_more_available
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            state.groups.isNotEmpty() -> Text(
                text = stringResource(
                    id = R.string.transactions_end
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> Unit
        }
    }
}

@Composable
internal fun transactionErrorMessage(
    error: TransactionListError
): String = stringResource(
    id = when (error) {
        TransactionListError.AccountUnavailable -> R.string.transactions_account_unavailable
        TransactionListError.AccountNotSelectable -> R.string.transactions_account_not_selectable
        TransactionListError.Network -> R.string.transactions_error_network
        TransactionListError.Unauthorized -> R.string.transactions_error_unauthorized
        TransactionListError.Server -> R.string.transactions_error_server
        TransactionListError.Unknown -> R.string.transactions_error_unknown
    }
)
