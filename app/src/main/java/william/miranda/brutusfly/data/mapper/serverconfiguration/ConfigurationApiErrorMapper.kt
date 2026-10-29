package william.miranda.brutusfly.data.mapper.serverconfiguration

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.network.model.ApiError
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationException
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationFailure
import javax.inject.Inject

/** Interprets transport failures in the context of verifying a Firefly III installation. */
class ConfigurationApiErrorMapper @Inject constructor() : Mapper<ApiError, ConfigurationException> {
    /** Distinguishes rejected credentials, unavailable servers, invalid responses and network failures. */
    override fun map(
        input: ApiError
    ): ConfigurationException = when (input) {
        is ApiError.Http -> ConfigurationException(
            failure = when (input.responseCode) {
                401, 403 -> ConfigurationFailure.UNAUTHORIZED
                in 500..599 -> ConfigurationFailure.SERVER
                else -> ConfigurationFailure.INVALID_SERVER
            }
        )
        is ApiError.Network -> ConfigurationException(
            failure = ConfigurationFailure.NETWORK,
            cause = input.cause
        )
        is ApiError.Serialization -> ConfigurationException(
            failure = ConfigurationFailure.INVALID_SERVER,
            cause = input.cause
        )
    }
}
