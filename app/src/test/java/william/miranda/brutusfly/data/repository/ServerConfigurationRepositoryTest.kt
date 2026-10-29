package william.miranda.brutusfly.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import william.miranda.brutusfly.data.local.ServerConfigurationStore
import william.miranda.brutusfly.data.mapper.ServerConfigurationInputMapper
import william.miranda.brutusfly.data.mapper.ConfigurationApiErrorMapper
import william.miranda.brutusfly.data.remote.FireflyApiFactory
import william.miranda.brutusfly.domain.model.ConfigurationException
import william.miranda.brutusfly.domain.model.ConfigurationFailure
import william.miranda.brutusfly.domain.model.ServerConfiguration

class ServerConfigurationRepositoryTest {
    @Test
    fun validatesWithBearerAndPersistsOnlyAfterSuccess() = runTest {
        val store = MemoryStore()
        var validated = false
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            assertNull(store.configuration.value)
            assertEquals("https://example.com/firefly/api/v1/about", chain.request().url.toString())
            assertEquals("Bearer test-token", chain.request().header("Authorization"))
            assertEquals("application/json", chain.request().header("Accept"))
            validated = true
            response(chain.request(), 200, """{"data":{"version":"6.0.0","other":"ignored"}}""")
        }.build()
        val repository = repository(store, client)
        assertFalse(repository.isConfigured.first())

        repository.configure("https://example.com/firefly", "test-token")

        assertTrue(validated)
        assertNotNull(store.configuration.value)
        assertEquals("https://example.com/firefly/", store.configuration.value?.baseUrl)
        assertTrue(repository.isConfigured.first())
    }

    @Test
    fun rejectedTokenDoesNotOverwriteExistingConfiguration() = runTest {
        val previous = ServerConfiguration("https://old.example.com/", "old-token")
        val store = MemoryStore(previous)
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            response(chain.request(), 401, "{}")
        }.build()

        try {
            repository(store, client).configure("https://example.com/", "invalid-token")
            throw AssertionError("Expected configuration validation to fail")
        } catch (exception: ConfigurationException) {
            assertEquals(ConfigurationFailure.UNAUTHORIZED, exception.failure)
        }
        assertSame(previous, store.configuration.value)
    }

    @Test
    fun htmlOrMissingVersionIsRejectedWithoutSaving() = runTest {
        listOf("<html>Login</html>", """{"data":{}}""", """{"data":{"version":""}}""").forEach { body ->
            val store = MemoryStore()
            val client = OkHttpClient.Builder().addInterceptor { chain ->
                response(chain.request(), 200, body)
            }.build()
            try {
                repository(store, client).configure("https://example.com/", "token")
                throw AssertionError("Expected invalid Firefly response to fail")
            } catch (exception: ConfigurationException) {
                assertEquals(ConfigurationFailure.INVALID_SERVER, exception.failure)
            }
            assertNull(store.configuration.value)
        }
    }

    @Test
    fun redirectIsRejectedWithoutSaving() = runTest {
        val store = MemoryStore()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            response(chain.request(), 302, "{}").newBuilder()
                .header("Location", "https://other.example.com/")
                .build()
        }.build()
        try {
            repository(store, client).configure("https://example.com/", "token")
            throw AssertionError("Expected redirect to fail")
        } catch (exception: ConfigurationException) {
            assertEquals(ConfigurationFailure.INVALID_SERVER, exception.failure)
        }
        assertNull(store.configuration.value)
    }

    private fun repository(store: MemoryStore, client: OkHttpClient) = ServerConfigurationRepositoryImpl(
        store,
        ServerConfigurationInputMapper(),
        FireflyApiFactory(client, Json { ignoreUnknownKeys = true }),
        ConfigurationApiErrorMapper(),
    )

    private fun response(request: okhttp3.Request, code: Int, body: String) = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(code)
        .message("Test response")
        .body(body.toResponseBody("application/json".toMediaType()))
        .build()

    private class MemoryStore(initial: ServerConfiguration? = null) : ServerConfigurationStore {
        override val configuration = MutableStateFlow(initial)
        override suspend fun save(configuration: ServerConfiguration) {
            this.configuration.value = configuration
        }
        override suspend fun clear() {
            configuration.value = null
        }
    }
}
