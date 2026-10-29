package william.miranda.brutusfly.core.network.adapter

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import william.miranda.brutusfly.core.network.model.ApiResult
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/** Supports suspend endpoints returning ApiResult<T> and regular endpoints returning Call<ApiResult<T>>. */
class ApiResultCallAdapterFactory : CallAdapter.Factory() {
    /** Recognizes the result wrapper and obtains its converter, leaving unrelated endpoints untouched. */
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java || returnType !is ParameterizedType) return null

        val resultType = getParameterUpperBound(0, returnType)
        if (getRawType(resultType) != ApiResult::class.java) return null
        require(resultType is ParameterizedType) { "ApiResult must declare its response body type" }
        val bodyType = getParameterUpperBound(0, resultType)

        val isUnit = getRawType(bodyType) == Unit::class.java
        val converter = if (isUnit) null else retrofit.responseBodyConverter<Any>(bodyType, annotations)

        return ApiResultCallAdapter(
            converter = converter,
            isUnit = isUnit
        )
    }
}

private class ApiResultCallAdapter<T>(
    private val converter: Converter<ResponseBody, T>?,
    private val isUnit: Boolean
) : CallAdapter<ResponseBody, Call<ApiResult<T>>> {
    // Read raw bodies so conversion errors and empty responses can be classified explicitly.
    override fun responseType(): Type = ResponseBody::class.java

    override fun adapt(call: Call<ResponseBody>): Call<ApiResult<T>> = ApiResultCall(
        delegate = call,
        converter = converter,
        isUnit = isUnit
    )
}
