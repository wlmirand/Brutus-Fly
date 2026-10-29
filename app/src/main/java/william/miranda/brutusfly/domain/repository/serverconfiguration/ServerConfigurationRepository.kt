package william.miranda.brutusfly.domain.repository.serverconfiguration

import kotlinx.coroutines.flow.Flow

interface ServerConfigurationRepository {
    val isConfigured: Flow<Boolean>
    suspend fun configure(
        baseUrl: String,
        personalAccessToken: String
    )
    suspend fun clear()
}
