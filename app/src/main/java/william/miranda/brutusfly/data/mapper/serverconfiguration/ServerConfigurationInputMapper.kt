package william.miranda.brutusfly.data.mapper.serverconfiguration

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationException
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationFailure
import william.miranda.brutusfly.domain.model.serverconfiguration.ServerConfiguration
import javax.inject.Inject

/** Validates user-entered connection settings without making a network request. */
class ServerConfigurationInputMapper @Inject constructor() :
    Mapper<ServerConfigurationInputMapper.Input, ServerConfiguration> {
    /** Raw form values; a regular class avoids generating a toString that exposes the token. */
    class Input(
        val baseUrl: String,
        val personalAccessToken: String
    )

    /**
     * Trims the values, requires HTTPS, and rejects URL credentials, queries, fragments and whitespace.
     * Requires a nonempty token containing only printable, non-space ASCII characters.
     * Preserves the installation path and normalizes its trailing slash for Retrofit.
     * Throws ConfigurationException with a field-specific failure when validation fails.
     */
    override fun map(
        input: Input
    ): ServerConfiguration {
        val rawUrl = input.baseUrl.trim()
        val url = rawUrl.toHttpUrlOrNull()
            ?: throw ConfigurationException(
                failure = ConfigurationFailure.INVALID_URL
            )
        if (!url.isHttps) throw ConfigurationException(
            failure = ConfigurationFailure.HTTPS_REQUIRED
        )
        if (rawUrl.any { it.isWhitespace() || it.isISOControl() } ||
            url.username.isNotEmpty() || url.password.isNotEmpty() ||
            url.query != null || url.fragment != null
        ) {
            throw ConfigurationException(
                failure = ConfigurationFailure.INVALID_URL
            )
        }
        val token = input.personalAccessToken.trim()
        if (token.isEmpty() || token.any { it.code !in 33..126 }) {
            throw ConfigurationException(
                failure = ConfigurationFailure.INVALID_TOKEN
            )
        }
        val normalized = url.newBuilder()
            .encodedPath(
                encodedPath = url.encodedPath.trimEnd(
                    '/'
                ) + "/"
            )
            .build()
            .toString()
        return ServerConfiguration(
            baseUrl = normalized,
            personalAccessToken = token
        )
    }
}
