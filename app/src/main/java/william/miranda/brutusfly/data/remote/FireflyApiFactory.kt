package william.miranda.brutusfly.data.remote

import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import william.miranda.brutusfly.core.network.adapter.ApiResultCallAdapterFactory
import william.miranda.brutusfly.domain.model.serverconfiguration.ServerConfiguration
import javax.inject.Inject
import javax.inject.Singleton

/** Builds authenticated API clients for a supplied configuration, including settings not yet saved. */
@Singleton
class FireflyApiFactory @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json
) {
    /**
     * Creates Retrofit with the configured HTTPS base URL, JSON converter and Bearer interceptor.
     * Derives an OkHttp client from the shared client, reusing its connection pool and dispatcher.
     * Disables redirects so an authenticated request cannot be redirected to another destination.
     * Does not perform a request or persist configuration; invalid base URLs fail immediately.
     */
    fun create(
        configuration: ServerConfiguration
    ): FireflyApi {
        require(
            value = configuration.baseUrl.toHttpUrl().isHttps
        ) { "HTTPS is required" }
        val authenticatedClient = client.newBuilder()
            .followRedirects(
                followRedirects = false
            )
            .followSslRedirects(
                followProtocolRedirects = false
            )
            .apply {
                interceptors().add(
                    0,
                    ServerAuthorizationInterceptor(
                        configuration = configuration
                    )
                )
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(
                configuration.baseUrl
            )
            .client(
                authenticatedClient
            )
            .addCallAdapterFactory(
                ApiResultCallAdapterFactory()
            )
            .addConverterFactory(
                json.asConverterFactory(
                    contentType = "application/json".toMediaType()
                )
            )
            .build()
            .create(
                FireflyApi::class.java
            )
    }
}
