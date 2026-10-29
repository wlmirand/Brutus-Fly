package william.miranda.brutusfly.domain.model.account

/** Account-list filters, distinct from the type of an individual account. */
enum class AccountTypeFilter {
    ALL,
    ASSET,
    CASH,
    EXPENSE,
    REVENUE,
    SPECIAL,
    HIDDEN,
    LIABILITY,
    LIABILITIES,
    DEFAULT_ACCOUNT,
    CASH_ACCOUNT,
    ASSET_ACCOUNT,
    EXPENSE_ACCOUNT,
    REVENUE_ACCOUNT,
    INITIAL_BALANCE_ACCOUNT,
    BENEFICIARY_ACCOUNT,
    IMPORT_ACCOUNT,
    RECONCILIATION_ACCOUNT,
    LOAN,
    DEBT,
    MORTGAGE,
}
