package william.miranda.brutusfly.ui.accountlist

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import william.miranda.brutusfly.core.ui.BaseViewModel
import william.miranda.brutusfly.domain.model.account.AccountFailure
import william.miranda.brutusfly.domain.model.account.AccountLoadException
import william.miranda.brutusfly.domain.model.account.AccountTypeFilter
import william.miranda.brutusfly.domain.usecase.account.GetAccountsUseCase
import william.miranda.brutusfly.domain.usecase.selectedaccount.InitializeSelectedAccountUseCase
import william.miranda.brutusfly.domain.usecase.selectedaccount.ObserveSelectedAccountUseCase
import william.miranda.brutusfly.domain.usecase.selectedaccount.SelectAccountUseCase
import william.miranda.brutusfly.ui.accountlist.mapper.AccountListUiMapper
import javax.inject.Inject

@HiltViewModel
internal class AccountListViewModel @Inject constructor(
    private val getAccounts: GetAccountsUseCase,
    private val mapper: AccountListUiMapper,
    private val initializeSelectedAccount: InitializeSelectedAccountUseCase,
    private val observeSelectedAccount: ObserveSelectedAccountUseCase,
    private val selectAccount: SelectAccountUseCase
) : BaseViewModel<AccountListState, AccountListAction, Nothing>(
    initialState = AccountListState()
) {
    private var loadJob: Job? = null
    private var selectionObservationJob: Job? = null

    init {
        observeSelection()
        loadPage(
            page = 1,
            append = false
        )
    }

    override fun onAction(
        action: AccountListAction
    ) {
        when (action) {
            is AccountListAction.SelectAccount -> select(
                accountId = action.accountId
            )
            AccountListAction.RetrySelection -> observeSelection()
            AccountListAction.Refresh,
            AccountListAction.Retry -> loadPage(
                page = 1,
                append = false
            )
            AccountListAction.LoadNextPage -> loadNextPage()
            AccountListAction.RetryNextPage -> loadNextPage(
                retry = true
            )
        }
    }

    private fun observeSelection() {
        if (selectionObservationJob?.isActive == true) return
        updateState {
            copy(
                selectionError = null
            )
        }
        selectionObservationJob = viewModelScope.launch {
            try {
                observeSelectedAccount(
                    input = Unit
                ).collect { accountId ->
                    updateState {
                        copy(
                            selectedAccountId = accountId,
                            hasLoadedSelection = true
                        )
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState {
                    copy(
                        hasLoadedSelection = false,
                        selectionError = AccountSelectionError.READ
                    )
                }
            }
        }
    }

    private fun select(
        accountId: String
    ) {
        val current = state.value
        if (!current.hasLoadedSelection || current.isSavingSelection || current.selectedAccountId == accountId) return
        val account = current.accounts.firstOrNull { it.id == accountId } ?: return
        if (!account.isSelectable) return

        updateState {
            copy(
                isSavingSelection = true,
                selectionError = null
            )
        }
        viewModelScope.launch {
            try {
                selectAccount(
                    input = SelectAccountUseCase.Input(
                        accountId = accountId
                    )
                )
                // The observed persisted ID, not the click, determines the selected card.
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState {
                    copy(
                        selectionError = if (hasLoadedSelection) AccountSelectionError.SAVE else AccountSelectionError.READ
                    )
                }
            } finally {
                updateState {
                    copy(
                        isSavingSelection = false
                    )
                }
            }
        }
    }

    private fun loadNextPage(
        retry: Boolean = false
    ) {
        val current = state.value
        if (!current.hasLoaded || current.error != null || (!retry && current.loadMoreError != null)) return
        loadPage(
            page = current.nextPage ?: return,
            append = true
        )
    }

    private fun loadPage(
        page: Int,
        append: Boolean
    ) {
        if (loadJob?.isActive == true || state.value.isLoading || state.value.isLoadingMore) return

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
                val result = getAccounts(
                    input = GetAccountsUseCase.Input(
                        page = page,
                        type = AccountTypeFilter.ASSET_ACCOUNT
                    )
                )
                val incoming = mapper.map(
                    input = AccountListUiMapper.Input(
                        accounts = result.accounts
                    )
                )
                result.accounts.firstOrNull { it.isSelectable }?.let { account ->
                    initializeSelectedAccount(
                        input = InitializeSelectedAccountUseCase.Input(
                            accountId = account.id
                        )
                    )
                }
                updateState {
                    copy(
                        accounts = ((if (append) accounts else emptyList()) + incoming)
                            .associateBy { it.id }.values.toList(),
                        hasLoaded = true,
                        isLoading = false,
                        isLoadingMore = false,
                        error = null,
                        loadMoreError = null,
                        nextPage = result.nextPage
                    )
                }
            } catch (cancelled: CancellationException) {
                updateState {
                    copy(
                        isLoading = false,
                        isLoadingMore = false
                    )
                }
                throw cancelled
            } catch (exception: Exception) {
                val failure = when ((exception as? AccountLoadException)?.failure) {
                    AccountFailure.NETWORK -> AccountListError.Network
                    AccountFailure.UNAUTHORIZED -> AccountListError.Unauthorized
                    AccountFailure.SERVER -> AccountListError.Server
                    AccountFailure.NOT_FOUND, AccountFailure.UNKNOWN, null -> AccountListError.Unknown
                }
                updateState {
                    if (append) copy(
                        isLoadingMore = false,
                        loadMoreError = failure
                    )
                    else copy(
                        isLoading = false,
                        error = failure
                    )
                }
            }
        }
    }
}
