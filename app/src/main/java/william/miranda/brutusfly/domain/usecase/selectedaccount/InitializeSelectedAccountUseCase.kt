package william.miranda.brutusfly.domain.usecase.selectedaccount

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.repository.selectedaccount.SelectedAccountRepository
import javax.inject.Inject

/** Initializes an absent selection from a supplied candidate; account eligibility is decided by the caller. */
class InitializeSelectedAccountUseCase @Inject constructor(
    private val repository: SelectedAccountRepository
) : SuspendUseCase<InitializeSelectedAccountUseCase.Input, Unit> {
    data class Input(
        val accountId: String
    )

    override suspend fun invoke(input: Input) {
        require(input.accountId.isNotBlank()) { "Account ID must not be blank" }
        repository.initialize(
            accountId = input.accountId
        )
    }
}
