package william.miranda.brutusfly.core.network.adapter

import kotlinx.serialization.SerializationException
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import william.miranda.brutusfly.core.network.model.ApiError
import william.miranda.brutusfly.core.network.model.ApiResult
import java.io.IOException
import java.util.concurrent.CancellationException

/** Wraps expected transport failures as values while forwarding cancellation and unexpected failures. */
internal class ApiResultCall<T>(
    private val delegate: Call<ResponseBody>,
    private val converter: Converter<ResponseBody, T>?,
    private val isUnit: Boolean
) : Call<ApiResult<T>> {
    /** Converts an asynchronous response before delivering it to Retrofit's suspend bridge or caller. */
    override fun enqueue(
        callback: Callback<ApiResult<T>>
    ) {
        delegate.enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val result = try {
                        responseToResult(
                            response = response
                        )
                    } catch (exception: Exception) {
                        callback.onFailure(
                            this@ApiResultCall,
                            exception
                        )
                        return
                    }
                    callback.onResponse(
                        this@ApiResultCall,
                        Response.success(
                            result
                        )
                    )
                }
    
                override fun onFailure(
                    call: Call<ResponseBody>,
                    throwable: Throwable
                ) {
                    if (delegate.isCanceled) {
                        callback.onFailure(
                            this@ApiResultCall,
                            cancellation(
                                cause = throwable
                            )
                        )
                        return
                    }
                    val failure = expectedFailure(
                        throwable = throwable
                    )
                    if (failure == null) {
                        callback.onFailure(
                            this@ApiResultCall,
                            throwable
                        )
                    } else {
                        callback.onResponse(
                            this@ApiResultCall,
                            Response.success(
                                failure
                            )
                        )
                    }
                }
            }
        )
    }

    /** Applies the same result contract to callers using Retrofit's synchronous Call API. */
    override fun execute(): Response<ApiResult<T>> {
        val result = try {
            responseToResult(response = delegate.execute())
        } catch (exception: Exception) {
            if (delegate.isCanceled) throw cancellation(cause = exception)
            expectedFailure(throwable = exception) ?: throw exception
        }
        return Response.success(result)
    }

    /** Preserves HTTP metadata, closes bodies and converts only successful payloads. */
    private fun responseToResult(
        response: Response<ResponseBody>
    ): ApiResult<T> {
        if (delegate.isCanceled) {
            response.body()?.close()
            response.errorBody()?.close()
            throw cancellation()
        }
        val headers = response.headers().toMultimap()
        if (!response.isSuccessful) {
            val (body, truncated) = readErrorBody(body = response.errorBody())

            return ApiResult.Error(
                error = ApiError.Http(
                    responseCode = response.code(),
                    headers = headers,
                    errorBody = body,
                    errorBodyTruncated = truncated
                )
            )
        }

        val body = response.body()
        if (isUnit) {
            body?.close()
            @Suppress("UNCHECKED_CAST")
            return ApiResult.Success(
                data = Unit as T,
                responseCode = response.code(),
                headers = headers
            )
        }
        if (body == null) return emptyBodyFailure()

        return body.use {
            try {
                val data = checkNotNull(converter).convert(body) ?: return emptyBodyFailure()
                ApiResult.Success(
                    data = data,
                    responseCode = response.code(),
                    headers = headers
                )
            } catch (exception: SerializationException) {
                ApiResult.Error(
                    error = ApiError.Serialization(
                        cause = exception
                    )
                )
            } catch (exception: IOException) {
                if (delegate.isCanceled) throw cancellation(
                    cause = exception
                )
                ApiResult.Error(
                    error = ApiError.Network(
                        cause = exception
                    )
                )
            }
        }
    }

    /** Retains at most 64 KiB of error-body bytes; a failed read does not discard the known HTTP status. */
    private fun readErrorBody(
        body: ResponseBody?
    ): Pair<String?, Boolean> {
        if (body == null) return null to false
        return body.use {
            try {
                val source = body.source()
                source.request(
                    byteCount = MAX_ERROR_BODY_BYTES + 1
                )
                val truncated = source.buffer.size > MAX_ERROR_BODY_BYTES
                val bytes = source.readByteArray(
                    byteCount = minOf(source.buffer.size, MAX_ERROR_BODY_BYTES)
                )
                val charset = body.contentType()?.charset(
                    defaultValue = Charsets.UTF_8
                ) ?: Charsets.UTF_8
                bytes.toString(charset = charset) to truncated
            } catch (exception: IOException) {
                if (delegate.isCanceled) throw cancellation(cause = exception)
                null to false
            }
        }
    }

    /** Classifies only known failures; null means the original exception must propagate unchanged. */
    private fun expectedFailure(
        throwable: Throwable
    ): ApiResult.Error? = when (throwable) {
        is SerializationException -> ApiResult.Error(
            error = ApiError.Serialization(
                cause = throwable
            )
        )
        is IOException -> ApiResult.Error(
            error = ApiError.Network(
                cause = throwable
            )
        )
        else -> null
    }

    /** A successful HTTP response without the required payload violates the declared endpoint contract. */
    private fun emptyBodyFailure(): ApiResult.Error =
        ApiResult.Error(
            error = ApiError.Serialization(
                cause = SerializationException(
                    message = "Expected a non-null response body"
                )
            )
        )

    /** Preserves cancellation as control flow rather than reporting it as a connectivity problem. */
    private fun cancellation(
        cause: Throwable? = null
    ): CancellationException =
        CancellationException(
            "API call was cancelled"
        ).apply {
            if (cause != null) initCause(
                cause
            )
        }

    override fun clone(): Call<ApiResult<T>> = ApiResultCall(
        delegate = delegate.clone(),
        converter = converter,
        isUnit = isUnit
    )
    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun cancel() = delegate.cancel()
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()

    private companion object {
        const val MAX_ERROR_BODY_BYTES = 64L * 1024
    }
}
