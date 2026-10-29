package william.miranda.brutusfly.domain.usecase.serverconfiguration

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.repository.serverconfiguration.ServerConfigurationRepository
import javax.inject.Inject

class ClearServerConfigurationUseCase @Inject constructor(
    private val repository: ServerConfigurationRepository
) : SuspendUseCase<Unit, Unit> {
    override suspend fun invoke(
        input: Unit
    ) = repository.clear()
}
