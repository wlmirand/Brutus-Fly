package william.miranda.brutusfly.domain.usecase.selectedaccount

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.repository.selectedaccount.SelectedAccountRepository
import javax.inject.Inject

class SelectAccountUseCase @Inject constructor(
    private val repository: SelectedAccountRepository
) : SuspendUseCase<SelectAccountUseCase.Input, Unit> {
    data class Input(
        val accountId: String
    )

    override suspend fun invoke(input: Input) {
        require(input.accountId.isNotBlank()) { "Account ID must not be blank" }
        repository.select(
            accountId = input.accountId
        )
    }
}
