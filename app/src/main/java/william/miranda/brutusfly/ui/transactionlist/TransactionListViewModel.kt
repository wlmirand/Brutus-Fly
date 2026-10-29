package william.miranda.brutusfly.ui.transactionlist

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import william.miranda.brutusfly.core.ui.BaseViewModel
import william.miranda.brutusfly.domain.model.category.Category
import william.miranda.brutusfly.domain.model.category.CategoryFailure
import william.miranda.brutusfly.domain.model.category.CategoryIcon
import william.miranda.brutusfly.domain.model.category.CategoryLoadException
import william.miranda.brutusfly.domain.model.transaction.TransactionFailure
import william.miranda.brutusfly.domain.model.transaction.TransactionGroup
import william.miranda.brutusfly.domain.model.transaction.TransactionLoadException
import william.miranda.brutusfly.domain.usecase.account.GetAccountTransactionsUseCase
import william.miranda.brutusfly.domain.usecase.category.GetCategoriesUseCase
import william.miranda.brutusfly.domain.usecase.categoryicon.ObserveCategoryIconsUseCase
import william.miranda.brutusfly.domain.usecase.selectedaccount.ObserveSelectedAccountUseCase
import william.miranda.brutusfly.ui.transactionlist.mapper.TransactionListUiMapper
import javax.inject.Inject

