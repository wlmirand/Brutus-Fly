package william.miranda.brutusfly.ui.accountlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.distinctUntilChanged
import william.miranda.brutusfly.R
import william.miranda.brutusfly.core.designsystem.components.BrutusScreenHeader
import william.miranda.brutusfly.core.designsystem.components.BrutusButton
import william.miranda.brutusfly.core.designsystem.components.BrutusErrorMessage
import william.miranda.brutusfly.core.designsystem.components.BrutusLoading
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.ui.accountlist.components.AccountCard
import william.miranda.brutusfly.ui.accountlist.components.AccountListFailure
import william.miranda.brutusfly.ui.accountlist.components.AccountListFooter
import william.miranda.brutusfly.ui.accountlist.model.sampleAccounts

@Composable
internal fun AccountListScreen(
    state: AccountListState,
    onAction: (AccountListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val currentOnAction by rememberUpdatedState(
        newValue = onAction
    )
    val footerKey = "accounts-pagination-${state.nextPage}"
    LaunchedEffect(
        listState,
        state.nextPage,
        state.hasLoaded,
        state.isLoading,
        state.isLoadingMore,
        state.error,
        state.loadMoreError
    ) {
        if (!state.hasLoaded || state.nextPage == null || state.isLoading || state.isLoadingMore ||
            state.error != null || state.loadMoreError != null
        ) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.any { it.key == footerKey } }
            .distinctUntilChanged()
            .collect { visible ->
                if (visible) currentOnAction(
                    AccountListAction.LoadNextPage
                )
            }
    }

    Column(
        modifier = modifier.fillMaxSize().background(
            color = MaterialTheme.colorScheme.background
        )
    ) {
        BrutusScreenHeader(
            title = stringResource(
                id = R.string.accounts_title
            ),
            subtitle = stringResource(
                id = R.string.accounts_subtitle
            ),
            modifier = Modifier.padding(
                horizontal = BrutusTheme.spacing.screenHorizontal,
                vertical = BrutusTheme.spacing.large
            ),
            actions = {
                IconButton(
                    onClick = {
                        onAction(
                            AccountListAction.Refresh
                        )
                    },
                    enabled = !state.isLoading && !state.isLoadingMore,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = stringResource(
                            id = R.string.accounts_refresh
                        )
                    )
                }
            }
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(
                weight = 1f
            ).selectableGroup(),
            contentPadding = PaddingValues(
                start = BrutusTheme.spacing.screenHorizontal,
                end = BrutusTheme.spacing.screenHorizontal,
                top = BrutusTheme.spacing.small,
                bottom = BrutusTheme.spacing.extraLarge
            ),
            verticalArrangement = Arrangement.spacedBy(
                space = BrutusTheme.spacing.medium
            )
        ) {
            if (state.selectionError != null) {
                item(
                    key = "accounts-selection-error"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            space = BrutusTheme.spacing.medium
                        )
                    ) {
                        BrutusErrorMessage(
                            message = stringResource(
                                id = when (state.selectionError) {
                                    AccountSelectionError.READ -> R.string.accounts_selection_read_error
                                    AccountSelectionError.SAVE -> R.string.accounts_selection_save_error
                                }
                            )
                        )
                        if (state.selectionError == AccountSelectionError.READ) {
                            BrutusButton(
                                label = stringResource(
                                    id = R.string.accounts_retry
                                ),
                                onClick = {
                                    onAction(
                                        AccountListAction.RetrySelection
                                    )
                                }
                            )
                        }
                    }
                }
            }
            if (state.isSavingSelection) {
                item(
                    key = "accounts-selection-saving"
                ) {
                    BrutusLoading()
                }
            }
            if (state.error != null) {
                item(
                    key = "accounts-error"
                ) {
                    AccountListFailure(
                        error = state.error
                    ) {
                        onAction(
                            AccountListAction.Retry
                        )
                    }
                }
            }
            if (state.hasLoaded && state.accounts.isEmpty()) {
                item(
                    key = "accounts-empty"
                ) {
                    Text(
                        text = stringResource(
                            id = if (state.nextPage == null) R.string.accounts_empty else R.string.accounts_empty_loaded
                        ),
                        modifier = Modifier.padding(
                            vertical = BrutusTheme.spacing.huge
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(
                items = state.accounts,
                key = { "account-${it.id}" }
            ) { account ->
                AccountCard(
                    account = account,
                    isSelected = account.id == state.selectedAccountId,
                    selectionEnabled = state.hasLoadedSelection && !state.isSavingSelection,
                    onSelect = {
                        onAction(
                            AccountListAction.SelectAccount(
                                accountId = account.id
                            )
                        )
                    }
                )
            }
            item(
                key = footerKey
            ) {
                AccountListFooter(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}

@Preview(name = "Accounts — light", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AccountListPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        AccountListScreen(
            state = AccountListState(
                accounts = sampleAccounts,
                hasLoaded = true,
                selectedAccountId = sampleAccounts.first().id,
                hasLoadedSelection = true
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Accounts — dark", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AccountListDarkPreview() {
    BrutusFlyTheme(
        darkTheme = true
    ) {
        AccountListScreen(
            state = AccountListState(
                accounts = sampleAccounts,
                hasLoaded = true,
                selectedAccountId = sampleAccounts[1].id,
                hasLoadedSelection = true
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Accounts — loading", showBackground = true, widthDp = 393)
@Composable
private fun AccountListLoadingPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        AccountListScreen(
            state = AccountListState(
                isLoading = true
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Accounts — empty", showBackground = true, widthDp = 393)
@Composable
private fun AccountListEmptyPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        AccountListScreen(
            state = AccountListState(
                hasLoaded = true
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Accounts — error", showBackground = true, widthDp = 393)
@Composable
private fun AccountListErrorPreview() {
    BrutusFlyTheme(
        darkTheme = true
    ) {
        AccountListScreen(
            state = AccountListState(
                error = AccountListError.Network
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Accounts — page retry", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AccountListPageErrorPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        AccountListScreen(
            state = AccountListState(
                accounts = sampleAccounts.take(
                    n = 1
                ),
                hasLoaded = true,
                nextPage = 2,
                loadMoreError = AccountListError.Network
            ),
            onAction = {}
        )
    }
}
