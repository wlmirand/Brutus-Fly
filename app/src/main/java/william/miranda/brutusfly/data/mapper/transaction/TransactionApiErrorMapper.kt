package william.miranda.brutusfly.data.mapper.transaction

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.network.model.ApiError
import william.miranda.brutusfly.domain.model.transaction.TransactionFailure
import william.miranda.brutusfly.domain.model.transaction.TransactionLoadException
import javax.inject.Inject

/** Translates transport failures without exposing HTTP details to the transaction use case. */
class TransactionApiErrorMapper @Inject constructor() : Mapper<ApiError, TransactionLoadException> {
    /** Maps known HTTP statuses and connectivity failures, preserving causes when available. */
    override fun map(
        input: ApiError
    ): TransactionLoadException = when (input) {
        is ApiError.Http -> TransactionLoadException(
            failure = when (input.responseCode) {
                401, 403 -> TransactionFailure.UNAUTHORIZED
                in 500..599 -> TransactionFailure.SERVER
                else -> TransactionFailure.UNKNOWN
            }
        )
        is ApiError.Network -> TransactionLoadException(
            failure = TransactionFailure.NETWORK,
            cause = input.cause
        )
        is ApiError.Serialization -> TransactionLoadException(
            failure = TransactionFailure.UNKNOWN,
            cause = input.cause
        )
    }
}
