package william.miranda.brutusfly.domain.usecase.serverconfiguration

import kotlinx.coroutines.flow.Flow
import william.miranda.brutusfly.core.common.usecase.UseCase
import william.miranda.brutusfly.domain.repository.serverconfiguration.ServerConfigurationRepository
import javax.inject.Inject

class ObserveServerConfigurationUseCase @Inject constructor(
    private val repository: ServerConfigurationRepository
) : UseCase<Unit, Flow<Boolean>> {
    override fun invoke(
        input: Unit
    ): Flow<Boolean> = repository.isConfigured
}
