package william.miranda.brutusfly.data.mapper.transaction

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.remote.dto.transaction.TransactionsResponse
import william.miranda.brutusfly.domain.model.transaction.TransactionPage
import javax.inject.Inject

/** Maps page content and pagination without leaking transport metadata into the domain. */
class TransactionPageDtoMapper @Inject constructor(
    private val groupMapper: TransactionGroupDtoMapper
) : Mapper<TransactionsResponse, TransactionPage> {
    /** Rejects invalid page boundaries instead of allowing repeated or unbounded page requests. */
    override fun map(
        input: TransactionsResponse
    ): TransactionPage {
        val pagination = input.meta.pagination
        if (pagination.currentPage < 1 || pagination.totalPages < 0 ||
            (pagination.totalPages > 0 && pagination.currentPage > pagination.totalPages) ||
            (pagination.totalPages == 0 && input.data.isNotEmpty())
        ) {
            throw MappingException(
                message = "Invalid transaction pagination metadata"
            )
        }
        return TransactionPage(
            groups = input.data.map(
                transform = groupMapper::map
            ),
            currentPage = pagination.currentPage,
            totalPages = pagination.totalPages
        )
    }
}
