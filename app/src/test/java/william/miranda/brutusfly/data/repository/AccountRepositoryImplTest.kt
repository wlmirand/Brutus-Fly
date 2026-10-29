package william.miranda.brutusfly.data.repository

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
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.mapper.AccountApiErrorMapper
import william.miranda.brutusfly.data.mapper.AccountDtoMapper
import william.miranda.brutusfly.data.mapper.AccountPageDtoMapper
import william.miranda.brutusfly.data.remote.FireflyApiFactory
import william.miranda.brutusfly.domain.model.AccountFailure
import william.miranda.brutusfly.domain.model.AccountLoadException
import william.miranda.brutusfly.domain.model.ServerConfiguration

class AccountRepositoryImplTest {
    @Test
    fun requestsTheAccountEndpointWithPaginationAndAuthentication() = runTest {
        var request: Request? = null
        val repository = repository { incoming ->
            request = incoming
            response(incoming, 200, """{"data":[{"id":"account","attributes":{"name":"Savings","type":"asset","active":true,"current_balance":"50.00","currency_code":"EUR"}}],"meta":{"pagination":{"current_page":2,"total_pages":3}}}""")
        }

        val result = repository.getAccounts(page = 2, limit = 10)

        assertEquals("/firefly/api/v1/accounts", request?.url?.encodedPath)
        assertEquals("2", request?.url?.queryParameter("page"))
        assertEquals("10", request?.url?.queryParameter("limit"))
        assertEquals("Bearer test-token", request?.header("Authorization"))
        assertEquals("Savings", result.accounts.single().name)
        assertEquals("50.00".toBigDecimal(), result.accounts.single().currentBalance)
        assertEquals(3, result.nextPage)
    }

    @Test
    fun httpFailuresAreTranslatedIntoDomainFailures() = runTest {
        val repository = repository { response(it, 401, "{}") }

        try {
            repository.getAccounts(1, 50)
            throw AssertionError("Expected a domain failure")
        } catch (exception: AccountLoadException) {
            assertEquals(AccountFailure.UNAUTHORIZED, exception.failure)
        }
    }

    @Test
    fun serverReturningADifferentPageIsRejected() = runTest {
        val repository = repository {
            response(it, 200, """{"data":[],"meta":{"pagination":{"current_page":1,"total_pages":3}}}""")
        }
        try {
            repository.getAccounts(2, 50)
            throw AssertionError("Expected inconsistent pagination to fail")
        } catch (exception: AccountLoadException) {
            assertEquals(AccountFailure.UNKNOWN, exception.failure)
            assertTrue(exception.cause is MappingException)
        }
    }

    private fun repository(respond: (Request) -> Response): AccountRepositoryImpl {
        val client = OkHttpClient.Builder().addInterceptor { respond(it.request()) }.build()
        val api = FireflyApiFactory(client, Json { ignoreUnknownKeys = true })
            .create(ServerConfiguration("https://example.com/firefly/", "test-token"))
        return AccountRepositoryImpl(api, AccountPageDtoMapper(AccountDtoMapper()), AccountApiErrorMapper())
    }

    private fun response(request: Request, code: Int, body: String) = Response.Builder()
        .request(request).protocol(Protocol.HTTP_1_1).code(code).message("Test response")
        .body(body.toResponseBody("application/json".toMediaType())).build()
}
