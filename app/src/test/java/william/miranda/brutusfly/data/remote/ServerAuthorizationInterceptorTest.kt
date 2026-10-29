package william.miranda.brutusfly.data.remote

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertThrows
import org.junit.Test
import william.miranda.brutusfly.domain.model.ServerConfiguration
import java.io.IOException

class ServerAuthorizationInterceptorTest {
    @Test
    fun rejectsHttpAndRequestsOutsideTheConfiguredOriginOrPathBeforeSending() {
        val client = OkHttpClient.Builder()
            .addInterceptor(
                ServerAuthorizationInterceptor(ServerConfiguration("https://example.com/firefly/", "token")),
            )
            .addInterceptor { throw AssertionError("An unauthorized request must not be sent") }
            .build()

        listOf(
            "http://example.com/firefly/api/v1/about",
            "https://other.example.com/firefly/api/v1/about",
            "https://example.com:8443/firefly/api/v1/about",
            "https://example.com/other/api/v1/about",
            "https://example.com/firefly-other/api/v1/about",
        ).forEach { url ->
            assertThrows(IOException::class.java) {
                client.newCall(Request.Builder().url(url).build()).execute().close()
            }
        }
    }
}
