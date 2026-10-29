package william.miranda.brutusfly.domain.repository.transaction

import william.miranda.brutusfly.domain.model.transaction.TransactionPage

interface TransactionRepository {

    /** Returns one page and its server-reported boundaries; callers decide when to request more. */
    suspend fun getTransactions(
        page: Int,
        limit: Int
    ): TransactionPage
}
