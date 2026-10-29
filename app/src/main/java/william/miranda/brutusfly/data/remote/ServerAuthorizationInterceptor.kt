package william.miranda.brutusfly.data.remote

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import william.miranda.brutusfly.domain.model.serverconfiguration.ServerConfiguration
import java.io.IOException

/** Credentials may only be sent to the configured HTTPS origin and installation path. */
class ServerAuthorizationInterceptor(
    configuration: ServerConfiguration
) : Interceptor {
    private val baseUrl = configuration.baseUrl.toHttpUrl()
    private val token = configuration.personalAccessToken

    /**
     * Rejects requests outside the configured server before adding any credentials.
     * Adds the Bearer token and JSON Accept header to allowed requests, then executes the next step.
     * Throws IOException when the request destination is not allowed.
     */
    override fun intercept(
        chain: Interceptor.Chain
    ): Response {
        val request = chain.request()
        if (!isAllowed(
            url = request.url
        )) throw IOException(
            "Request outside configured server"
        )
        return chain.proceed(
            request = request.newBuilder()
                .header(
                    name = "Authorization",
                    value = "Bearer $token"
                )
                .header(
                    name = "Accept",
                    value = "application/json"
                )
                .build()
        )
    }

    /** Checks HTTPS, exact host and port, and the configured installation path prefix. */
    private fun isAllowed(
        url: HttpUrl
    ): Boolean = url.isHttps &&
        url.host == baseUrl.host && url.port == baseUrl.port &&
        url.encodedPath.startsWith(
            prefix = baseUrl.encodedPath
        )
}