@HiltViewModel
internal class TransactionListViewModel @Inject constructor(
    private val getAccountTransactions: GetAccountTransactionsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val observeCategoryIcons: ObserveCategoryIconsUseCase,
    private val mapper: TransactionListUiMapper,
    private val observeSelectedAccount: ObserveSelectedAccountUseCase
) :
    BaseViewModel<TransactionListState, TransactionListAction, Nothing>(
        initialState = TransactionListState()
    ) {
    private var loadJob: Job? = null
    private var categoriesJob: Job? = null
    private var selectionObservationJob: Job? = null
    private var iconsObservationJob: Job? = null
    private var categoryIcons: Map<String, CategoryIcon> = emptyMap()
    private var selectionGeneration = 0L
    // Keep the accepted domain groups to merge later pages before rebuilding daily UI sections.
    private var loadedTransactions: List<TransactionGroup> = emptyList()

    init {
        observeIcons()
        loadCategories()
        observeSelection()
    }

    override fun onAction(
        action: TransactionListAction
    ) {
        when (action) {
            TransactionListAction.RetryIcons -> observeIcons()
            TransactionListAction.RetryCategories -> loadCategories()
            TransactionListAction.RetrySelection -> observeSelection()
            TransactionListAction.Refresh,
            TransactionListAction.Retry -> loadTransactions()
            TransactionListAction.LoadNextPage -> loadNextPage()
            TransactionListAction.RetryNextPage -> loadNextPage(retry = true)
            TransactionListAction.ErrorDismissed -> updateState {
                if (isAccountUnavailable)
                    this
                else
                    copy(error = null)
            }
            is TransactionListAction.SearchChanged -> updateState { copy(query = action.query) }
            is TransactionListAction.FilterChanged -> updateState { copy(selectedFilter = action.filter) }
            is TransactionListAction.PeriodChanged -> updateState { copy(selectedPeriod = action.period) }
            is TransactionListAction.CategoryChanged -> updateState { copy(selectedCategoryId = action.category?.id) }
        }
    }

    private fun observeIcons() {
        if (iconsObservationJob?.isActive == true) return
        iconsObservationJob = viewModelScope.launch {
            try {
                observeCategoryIcons(input = Unit).collect { icons ->
                    val mappedGroups = mapper.map(
                        input = TransactionListUiMapper.Input(
                            groups = loadedTransactions,
                            categoryIcons = icons
                        )
                    )
                    categoryIcons = icons
                    updateState { copy(groups = mappedGroups, iconReadError = false) }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState { copy(iconReadError = true) }
            }
        }
    }

    private fun loadCategories() {
        if (categoriesJob?.isActive == true) return
        updateState {
            copy(
                isLoadingCategories = true,
                categoriesError = null
            )
        }
        categoriesJob = viewModelScope.launch {
            try {
                val categories = linkedMapOf<String, Category>()
                var nextPage: Int? = 1
                while (nextPage != null) {
                    val result = getCategories(
                        input = GetCategoriesUseCase.Input(
                            page = nextPage
                        )
                    )
                    currentCoroutineContext().ensureActive()
                    result.categories.forEach { categories[it.id] = it }
                    nextPage = result.nextPage
                }
                updateState {
                    copy(
                        categories = categories.values.toList(),
                        isLoadingCategories = false,
                        categoriesError = null
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                currentCoroutineContext().ensureActive()
                updateState {
                    copy(
                        isLoadingCategories = false,
                        categoriesError = (exception as? CategoryLoadException)?.failure
                            ?: CategoryFailure.UNKNOWN
                    )
                }
            }
        }
    }

    private fun observeSelection() {
        if (selectionObservationJob?.isActive == true) return
        updateState { copy(selectionReadError = false) }

        selectionObservationJob = viewModelScope.launch {
            try {
                observeSelectedAccount(input = Unit).collect { accountId ->
                    if (!state.value.hasLoadedSelection || state.value.selectedAccountId != accountId) {
                        resetAccount(
                            accountId = accountId,
                            selectionLoaded = true
                        )
                        if (accountId != null) {
                            loadTransactions()
                        }
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                resetAccount(
                    accountId = null,
                    selectionLoaded = false
                )
                updateState {
                    copy(selectionReadError = true)
                }
            }
        }
    }

    private fun resetAccount(
        accountId: String?,
        selectionLoaded: Boolean
    ) {
        // Invalidate before cancelling: late completion or cancellation must not mutate the new account's state.
        selectionGeneration++
        loadJob?.cancel()
        loadJob = null
        loadedTransactions = emptyList()
        updateState {
            copy(
                selectedAccountId = accountId,
                hasLoadedSelection = selectionLoaded,
                selectionReadError = false,
                groups = emptyList(),
                hasLoaded = false,
                isLoading = false,
                isLoadingMore = false,
                nextPage = null,
                error = null,
                loadMoreError = null
            )
        }
    }

    private fun loadTransactions() {
        loadPage(
            page = 1,
            append = false
        )
    }

    private fun loadNextPage(
        retry: Boolean = false
    ) {
        val current = state.value
        if (!current.hasLoaded || current.error != null || (!retry && current.loadMoreError != null)) return
        val page = current.nextPage ?: return
        loadPage(
            page = page,
            append = true
        )
    }

    private fun loadPage(
        page: Int,
        append: Boolean
    ) {
        val current = state.value
        if (!current.hasLoadedSelection || current.selectionReadError) return
        val accountId = current.selectedAccountId ?: return
        if (loadJob?.isActive == true || state.value.isLoading || state.value.isLoadingMore) return
        val generation = selectionGeneration
        updateState {
            copy(
                isLoading = !append,
                isLoadingMore = append,
                error = null,
                loadMoreError = null
            )
        }
        loadJob = viewModelScope.launch {
            try {
                val result = getAccountTransactions(
                    input = GetAccountTransactionsUseCase.Input(
                        accountId = accountId,
                        page = page
                    )
                )
                currentCoroutineContext().ensureActive()
                if (generation != selectionGeneration) return@launch
                val transactions = ((if (append) loadedTransactions else emptyList()) + result.groups)
                    .associateBy { it.id }
                    .values.toList()
                val groups = mapper.map(
                    input = TransactionListUiMapper.Input(
                        groups = transactions,
                        categoryIcons = categoryIcons
                    )
                )
                loadedTransactions = transactions
                updateState {
                    copy(
                        groups = groups,
                        hasLoaded = true,
                        isLoading = false,
                        isLoadingMore = false,
                        error = null,
                        loadMoreError = null,
                        nextPage = result.nextPage
                    )
                }
            } catch (cancelled: CancellationException) {
                if (generation == selectionGeneration) {
                    updateState {
                        copy(
                            isLoading = false,
                            isLoadingMore = false
                        )
                    }
                }
                throw cancelled
            } catch (exception: Exception) {
                currentCoroutineContext().ensureActive()
                if (generation != selectionGeneration) return@launch
                val failure = when ((exception as? TransactionLoadException)?.failure) {
                    TransactionFailure.ACCOUNT_UNAVAILABLE -> TransactionListError.AccountUnavailable
                    TransactionFailure.ACCOUNT_NOT_SELECTABLE -> TransactionListError.AccountNotSelectable
                    TransactionFailure.NETWORK -> TransactionListError.Network
                    TransactionFailure.UNAUTHORIZED -> TransactionListError.Unauthorized
                    TransactionFailure.SERVER -> TransactionListError.Server
                    TransactionFailure.UNKNOWN, null -> TransactionListError.Unknown
                }
                if (failure == TransactionListError.AccountUnavailable || failure == TransactionListError.AccountNotSelectable) {
                    loadedTransactions = emptyList()
                    updateState {
                        copy(
                            groups = emptyList(),
                            hasLoaded = false,
                            isLoading = false,
                            isLoadingMore = false,
                            nextPage = null,
                            loadMoreError = null,
                            error = failure
                        )
                    }
                    return@launch
                }
                updateState {
                    if (append) {
                        copy(
                            isLoadingMore = false,
                            loadMoreError = failure
                        )
                    } else {
                        copy(
                            isLoading = false,
                            error = failure
                        )
                    }
                }
            }
        }
    }
}
