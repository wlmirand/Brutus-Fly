package william.miranda.brutusfly.ui.transactionlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import william.miranda.brutusfly.core.designsystem.components.BrutusButton
import william.miranda.brutusfly.core.designsystem.components.BrutusErrorMessage
import william.miranda.brutusfly.core.designsystem.components.BrutusScreenHeader
import william.miranda.brutusfly.core.designsystem.components.BrutusSearchField
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.domain.model.category.CategoryFailure
import william.miranda.brutusfly.ui.transactionlist.components.TransactionCard
import william.miranda.brutusfly.ui.transactionlist.components.TransactionDateSection
import william.miranda.brutusfly.ui.transactionlist.components.TransactionEmptyState
import william.miranda.brutusfly.ui.transactionlist.components.TransactionFilterRow
import william.miranda.brutusfly.ui.transactionlist.components.TransactionPaginationFooter
import william.miranda.brutusfly.ui.transactionlist.components.TransactionSearchSummary
import william.miranda.brutusfly.ui.transactionlist.components.transactionErrorMessage
import william.miranda.brutusfly.ui.transactionlist.model.sampleTransactionGroups

@Composable
internal fun TransactionListScreen(
    state: TransactionListState,
    onAction: (TransactionListAction) -> Unit,
    onChooseAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredGroups = state.filteredGroups
    val searchResults = state.searchResults
    val listState = rememberLazyListState()
    val currentOnAction by rememberUpdatedState(newValue = onAction)
    val paginationKey = "$PAGINATION_KEY-${state.selectedAccountId}-${state.nextPage}"

    LaunchedEffect(state.selectedAccountId) {
        listState.scrollToItem(index = 0)
    }

    LaunchedEffect(
        listState,
        state.selectedAccountId,
        state.hasLoadedSelection,
        state.selectionReadError,
        state.nextPage,
        state.hasLoaded,
        state.isLoading,
        state.isLoadingMore,
        state.loadMoreError,
        state.error,
        state.query,
        state.selectedFilter,
        state.selectedPeriod,
        state.selectedCategoryId
    ) {
        if (!state.hasLoadedSelection || state.selectedAccountId == null || state.selectionReadError ||
            !state.hasLoaded || state.nextPage == null || state.isLoading || state.isLoadingMore ||
            state.loadMoreError != null || state.error != null
        ) return@LaunchedEffect

        // A new cursor gets a new key, so stale layout info cannot trigger another page before remeasurement.
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.any { it.key == paginationKey } }
            .distinctUntilChanged()
            .collect { visible ->
                if (visible) {
                    currentOnAction(TransactionListAction.LoadNextPage)
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {
        BrutusScreenHeader(
            title = stringResource(R.string.transactions_title),
            subtitle = stringResource(R.string.transactions_subtitle),
            modifier = Modifier
                .padding(
                    horizontal = BrutusTheme.spacing.screenHorizontal,
                    vertical = BrutusTheme.spacing.large
                ),
            actions = {
                IconButton(
                    onClick = { onAction(TransactionListAction.Refresh) },
                    enabled = state.hasLoadedSelection && state.selectedAccountId != null &&
                        !state.selectionReadError && !state.isLoading && !state.isLoadingMore,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = stringResource(R.string.transactions_refresh)
                    )
                }
            }
        )

        BrutusSearchField(
            modifier = Modifier.padding(
                start = BrutusTheme.spacing.screenHorizontal,
                top = BrutusTheme.spacing.small,
                end = BrutusTheme.spacing.screenHorizontal
            ),
            query = state.query,
            onQueryChange = { onAction(TransactionListAction.SearchChanged(query = it)) },
            placeholder = stringResource(R.string.transactions_search)
        )

        Spacer(modifier = Modifier.height(BrutusTheme.spacing.large))

        TransactionFilterRow(
            modifier = Modifier.padding(
                start = BrutusTheme.spacing.screenHorizontal,
                end = BrutusTheme.spacing.screenHorizontal,
                bottom = BrutusTheme.spacing.medium
            ),
            selectedFilter = state.selectedFilter,
            onFilterSelected = { onAction(TransactionListAction.FilterChanged(filter = it)) },
            selectedPeriod = state.selectedPeriod,
            categories = state.categories,
            selectedCategory = state.categories.find { it.id == state.selectedCategoryId },
            onCategorySelected = { onAction(TransactionListAction.CategoryChanged(category = it)) },
            onPeriodSelected = { onAction(TransactionListAction.PeriodChanged(period = it)) }
        )

        if (state.isLoadingCategories) {
            Text(
                text = stringResource(id = R.string.transactions_categories_loading),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = BrutusTheme.spacing.screenHorizontal)
            )
        }

        if (state.categoriesError != null) {
            BrutusErrorMessage(
                message = stringResource(
                    when (state.categoriesError) {
                        CategoryFailure.NETWORK -> R.string.categories_error_network
                        CategoryFailure.UNAUTHORIZED -> R.string.categories_error_unauthorized
                        CategoryFailure.SERVER -> R.string.categories_error_server
                        CategoryFailure.UNKNOWN -> R.string.categories_error_unknown
                    }
                ),
                modifier = Modifier.padding(horizontal = BrutusTheme.spacing.screenHorizontal)
            )
            
            BrutusButton(
                label = stringResource(id = R.string.configuration_retry),
                onClick = { onAction(TransactionListAction.RetryCategories) },
                modifier = Modifier.padding(horizontal = BrutusTheme.spacing.screenHorizontal)
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = BrutusTheme.spacing.screenHorizontal,
                top = BrutusTheme.spacing.small,
                end = BrutusTheme.spacing.screenHorizontal,
                bottom = BrutusTheme.spacing.extraLarge
            )
        ) {
            if (state.iconReadError) {
                item(key = "category-icons-error") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.small)
                    ) {
                        BrutusErrorMessage(
                            message = stringResource(id = R.string.transactions_icons_read_error)
                        )
                        BrutusButton(
                            label = stringResource(id = R.string.category_icon_retry),
                            onClick = { onAction(TransactionListAction.RetryIcons) }
                        )
                    }
                }
            }
            if (state.selectionReadError) {
                item(key = "selection-error") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            space = BrutusTheme.spacing.medium
                        )
                    ) {
                        BrutusErrorMessage(
                            message = stringResource(R.string.transactions_selection_read_error)
                        )
                        BrutusButton(
                            label = stringResource(R.string.configuration_retry),
                            onClick = { onAction(TransactionListAction.RetrySelection) }
                        )
                    }
                }
            } else if (state.hasLoadedSelection && state.selectedAccountId == null) {
                item(key = "no-selected-account") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            space = BrutusTheme.spacing.medium
                        )
                    ) {
                        TransactionEmptyState(
                            message = stringResource(R.string.transactions_no_selected_account)
                        )
                        BrutusButton(
                            label = stringResource(R.string.transactions_choose_account),
                            onClick = onChooseAccount
                        )
                    }
                }
            }

            if (state.error != null) {
                item(key = "request-error") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(BrutusTheme.spacing.medium)
                    ) {
                        BrutusErrorMessage(
                            message = transactionErrorMessage(error = state.error)
                        )
                        if (state.isAccountUnavailable) {
                            BrutusButton(
                                label = stringResource(R.string.transactions_choose_account),
                                onClick = onChooseAccount
                            )
                        }
                        BrutusButton(
                            label = stringResource(R.string.configuration_retry),
                            onClick = { onAction(TransactionListAction.Retry) }
                        )
                    }
                }
            }

            if (state.hasLoaded && state.isSearching) {
                item {
                    Column {
                        TransactionSearchSummary(
                            count = searchResults.size,
                            query = state.query.trim()
                        )
                        if (state.nextPage != null) {
                            Text(
                                text = stringResource(R.string.transactions_partial_results),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (searchResults.isEmpty()) {
                    item {
                        TransactionEmptyState(
                            message = stringResource(
                                if (state.nextPage == null) R.string.transactions_empty_search
                                else R.string.transactions_empty_loaded
                            )
                        )
                    }
                } else {
                    item {
                        TransactionCard(
                            transactions = searchResults
                        )
                    }
                }
            } else if (state.hasLoaded && filteredGroups.isEmpty()) {
                item {
                    TransactionEmptyState(
                        message = stringResource(
                            when {
                                state.nextPage != null -> R.string.transactions_empty_loaded
                                state.selectedCategoryId != null -> R.string.transactions_empty_category
                                else -> R.string.transactions_empty_period
                            }
                        )
                    )
                }
            } else {
                filteredGroups.forEach { group ->
                    item(key = "section-${group.label}") {
                        TransactionDateSection(
                            group = group
                        )
                    }
                }
            }

            item(key = paginationKey) {
                TransactionPaginationFooter(
                    state = state,
                    onAction = onAction
                )
            }
        }

    }
}

private const val PAGINATION_KEY = "transaction-pagination-footer"

@Preview(
    name = "Transactions",
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun TransactionListScreenPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        TransactionListScreen(
            state = TransactionListState(
                groups = sampleTransactionGroups,
                hasLoaded = true,
                selectedAccountId = "preview-account",
                hasLoadedSelection = true
            ),
            onAction = {},
            onChooseAccount = {}
        )
    }
}

