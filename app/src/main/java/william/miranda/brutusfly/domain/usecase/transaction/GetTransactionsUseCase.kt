package william.miranda.brutusfly.domain.usecase.transaction

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.model.transaction.TransactionPage
import william.miranda.brutusfly.domain.repository.transaction.TransactionRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) : SuspendUseCase<GetTransactionsUseCase.Input, TransactionPage> {

    data class Input(
        val page: Int = 1,
        val limit: Int = 50
    )

    override suspend fun invoke(
        input: Input
    ): TransactionPage {
        return repository.getTransactions(
            page = input.page,
            limit = input.limit
        )
    }

}
