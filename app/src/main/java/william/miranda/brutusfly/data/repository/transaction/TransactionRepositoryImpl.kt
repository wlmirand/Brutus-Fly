package william.miranda.brutusfly.data.repository.transaction

import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.core.network.model.ApiResult
import william.miranda.brutusfly.data.mapper.transaction.TransactionApiErrorMapper
import william.miranda.brutusfly.data.mapper.transaction.TransactionPageDtoMapper
import william.miranda.brutusfly.data.remote.FireflyApi
import william.miranda.brutusfly.domain.model.transaction.TransactionFailure
import william.miranda.brutusfly.domain.model.transaction.TransactionPage
import william.miranda.brutusfly.domain.model.transaction.TransactionLoadException
import william.miranda.brutusfly.domain.repository.transaction.TransactionRepository
import javax.inject.Inject

/** HTTP-backed transaction repository selected by Hilt for real server requests. */
class TransactionRepositoryImpl @Inject constructor(
    private val api: FireflyApi,
    private val mapper: TransactionPageDtoMapper,
    private val errorMapper: TransactionApiErrorMapper
) : TransactionRepository {

    /**
     * Requests one transaction page, mapping both its groups and server pagination to the domain.
     * A mapping error fails the whole operation rather than returning a partial list.
     * Translates ApiResult errors into domain failures, without exposing the transport wrapper.
     * DTO mapping failures are handled separately; cancellation and unexpected exceptions propagate.
     */
    override suspend fun getTransactions(
        page: Int,
        limit: Int
    ): TransactionPage =
        try {
            when (val result = api.getTransactions(
                page = page,
                limit = limit
            )) {
                is ApiResult.Success -> mapper.map(
                    input = result.data
                ).also { mapped ->
                    if (mapped.currentPage != page) {
                        throw MappingException(
                            message = "Server returned a different transaction page"
                        )
                    }
                }
                is ApiResult.Error -> throw errorMapper.map(
                    input = result.error
                )
            }
        } catch (exception: MappingException) {
            throw TransactionLoadException(
                failure = TransactionFailure.UNKNOWN,
                cause = exception
            )
        }
}
