package william.miranda.brutusfly.data.mapper.account

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.network.model.ApiError
import william.miranda.brutusfly.data.mapper.transaction.TransactionApiErrorMapper
import william.miranda.brutusfly.domain.model.transaction.TransactionFailure
import william.miranda.brutusfly.domain.model.transaction.TransactionLoadException
import javax.inject.Inject

/** Interprets missing account resources only for the account-scoped transaction endpoint. */
class AccountTransactionApiErrorMapper @Inject constructor(
    private val transactionErrorMapper: TransactionApiErrorMapper
) : Mapper<ApiError, TransactionLoadException> {
    /** Keeps authentication and transient failures distinct from a missing account resource. */
    override fun map(
        input: ApiError
    ): TransactionLoadException =
        if (input is ApiError.Http && input.responseCode == 404) {
            TransactionLoadException(
                failure = TransactionFailure.ACCOUNT_UNAVAILABLE
            )
        } else {
            transactionErrorMapper.map(
                input = input
            )
        }
}
