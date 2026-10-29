package william.miranda.brutusfly.data.repository.serverconfiguration

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import william.miranda.brutusfly.core.network.model.ApiResult
import william.miranda.brutusfly.data.local.datastore.serverconfiguration.ServerConfigurationStore
import william.miranda.brutusfly.data.mapper.serverconfiguration.ConfigurationApiErrorMapper
import william.miranda.brutusfly.data.mapper.serverconfiguration.ServerConfigurationInputMapper
import william.miranda.brutusfly.data.remote.FireflyApiFactory
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationException
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationFailure
import william.miranda.brutusfly.domain.repository.serverconfiguration.ServerConfigurationRepository
import javax.inject.Inject

/** Coordinates connection-input validation, remote server verification and secure persistence. */
class ServerConfigurationRepositoryImpl @Inject constructor(
    private val store: ServerConfigurationStore,
    private val mapper: ServerConfigurationInputMapper,
    private val apiFactory: FireflyApiFactory,
    private val errorMapper: ConfigurationApiErrorMapper
) : ServerConfigurationRepository {
    /**
     * Exposes configuration presence without exposing credentials to presentation code.
     * Suppresses repeated presence values and propagates storage or decryption failures.
     */
    override val isConfigured = store.configuration.map { it != null }.distinctUntilChanged()

    /**
     * Normalizes the inputs, authenticates against the about endpoint and checks for a nonblank version.
     * Saves only after verification succeeds, leaving previously stored settings intact on validation failure.
     * Maps ApiResult errors to configuration failures; cancellation and unexpected exceptions propagate.
     * Storage errors propagate from the store after remote validation.
     */
    override suspend fun configure(
        baseUrl: String,
        personalAccessToken: String
    ) {
        val configuration = mapper.map(
            input = ServerConfigurationInputMapper.Input(
                baseUrl = baseUrl,
                personalAccessToken = personalAccessToken
            )
        )
        when (val result = apiFactory.create(
            configuration = configuration
        ).getAbout()) {
            is ApiResult.Success -> {
                if (result.data.data.version.isBlank()) {
                    throw ConfigurationException(
                        failure = ConfigurationFailure.INVALID_SERVER
                    )
                }
            }
            is ApiResult.Error -> throw errorMapper.map(
                input = result.error
            )
        }
        // Validation must succeed before atomically replacing any saved credentials.
        store.save(
            configuration = configuration
        )
    }

    /** Removes persisted connection settings; observers then receive false if storage remains readable. */
    override suspend fun clear() = store.clear()
}
