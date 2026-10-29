package william.miranda.brutusfly.domain.usecase.selectedaccount

import kotlinx.coroutines.flow.Flow
import william.miranda.brutusfly.core.common.usecase.UseCase
import william.miranda.brutusfly.domain.repository.selectedaccount.SelectedAccountRepository
import javax.inject.Inject

class ObserveSelectedAccountUseCase @Inject constructor(
    private val repository: SelectedAccountRepository
) : UseCase<Unit, Flow<String?>> {
    override fun invoke(
        input: Unit
    ): Flow<String?> = repository.selectedAccountId
}
