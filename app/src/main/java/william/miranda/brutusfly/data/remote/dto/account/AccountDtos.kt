package william.miranda.brutusfly.data.remote.dto.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Account resources and server-reported pagination for GET api/v1/accounts. */
@Serializable
data class AccountsResponse(
    val data: List<AccountDto>,
    val meta: AccountMetaDto
)

/** Single-account response envelope used to confirm the selected account's current eligibility. */
@Serializable
data class AccountResponse(
    val data: AccountDto
)

/** Transport envelope identifying one account and its attributes. */
@Serializable
data class AccountDto(
    val id: String,
    val attributes: AccountAttributesDto
)

/** Financial values stay as decimal strings until the domain mapper validates them. */
@Serializable
data class AccountAttributesDto(
    val name: String,
    val type: String,
    val active: Boolean,
    @SerialName("current_balance") val currentBalance: String? = null,
    @SerialName("currency_code") val currencyCode: String? = null
)

/** Pagination metadata associated with an account response. */
@Serializable
data class AccountMetaDto(
    val pagination: AccountPaginationDto
)

/** Page boundaries used to stop loading without guessing from the number of returned items. */
@Serializable
data class AccountPaginationDto(
    @SerialName("current_page") val currentPage: Int,
    @SerialName("total_pages") val totalPages: Int
)
