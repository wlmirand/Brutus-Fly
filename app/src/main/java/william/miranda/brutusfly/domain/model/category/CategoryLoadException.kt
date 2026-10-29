package william.miranda.brutusfly.domain.model.category

enum class CategoryFailure {
    NETWORK,
    UNAUTHORIZED,
    SERVER,
    UNKNOWN,
}

class CategoryLoadException(
    val failure: CategoryFailure,
    cause: Throwable? = null
) : Exception(cause)
