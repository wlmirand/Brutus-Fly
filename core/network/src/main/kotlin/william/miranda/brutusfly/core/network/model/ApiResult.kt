package william.miranda.brutusfly.core.network.model

/** Transport result consumed by repositories, never exposed as a domain or UI contract. */
sealed interface ApiResult<out T> {
    data class Success<T>(
        val data: T,
        val responseCode: Int,
        val headers: Map<String, List<String>>
    ) : ApiResult<T>

    data class Error(
        val error: ApiError
    ) : ApiResult<Nothing>
}
