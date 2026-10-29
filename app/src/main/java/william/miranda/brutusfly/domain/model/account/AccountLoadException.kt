package william.miranda.brutusfly.domain.model.account

enum class AccountFailure {
    NOT_FOUND,
    NETWORK,
    UNAUTHORIZED,
    SERVER,
    UNKNOWN,
}

class AccountLoadException(
    val failure: AccountFailure,
    cause: Throwable? = null
) : Exception(cause)
