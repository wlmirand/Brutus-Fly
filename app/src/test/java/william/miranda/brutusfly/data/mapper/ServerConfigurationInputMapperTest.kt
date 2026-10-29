package william.miranda.brutusfly.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.domain.model.ConfigurationException
import william.miranda.brutusfly.domain.model.ConfigurationFailure
import william.miranda.brutusfly.domain.model.ServerConfiguration

class ServerConfigurationInputMapperTest {
    private val mapper: Mapper<ServerConfigurationInputMapper.Input, ServerConfiguration> =
        ServerConfigurationInputMapper()

    @Test
    fun normalizesUrlAndPreservesInstallationPath() {
        val result = mapper.map(ServerConfigurationInputMapper.Input(" https://EXAMPLE.com/firefly ", " token "))

        assertEquals("https://example.com/firefly/", result.baseUrl)
        assertEquals("token", result.personalAccessToken)
    }

    @Test
    fun rejectsInsecureUrl() {
        assertInvalid("http://example.com/", "token", ConfigurationFailure.HTTPS_REQUIRED)
    }

    @Test
    fun rejectsCredentialsQueryFragmentAndMalformedUrls() {
        listOf(
            "not a url",
            "https://user:password@example.com/",
            "https://example.com/?token=secret",
            "https://example.com/#fragment",
            "https://example.com/a b",
        ).forEach { assertInvalid(it, "token", ConfigurationFailure.INVALID_URL) }
    }

    @Test
    fun rejectsBlankAndHeaderInjectionTokens() {
        listOf("", "  ", "invalid token", "token\r\nHeader: injected").forEach {
            assertInvalid("https://example.com/", it, ConfigurationFailure.INVALID_TOKEN)
        }
    }

    private fun assertInvalid(url: String, token: String, expected: ConfigurationFailure) {
        val exception = assertThrows(ConfigurationException::class.java) {
            mapper.map(ServerConfigurationInputMapper.Input(url, token))
        }
        assertEquals(expected, exception.failure)
    }
}
