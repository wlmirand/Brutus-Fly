package william.miranda.brutusfly.domain.repository.account

import william.miranda.brutusfly.domain.model.account.AccountPage
import william.miranda.brutusfly.domain.model.account.Account
import william.miranda.brutusfly.domain.model.account.AccountTypeFilter
import william.miranda.brutusfly.domain.model.transaction.TransactionPage

interface AccountRepository {
    /** Returns one page of accounts, optionally filtered by type. */
    suspend fun getAccounts(
        page: Int,
        limit: Int,
        type: AccountTypeFilter? = null
    ): AccountPage

    /** Returns the current account attributes; failures surface as AccountLoadException. */
    suspend fun getAccount(
        accountId: String
    ): Account

    /**
     * Returns one page of the transaction groups recorded against the given account.
     * Pagination is server-authoritative; failures surface as TransactionLoadException.
     */
    suspend fun getAccountTransactions(
        accountId: String,
        page: Int,
        limit: Int
    ): TransactionPage
}
