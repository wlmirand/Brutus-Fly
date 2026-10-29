package william.miranda.brutusfly.domain.model.transaction

enum class TransactionFailure {
    ACCOUNT_UNAVAILABLE,
    ACCOUNT_NOT_SELECTABLE,
    NETWORK,
    UNAUTHORIZED,
    SERVER,
    UNKNOWN,
}

class TransactionLoadException(
    val failure: TransactionFailure,
    cause: Throwable? = null
) : Exception(cause)
