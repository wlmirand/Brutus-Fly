package william.miranda.brutusfly.ui.transactionlist

import william.miranda.brutusfly.domain.model.category.Category
import william.miranda.brutusfly.domain.model.category.CategoryFailure
import william.miranda.brutusfly.ui.transactionlist.model.TransactionFilter
import william.miranda.brutusfly.ui.transactionlist.model.TransactionPeriod
import william.miranda.brutusfly.ui.transactionlist.model.TransactionUiGroup
import william.miranda.brutusfly.ui.transactionlist.model.TransactionUiModel
import william.miranda.brutusfly.ui.transactionlist.model.matchesFilter
import william.miranda.brutusfly.ui.transactionlist.model.matchesQuery
import william.miranda.brutusfly.ui.transactionlist.model.matchesPeriod

/**
 * Snapshot of the transaction screen, combining persisted account selection,
 * paginated transactions, category options, and local icon assignments.
 *
 * [groups] holds the loaded data before local filtering. [filteredGroups] and
 * [searchResults] derive what the screen displays without changing those source groups.
 * Filters apply only to loaded pages; they do not request filtered data from the server.
 */
internal data class TransactionListState(
    // Daily sections accumulated from the current account's accepted pages.
    val groups: List<TransactionUiGroup> = emptyList(),

    // Category loading is independent of account selection and transaction requests.
    val categories: List<Category> = emptyList(),
    val isLoadingCategories: Boolean = false,
    val categoriesError: CategoryFailure? = null,

    // Icon read failures do not block transactions or discard the last known choices.
    val iconReadError: Boolean = false,

    // Local filters survive refreshes and account changes. A null category means all categories.
    val selectedCategoryId: String? = null,
    val query: String = "",
    val selectedFilter: TransactionFilter = TransactionFilter.ALL,
    val selectedPeriod: TransactionPeriod = TransactionPeriod.THIS_MONTH,

    // True after a successful transaction page load, even when that page contains no items.
    val hasLoaded: Boolean = false,
    // Initial loads and refreshes share these fields; refresh failures can coexist with old groups.
    val isLoading: Boolean = false,
    val error: TransactionListError? = null,

    // Server-reported cursor. Null means no next page, but also occurs before the first load.
    val nextPage: Int? = null,
    // Appending failures do not replace the already loaded groups or the retry cursor.
    val isLoadingMore: Boolean = false,
    val loadMoreError: TransactionListError? = null,

    // Null account + successful selection read means no account has been selected.
    val selectedAccountId: String? = null,
    // Distinguishes a completed selection read from the initial, unresolved state.
    val hasLoadedSelection: Boolean = false,
    // Reading persisted selection failed; this is not a transaction API error.
    val selectionReadError: Boolean = false
) {
    /** The selected account is missing or ineligible; its persisted ID is not cleared automatically. */
    val isAccountUnavailable: Boolean
        get() = error == TransactionListError.AccountUnavailable || error == TransactionListError.AccountNotSelectable

    /** Enables the flat search-results presentation instead of the daily sections. */
    val isSearching: Boolean
        get() = query.isNotBlank()

    /**
     * Daily sections after period, type, and category filtering, but before text search.
     * Empty sections are removed and totals include only the remaining transactions.
     * Computed on access, so newly loaded pages automatically use the current filters.
     */
    val filteredGroups: List<TransactionUiGroup>
        get() = groups
            // Period belongs to the daily section; type and category belong to each split.
            .filter { it.matchesPeriod(selectedPeriod = selectedPeriod) }
            .mapNotNull { group ->
                val visibleTransactions = group.transactions.filter { transactionUiModel ->
                    transactionUiModel.matchesFilter(selectedFilter)
                            && (selectedCategoryId == null || transactionUiModel.categoryId == selectedCategoryId)
                }
                if (visibleTransactions.isEmpty()) {
                    // Do not leave an empty date header after filtering its transactions.
                    null
                } else {
                    group.copy(
                        // Currency totals are derived from these items by TransactionUiGroup.
                        transactions = visibleTransactions
                    )
                }
            }

    /** Flat text matches within [filteredGroups], preserving all other active filters. */
    val searchResults: List<TransactionUiModel>
        get() = filteredGroups
            .flatMap { it.transactions }
            .filter {
                it.matchesQuery(
                    query = query
                )
            }
}

internal sealed interface TransactionListError {
    data object AccountUnavailable : TransactionListError
    data object AccountNotSelectable : TransactionListError
    data object Network : TransactionListError
    data object Unauthorized : TransactionListError
    data object Server : TransactionListError
    data object Unknown : TransactionListError
}

internal sealed interface TransactionListAction {
    data object RetryIcons : TransactionListAction
    data object RetryCategories : TransactionListAction
    data object RetrySelection : TransactionListAction
    data object LoadNextPage : TransactionListAction
    data object RetryNextPage : TransactionListAction
    data object Refresh : TransactionListAction
    data object Retry : TransactionListAction
    data object ErrorDismissed : TransactionListAction
    data class SearchChanged(
        val query: String
    ) : TransactionListAction
    data class FilterChanged(
        val filter: TransactionFilter
    ) : TransactionListAction
    data class PeriodChanged(
        val period: TransactionPeriod
    ) : TransactionListAction
    data class CategoryChanged(
        val category: Category?
    ) : TransactionListAction
}
