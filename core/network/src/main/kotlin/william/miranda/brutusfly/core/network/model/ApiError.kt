package william.miranda.brutusfly.core.network.model

import kotlinx.serialization.SerializationException
import java.io.IOException

/** Expected transport failures. Cancellation and programming errors are not represented here. */
sealed interface ApiError {
    data class Http(
        val responseCode: Int,
        val headers: Map<String, List<String>>,
        val errorBody: String?,
        val errorBodyTruncated: Boolean = false
    ) : ApiError {
        // Error responses can contain credentials or other sensitive server-side details.
        override fun toString(): String = "Http(responseCode=$responseCode, errorBodyTruncated=$errorBodyTruncated)"
    }

    data class Network(
        val cause: IOException
    ) : ApiError

    data class Serialization(
        val cause: SerializationException
    ) : ApiError
}
