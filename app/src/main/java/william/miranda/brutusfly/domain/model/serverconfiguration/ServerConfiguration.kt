package william.miranda.brutusfly.domain.model.serverconfiguration

/** Deliberately not a data class: generated toString must not expose the token. */
class ServerConfiguration(
    val baseUrl: String,
    val personalAccessToken: String
)

enum class ConfigurationFailure {
    INVALID_URL,
    HTTPS_REQUIRED,
    INVALID_TOKEN,
    UNAUTHORIZED,
    NETWORK,
    SERVER,
    INVALID_SERVER,
    STORAGE,
    UNKNOWN,
}

class ConfigurationException(
    val failure: ConfigurationFailure,
    cause: Throwable? = null
) : Exception(cause)
