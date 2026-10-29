package william.miranda.brutusfly.ui.accountlist

import william.miranda.brutusfly.ui.accountlist.model.AccountUiModel

internal data class AccountListState(
    val accounts: List<AccountUiModel> = emptyList(),
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val error: AccountListError? = null,
    val nextPage: Int? = null,
    val isLoadingMore: Boolean = false,
    val loadMoreError: AccountListError? = null,
    val selectedAccountId: String? = null,
    val hasLoadedSelection: Boolean = false,
    val isSavingSelection: Boolean = false,
    val selectionError: AccountSelectionError? = null
)

internal enum class AccountSelectionError {
    READ,
    SAVE,
}

internal sealed interface AccountListError {
    data object Network : AccountListError
    data object Unauthorized : AccountListError
    data object Server : AccountListError
    data object Unknown : AccountListError
}

internal sealed interface AccountListAction {
    data class SelectAccount(
        val accountId: String
    ) : AccountListAction
    data object RetrySelection : AccountListAction
    data object Refresh : AccountListAction
    data object Retry : AccountListAction
    data object LoadNextPage : AccountListAction
    data object RetryNextPage : AccountListAction
}
