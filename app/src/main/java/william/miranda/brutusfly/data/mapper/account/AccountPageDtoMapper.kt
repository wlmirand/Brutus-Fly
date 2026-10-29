package william.miranda.brutusfly.data.mapper.account

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.remote.dto.account.AccountsResponse
import william.miranda.brutusfly.domain.model.account.AccountPage
import javax.inject.Inject

/** Converts the complete transport page, including its authoritative pagination boundaries. */
class AccountPageDtoMapper @Inject constructor(
    private val accountMapper: AccountDtoMapper
) : Mapper<AccountsResponse, AccountPage> {
    /** Rejects invalid metadata and propagates account-mapping failures instead of silently skipping rows. */
    override fun map(
        input: AccountsResponse
    ): AccountPage {
        val pagination = input.meta.pagination
        if (pagination.currentPage < 1 || pagination.totalPages < 0 ||
            (pagination.totalPages > 0 && pagination.currentPage > pagination.totalPages) ||
            (pagination.totalPages == 0 && input.data.isNotEmpty())
        ) {
            throw MappingException(
                message = "Invalid account pagination metadata"
            )
        }
        return AccountPage(
            accounts = input.data.map(
                transform = accountMapper::map
            ),
            currentPage = pagination.currentPage,
            totalPages = pagination.totalPages
        )
    }
}
