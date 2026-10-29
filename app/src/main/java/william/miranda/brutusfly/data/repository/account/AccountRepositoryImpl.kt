package william.miranda.brutusfly.data.repository.account

import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.core.network.model.ApiResult
import william.miranda.brutusfly.data.mapper.account.AccountApiErrorMapper
import william.miranda.brutusfly.data.mapper.account.AccountDtoMapper
import william.miranda.brutusfly.data.mapper.account.AccountTransactionApiErrorMapper
import william.miranda.brutusfly.data.mapper.account.AccountPageDtoMapper
import william.miranda.brutusfly.data.mapper.account.AccountTypeFilterMapper
import william.miranda.brutusfly.data.mapper.transaction.TransactionPageDtoMapper
import william.miranda.brutusfly.data.remote.FireflyApi
import william.miranda.brutusfly.domain.model.account.AccountFailure
import william.miranda.brutusfly.domain.model.account.Account
import william.miranda.brutusfly.domain.model.account.AccountLoadException
import william.miranda.brutusfly.domain.model.account.AccountPage
import william.miranda.brutusfly.domain.model.account.AccountTypeFilter
import william.miranda.brutusfly.domain.model.transaction.TransactionFailure
import william.miranda.brutusfly.domain.model.transaction.TransactionLoadException
import william.miranda.brutusfly.domain.model.transaction.TransactionPage
import william.miranda.brutusfly.domain.repository.account.AccountRepository
import javax.inject.Inject

/** Loads accounts through the API using the configured server URL and personal access token. */
class AccountRepositoryImpl @Inject constructor(
    private val api: FireflyApi,
    private val mapper: AccountPageDtoMapper,
    private val errorMapper: AccountApiErrorMapper,
    private val transactionMapper: TransactionPageDtoMapper,
    private val transactionErrorMapper: AccountTransactionApiErrorMapper,
    private val accountMapper: AccountDtoMapper,
    private val typeFilterMapper: AccountTypeFilterMapper
) : AccountRepository {
    /** Maps successful pages and translates expected failures; cancellation and unexpected errors propagate. */
    override suspend fun getAccounts(
        page: Int,
        limit: Int,
        type: AccountTypeFilter?
    ): AccountPage = try {
        when (val result = api.getAccounts(
            page = page,
            limit = limit,
            type = type?.let(
                block = typeFilterMapper::map
            )
        )) {
            is ApiResult.Success -> mapper.map(
                input = result.data
            ).also { mapped ->
                if (mapped.currentPage != page) throw MappingException(
                    message = "Server returned a different account page"
                )
            }
            is ApiResult.Error -> throw errorMapper.map(
                input = result.error
            )
        }
    } catch (exception: MappingException) {
        throw AccountLoadException(
            failure = AccountFailure.UNKNOWN,
            cause = exception
        )
    }

    /** Maps a single account and rejects a mismatched resource ID instead of validating another account. */
    override suspend fun getAccount(
        accountId: String
    ): Account = try {
        when (val result = api.getAccount(
            accountId = accountId
        )) {
            is ApiResult.Success -> accountMapper.map(
                input = result.data.data
            ).also { account ->
                if (account.id != accountId) throw MappingException(
                    message = "Server returned a different account"
                )
            }
            is ApiResult.Error -> throw errorMapper.map(
                input = result.error
            )
        }
    } catch (exception: MappingException) {
        throw AccountLoadException(
            failure = AccountFailure.UNKNOWN,
            cause = exception
        )
    }

    /**
     * Requests one transaction page for a single account, reusing the shared transaction mapping.
     * A mapping error fails the whole operation rather than returning a partial list, and the
     * server must confirm the requested page. Errors translate into transaction domain failures.
     */
    override suspend fun getAccountTransactions(
        accountId: String,
        page: Int,
        limit: Int
    ): TransactionPage =
        try {
            when (val result = api.getAccountTransactions(
                accountId = accountId,
                page = page,
                limit = limit
            )) {
                is ApiResult.Success -> transactionMapper.map(
                    input = result.data
                ).also { mapped ->
                    if (mapped.currentPage != page) {
                        throw MappingException(
                            message = "Server returned a different account transaction page"
                        )
                    }
                }
                is ApiResult.Error -> throw transactionErrorMapper.map(
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
