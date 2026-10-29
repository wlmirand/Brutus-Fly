package william.miranda.brutusfly.data.remote

import kotlinx.coroutines.flow.first
import william.miranda.brutusfly.core.network.model.ApiResult
import william.miranda.brutusfly.data.local.datastore.serverconfiguration.ServerConfigurationStore
import william.miranda.brutusfly.data.remote.dto.serverconfiguration.AboutResponse
import william.miranda.brutusfly.data.remote.dto.account.AccountsResponse
import william.miranda.brutusfly.data.remote.dto.account.AccountResponse
import william.miranda.brutusfly.data.remote.dto.category.CategoriesResponse
import william.miranda.brutusfly.data.remote.dto.transaction.TransactionsResponse
import javax.inject.Inject

/** Reads the persisted configuration before creating an authenticated API client. */
class ConfiguredFireflyApi @Inject constructor(
    private val store: ServerConfigurationStore,
    private val factory: FireflyApiFactory
) : FireflyApi {
    /**
     * Reads one current configuration snapshot and creates a client using that URL and token.
     * Throws if no configuration exists; storage and decryption failures also propagate.
     * Reads again for each operation so subsequent requests use updated settings.
     */
    private suspend fun api(): FireflyApi {
        val configuration = checkNotNull(
            value = store.configuration.first()
        ) { "Server is not configured" }
        return factory.create(
            configuration = configuration
        )
    }

    /** Fetches server metadata using the currently persisted connection settings. */
    override suspend fun getAbout(): ApiResult<AboutResponse> = api().getAbout()

    /** Fetches an optionally type-filtered account page using the persisted connection settings. */
    override suspend fun getAccounts(
        page: Int,
        limit: Int,
        type: String?
    ): ApiResult<AccountsResponse> =
        api().getAccounts(
            page = page,
            limit = limit,
            type = type
        )

    /** Fetches current account attributes using the persisted connection settings. */
    override suspend fun getAccount(
        accountId: String
    ): ApiResult<AccountResponse> = api().getAccount(
        accountId = accountId
    )

    /** Fetches a category page using the currently persisted connection settings. */
    override suspend fun getCategories(
        page: Int,
        limit: Int
    ): ApiResult<CategoriesResponse> = api().getCategories(
        page = page,
        limit = limit
    )

    /** Fetches the requested transaction page using the currently persisted connection settings. */
    override suspend fun getTransactions(
        page: Int,
        limit: Int
    ): ApiResult<TransactionsResponse> =
        api().getTransactions(
            page = page,
            limit = limit
        )

    /** Fetches one account's transaction page using the currently persisted connection settings. */
    override suspend fun getAccountTransactions(
        accountId: String,
        page: Int,
        limit: Int
    ): ApiResult<TransactionsResponse> = api().getAccountTransactions(
        accountId = accountId,
        page = page,
        limit = limit
    )
}
