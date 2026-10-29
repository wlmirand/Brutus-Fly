package william.miranda.brutusfly.domain.usecase.account

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.model.transaction.TransactionPage
import william.miranda.brutusfly.domain.model.transaction.TransactionFailure
import william.miranda.brutusfly.domain.model.transaction.TransactionLoadException
import william.miranda.brutusfly.domain.model.account.AccountFailure
import william.miranda.brutusfly.domain.model.account.AccountLoadException
import william.miranda.brutusfly.domain.repository.account.AccountRepository
import javax.inject.Inject

/** Validates current account eligibility before page one, including refresh and retry of that page. */
class GetAccountTransactionsUseCase @Inject constructor(
    private val repository: AccountRepository
) : SuspendUseCase<GetAccountTransactionsUseCase.Input, TransactionPage> {
    data class Input(
        val accountId: String,
        val page: Int = 1,
        val limit: Int = 50
    )

    override suspend fun invoke(
        input: Input
    ): TransactionPage {
        require(input.accountId.isNotBlank()) { "Account ID must not be blank" }
        if (input.page == 1) validateAccount(accountId = input.accountId)
        return repository.getAccountTransactions(
            accountId = input.accountId,
            page = input.page,
            limit = input.limit
        )
    }

    private suspend fun validateAccount(accountId: String) {
        val account = try {
            repository.getAccount(accountId = accountId)
        } catch (exception: AccountLoadException) {
            val failure = when (exception.failure) {
                AccountFailure.NOT_FOUND -> TransactionFailure.ACCOUNT_UNAVAILABLE
                AccountFailure.NETWORK -> TransactionFailure.NETWORK
                AccountFailure.UNAUTHORIZED -> TransactionFailure.UNAUTHORIZED
                AccountFailure.SERVER -> TransactionFailure.SERVER
                AccountFailure.UNKNOWN -> TransactionFailure.UNKNOWN
            }
            throw TransactionLoadException(
                failure = failure,
                cause = exception
            )
        }
        if (!account.isSelectable) {
            throw TransactionLoadException(
                failure = TransactionFailure.ACCOUNT_NOT_SELECTABLE
            )
        }
    }
}