@Preview(
    name = "Transactions — search",
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun TransactionListSearchPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        TransactionListScreen(
            state = TransactionListState(
                groups = sampleTransactionGroups,
                query = "grocery",
                hasLoaded = true,
                selectedAccountId = "preview-account",
                hasLoadedSelection = true
            ),
            onAction = {},
            onChooseAccount = {}
        )
    }
}

@Preview(
    name = "Transactions — dark",
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun TransactionListDarkPreview() {
    BrutusFlyTheme(
        darkTheme = true
    ) {
        TransactionListScreen(
            state = TransactionListState(
                groups = sampleTransactionGroups,
                hasLoaded = true,
                selectedAccountId = "preview-account",
                hasLoadedSelection = true
            ),
            onAction = {},
            onChooseAccount = {}
        )
    }
}

@Preview(name = "Transactions — loading more", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun TransactionListPaginationPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        TransactionListScreen(
            state = TransactionListState(
                groups = sampleTransactionGroups,
                hasLoaded = true,
                nextPage = 2,
                isLoadingMore = true,
                selectedAccountId = "preview-account",
                hasLoadedSelection = true
            ),
            onAction = {},
            onChooseAccount = {}
        )
    }
}

@Preview(name = "Transactions — no account", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun TransactionListNoAccountPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        TransactionListScreen(
            state = TransactionListState(
                hasLoadedSelection = true
            ),
            onAction = {},
            onChooseAccount = {}
        )
    }
}

@Preview(
    name = "Transactions — unavailable account",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun TransactionListUnavailableAccountPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        TransactionListScreen(
            state = TransactionListState(
                selectedAccountId = "preview-account",
                hasLoadedSelection = true,
                error = TransactionListError.AccountUnavailable
            ),
            onAction = {},
            onChooseAccount = {}
        )
    }
}

@Preview(
    name = "Transactions — ineligible account",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun TransactionListIneligibleAccountPreview() {
    BrutusFlyTheme(
        darkTheme = true
    ) {
        TransactionListScreen(
            state = TransactionListState(
                selectedAccountId = "preview-account",
                hasLoadedSelection = true,
                error = TransactionListError.AccountNotSelectable
            ),
            onAction = {},
            onChooseAccount = {}
        )
    }
}
