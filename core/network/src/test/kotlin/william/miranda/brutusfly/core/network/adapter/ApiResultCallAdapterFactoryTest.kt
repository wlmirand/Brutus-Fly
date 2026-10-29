package william.miranda.brutusfly.core.network.adapter

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import william.miranda.brutusfly.core.network.model.ApiError
import william.miranda.brutusfly.core.network.model.ApiResult
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

@OptIn(ExperimentalCoroutinesApi::class)
class ApiResultCallAdapterFactoryTest {
    @Test
    fun suspendSuccessPreservesBodyStatusAndRepeatedHeaders() = runTest {
        val api = api(client { request ->
            response(request, 201, "\"hello\"").newBuilder()
                .addHeader("X-Test", "first")
                .addHeader("X-Test", "second")
                .build()
        })

        val result = api.text()

        assertTrue(result is ApiResult.Success)
        result as ApiResult.Success
        assertEquals("hello", result.data)
        assertEquals(201, result.responseCode)
        assertEquals(listOf("first", "second"), result.headers["X-Test"])
    }

    @Test
    fun httpFailureRetainsItsBodyAndStatus() = runTest {
        val api = api(client { response(it, 401, "{\"error\":\"rejected\"}") })

        val failure = (api.text() as ApiResult.Error).error as ApiError.Http

        assertEquals(401, failure.responseCode)
        assertEquals("{\"error\":\"rejected\"}", failure.errorBody)
        assertFalse(failure.errorBodyTruncated)
        assertFalse(failure.toString().contains("rejected"))
    }

    @Test
    fun retainedErrorBodyIsLimitedToSixtyFourKiB() = runTest {
        val api = api(client { response(it, 500, "x".repeat(70_000)) })

        val failure = (api.text() as ApiResult.Error).error as ApiError.Http

        assertEquals(64 * 1024, failure.errorBody?.length)
        assertTrue(failure.errorBodyTruncated)
    }

    @Test
    fun connectivityFailureIsReturnedWithoutInventingAnHttpResponse() = runTest {
        val expected = IOException("offline")
        val api = api(client { throw expected })

        val failure = (api.text() as ApiResult.Error).error as ApiError.Network

        assertSame(expected, failure.cause)
    }

    @Test
    fun malformedJsonIsASerializationFailure() = runTest {
        val api = api(client { response(it, 200, "not-json") })

        val failure = (api.text() as ApiResult.Error).error

        assertTrue(failure is ApiError.Serialization)
    }

    @Test
    fun noContentIsSuccessfulForUnitButNotForRequiredPayloads() = runTest {
        val api = api(client { response(it, 204, "") })

        val unitResult = api.noContent() as ApiResult.Success
        val textResult = api.text() as ApiResult.Error

        assertEquals(Unit, unitResult.data)
        assertEquals(204, unitResult.responseCode)
        assertTrue(textResult.error is ApiError.Serialization)
    }

    @Test
    fun nullJsonCannotBecomeSuccessWithMissingRequiredData() = runTest {
        val api = api(client { response(it, 200, "null") })

        assertTrue((api.text() as ApiResult.Error).error is ApiError.Serialization)
    }

    @Test
    fun ordinaryRetrofitEndpointsAreNotAdapted() = runTest {
        val api = api(client { response(it, 200, "\"hello\"") })

        assertEquals("hello", api.unwrapped())
    }

    @Test
    fun synchronousCallsAndClonesUseTheSameContract() {
        val api = api(client { response(it, 200, "\"hello\"") })
        val call = api.textCall()
        val clone = call.clone()

        assertFalse(call.isExecuted)
        assertEquals("hello", (call.execute().body() as ApiResult.Success).data)
        assertTrue(call.isExecuted)
        assertFalse(clone.isExecuted)
        assertEquals("hello", (clone.execute().body() as ApiResult.Success).data)
    }

    @Test
    fun unexpectedConverterExceptionsPropagateUnchanged() = runTest {
        val expected = IllegalStateException("Converter bug")
        val converter = object : Converter.Factory() {
            override fun responseBodyConverter(
                type: Type,
                annotations: Array<Annotation>,
                retrofit: Retrofit,
            ): Converter<ResponseBody, *>? = if (type == String::class.java) {
                Converter<ResponseBody, String> { throw expected }
            } else null
        }
        val api = api(client { response(it, 200, "\"hello\"") }, converter)

        try {
            api.text()
            throw AssertionError("Expected the converter exception")
        } catch (exception: IllegalStateException) {
            // Coroutine stack-trace recovery may copy the exception and retain the original as its cause.
            assertEquals(expected.message, exception.message)
            assertTrue(exception === expected || exception.cause === expected)
        }
    }

    @Test
    fun ioDuringConversionIsNotMisclassifiedAsMalformedJson() = runTest {
        val expected = IOException("Body read failed")
        val converter = object : Converter.Factory() {
            override fun responseBodyConverter(
                type: Type,
                annotations: Array<Annotation>,
                retrofit: Retrofit,
            ): Converter<ResponseBody, *>? = if (type == String::class.java) {
                Converter<ResponseBody, String> { throw expected }
            } else null
        }
        val api = api(client { response(it, 200, "\"hello\"") }, converter)

        assertSame(expected, ((api.text() as ApiResult.Error).error as ApiError.Network).cause)
    }

    @Test
    fun cancellingSuspendRequestCancelsTheUnderlyingCall() = runTest {
        val entered = CompletableDeferred<Unit>()
        val release = CountDownLatch(1)
        val underlying = AtomicReference<Call>()
        val client = OkHttpClient.Builder()
            .eventListener(object : EventListener() {
                override fun callStart(call: Call) {
                    underlying.set(call)
                }
            })
            .addInterceptor { chain ->
                entered.complete(Unit)
                release.await()
                response(chain.request(), 200, "\"hello\"")
            }
            .build()
        try {
            val result = async { api(client).text() }
            runCurrent()
            entered.await()

            result.cancelAndJoin()

            assertTrue(result.isCancelled)
            assertTrue(underlying.get().isCanceled())
        } finally {
            release.countDown()
        }
    }

    private fun client(respond: (Request) -> Response): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain -> respond(chain.request()) }
        .build()

    private fun response(request: Request, code: Int, body: String): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(code)
        .message("Test response")
        .body(body.toResponseBody("application/json".toMediaType()))
        .build()

    private fun api(client: OkHttpClient, converter: Converter.Factory? = null): TestApi = Retrofit.Builder()
        .baseUrl("https://example.com/")
        .client(client)
        .addCallAdapterFactory(ApiResultCallAdapterFactory())
        .apply { if (converter != null) addConverterFactory(converter) }
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(TestApi::class.java)

    private interface TestApi {
        @GET("text")
        suspend fun text(): ApiResult<String>

        @GET("empty")
        suspend fun noContent(): ApiResult<Unit>

        @GET("text")
        fun textCall(): retrofit2.Call<ApiResult<String>>

        @GET("text")
        suspend fun unwrapped(): String
    }
}
