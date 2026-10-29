package william.miranda.brutusfly.data.mapper.account

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.network.model.ApiError
import william.miranda.brutusfly.domain.model.account.AccountFailure
import william.miranda.brutusfly.domain.model.account.AccountLoadException
import javax.inject.Inject

/** Keeps transport error types and HTTP status codes outside the account domain contract. */
class AccountApiErrorMapper @Inject constructor() : Mapper<ApiError, AccountLoadException> {
    /** Translates known HTTP and connectivity failures, preserving exception causes when available. */
    override fun map(
        input: ApiError
    ): AccountLoadException = when (input) {
        is ApiError.Http -> AccountLoadException(
            failure = when (input.responseCode) {
                401, 403 -> AccountFailure.UNAUTHORIZED
                404 -> AccountFailure.NOT_FOUND
                in 500..599 -> AccountFailure.SERVER
                else -> AccountFailure.UNKNOWN
            }
        )
        is ApiError.Network -> AccountLoadException(
            failure = AccountFailure.NETWORK,
            cause = input.cause
        )
        is ApiError.Serialization -> AccountLoadException(
            failure = AccountFailure.UNKNOWN,
            cause = input.cause
        )
    }
}
