package william.miranda.brutusfly.ui.accountlist.model

internal enum class AccountKind {
    ASSET, CASH, EXPENSE, REVENUE, LIABILITY, INITIAL_BALANCE, RECONCILIATION, OTHER,
}

internal data class AccountUiModel(
    val id: String,
    val name: String,
    val kind: AccountKind,
    val balance: String?,
    val isActive: Boolean,
    val hasNegativeBalance: Boolean,
    val isSelectable: Boolean
)

// Preview-only examples. Runtime accounts are loaded through AccountRepository.
internal val sampleAccounts = listOf(
    AccountUiModel(
        id = "checking",
        name = "Checking account",
        kind = AccountKind.ASSET,
        balance = "\$1,240.50",
        isActive = true,
        hasNegativeBalance = false,
        isSelectable = true
    ),
    AccountUiModel(
        id = "cash",
        name = "Wallet",
        kind = AccountKind.CASH,
        balance = "\$85.00",
        isActive = true,
        hasNegativeBalance = false,
        isSelectable = true
    ),
    AccountUiModel(
        id = "credit",
        name = "Credit card",
        kind = AccountKind.LIABILITY,
        balance = "-\$320.75",
        isActive = true,
        hasNegativeBalance = true,
        isSelectable = false
    ),
    AccountUiModel(
        id = "closed",
        name = "Previous savings account",
        kind = AccountKind.ASSET,
        balance = null,
        isActive = false,
        hasNegativeBalance = false,
        isSelectable = false
    )
)
