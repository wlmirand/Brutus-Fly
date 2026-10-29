package william.miranda.brutusfly.domain.usecase.serverconfiguration

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.repository.serverconfiguration.ServerConfigurationRepository
import javax.inject.Inject

class ConfigureServerUseCase @Inject constructor(
    private val repository: ServerConfigurationRepository
) : SuspendUseCase<ConfigureServerUseCase.Input, Unit> {
    class Input(
        val baseUrl: String,
        val personalAccessToken: String
    )

    override suspend fun invoke(
        input: Input
    ) {
        repository.configure(
            baseUrl = input.baseUrl,
            personalAccessToken = input.personalAccessToken
        )
    }
}
