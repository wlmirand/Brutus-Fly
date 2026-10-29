package william.miranda.brutusfly.data.mapper.account

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.domain.model.account.AccountTypeFilter
import javax.inject.Inject

/** Converts domain filters to the exact query values accepted by Firefly III. */
class AccountTypeFilterMapper @Inject constructor() : Mapper<AccountTypeFilter, String> {
    /** Maps every documented filter explicitly, including the long-form account names. */
    override fun map(
        input: AccountTypeFilter
    ): String = when (input) {
        AccountTypeFilter.ALL -> "all"
        AccountTypeFilter.ASSET -> "asset"
        AccountTypeFilter.CASH -> "cash"
        AccountTypeFilter.EXPENSE -> "expense"
        AccountTypeFilter.REVENUE -> "revenue"
        AccountTypeFilter.SPECIAL -> "special"
        AccountTypeFilter.HIDDEN -> "hidden"
        AccountTypeFilter.LIABILITY -> "liability"
        AccountTypeFilter.LIABILITIES -> "liabilities"
        AccountTypeFilter.DEFAULT_ACCOUNT -> "Default account"
        AccountTypeFilter.CASH_ACCOUNT -> "Cash account"
        AccountTypeFilter.ASSET_ACCOUNT -> "Asset account"
        AccountTypeFilter.EXPENSE_ACCOUNT -> "Expense account"
        AccountTypeFilter.REVENUE_ACCOUNT -> "Revenue account"
        AccountTypeFilter.INITIAL_BALANCE_ACCOUNT -> "Initial balance account"
        AccountTypeFilter.BENEFICIARY_ACCOUNT -> "Beneficiary account"
        AccountTypeFilter.IMPORT_ACCOUNT -> "Import account"
        AccountTypeFilter.RECONCILIATION_ACCOUNT -> "Reconciliation account"
        AccountTypeFilter.LOAN -> "Loan"
        AccountTypeFilter.DEBT -> "Debt"
        AccountTypeFilter.MORTGAGE -> "Mortgage"
    }
}
