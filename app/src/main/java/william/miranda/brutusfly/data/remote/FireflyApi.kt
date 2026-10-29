package william.miranda.brutusfly.data.remote

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import william.miranda.brutusfly.core.network.model.ApiResult
import william.miranda.brutusfly.data.remote.dto.serverconfiguration.AboutResponse
import william.miranda.brutusfly.data.remote.dto.account.AccountsResponse
import william.miranda.brutusfly.data.remote.dto.account.AccountResponse
import william.miranda.brutusfly.data.remote.dto.category.CategoriesResponse
import william.miranda.brutusfly.data.remote.dto.transaction.TransactionsResponse

/**
 * Retrofit contract for Firefly III. The base URL must be the installation root,
 * for example https://firefly.example.com/ or https://example.com/firefly/.
 * Endpoint paths are relative so an installation subdirectory is preserved.
 */
interface FireflyApi {

    /** Retrieves server metadata, including the version checked during connection setup. */
    @Headers("Accept: application/json")
    @GET("api/v1/about")
    suspend fun getAbout(): ApiResult<AboutResponse>

    /** Retrieves one page of accounts, optionally filtered by the API's AccountTypeFilter value. */
    @Headers("Accept: application/json")
    @GET("api/v1/accounts")
    suspend fun getAccounts(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("type") type: String? = null
    ): ApiResult<AccountsResponse>

    /** Retrieves the current attributes of one account without relying on a paginated list. */
    @Headers("Accept: application/json")
    @GET("api/v1/accounts/{id}")
    suspend fun getAccount(
        @Path("id") accountId: String
    ): ApiResult<AccountResponse>

    /** Retrieves one page of categories, including the server-reported pagination boundaries. */
    @Headers("Accept: application/json")
    @GET("api/v1/categories")
    suspend fun getCategories(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiResult<CategoriesResponse>

    /**
     * Retrieves one page of transaction groups as transport models, without domain conversion.
     * The page number is one-based and limit is the requested number of groups per page.
     * Expected HTTP, network and deserialization failures are returned as ApiResult.Error.
     * Cancellation and unexpected exceptions propagate instead of being converted into values.
     */
    @Headers("Accept: application/json")
    @GET("api/v1/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiResult<TransactionsResponse>

    /**
     * Retrieves one page of transaction groups limited to a single account, identified by its ID.
     * The response shape and pagination are the same as getTransactions, so domain conversion
     * reuses the same mapper. Expected HTTP, network and deserialization failures are returned
     * as ApiResult.Error; cancellation and unexpected exceptions propagate.
     */
    @Headers("Accept: application/json")
    @GET("api/v1/accounts/{id}/transactions")
    suspend fun getAccountTransactions(
        @Path("id") accountId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiResult<TransactionsResponse>
}
