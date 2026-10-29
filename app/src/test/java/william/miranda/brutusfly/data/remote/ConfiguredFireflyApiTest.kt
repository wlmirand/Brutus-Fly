package william.miranda.brutusfly.data.remote

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import william.miranda.brutusfly.data.local.ServerConfigurationStore
import william.miranda.brutusfly.core.network.model.ApiResult
import william.miranda.brutusfly.domain.model.ServerConfiguration

class ConfiguredFireflyApiTest {
    @Test
    fun initializesRequestsFromCurrentSavedConfiguration() = runTest {
        val requests = mutableListOf<Request>()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            requests += chain.request()
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("""{"data":{"version":"6.0.0"}}""".toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        val store = object : ServerConfigurationStore {
            override val configuration = MutableStateFlow<ServerConfiguration?>(
                ServerConfiguration("https://first.example.com/firefly/", "first-token"),
            )
            override suspend fun save(configuration: ServerConfiguration) {
                this.configuration.value = configuration
            }
            override suspend fun clear() {
                configuration.value = null
            }
        }
        val api = ConfiguredFireflyApi(store, FireflyApiFactory(client, Json))

        assertTrue(api.getAbout() is ApiResult.Success)
        store.save(ServerConfiguration("https://second.example.com/", "second-token"))
        assertTrue(api.getAbout() is ApiResult.Success)

        assertEquals("https://first.example.com/firefly/api/v1/about", requests[0].url.toString())
        assertEquals("Bearer first-token", requests[0].header("Authorization"))
        assertEquals("https://second.example.com/api/v1/about", requests[1].url.toString())
        assertEquals("Bearer second-token", requests[1].header("Authorization"))
    }
}
