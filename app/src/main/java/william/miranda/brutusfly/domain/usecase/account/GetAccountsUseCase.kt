package william.miranda.brutusfly.domain.usecase.account

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.model.account.AccountPage
import william.miranda.brutusfly.domain.model.account.AccountTypeFilter
import william.miranda.brutusfly.domain.repository.account.AccountRepository
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val repository: AccountRepository
) : SuspendUseCase<GetAccountsUseCase.Input, AccountPage> {

    data class Input(
        val page: Int = 1,
        val limit: Int = 50,
        val type: AccountTypeFilter? = null
    )

    override suspend fun invoke(
        input: Input
    ): AccountPage =
        repository.getAccounts(
            page = input.page,
            limit = input.limit,
            type = input.type
        )
}
