package william.miranda.brutusfly.data.remote.dto.transaction

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Transaction response envelope, including the server's authoritative pagination boundaries. */
@Serializable
data class TransactionsResponse(
    val data: List<TransactionGroupDto> = emptyList(),
    val meta: TransactionMetaDto
)

/** Metadata associated with the returned transaction page. */
@Serializable
data class TransactionMetaDto(
    val pagination: TransactionPaginationDto
)

/** Only the pagination fields needed to request the next page are decoded. */
@Serializable
data class TransactionPaginationDto(
    @SerialName("current_page") val currentPage: Int,
    @SerialName("total_pages") val totalPages: Int
)

/** API resource containing a transaction group identifier and its attributes. */
@Serializable
data class TransactionGroupDto(
    val id: String,
    val attributes: TransactionGroupAttributesDto
)

/** Group-level attributes, including an optional title and the underlying transaction splits. */
@Serializable
data class TransactionGroupAttributesDto(
    @SerialName("group_title")
    val groupTitle: String? = null,
    val transactions: List<TransactionSplitDto> = emptyList()
)

/**
 * Transport representation of a split. Monetary amounts and dates remain strings here.
 * SerialName maps API snake_case names to Kotlin properties; optional metadata may be absent or null.
 */
@Serializable
data class TransactionSplitDto(
    val type: String,
    val date: String,
    val amount: String,
    val description: String,
    @SerialName("currency_code")
    val currencyCode: String? = null,
    @SerialName("source_name")
    val sourceName: String? = null,
    @SerialName("destination_name")
    val destinationName: String? = null,
    @SerialName("category_name")
    val categoryName: String? = null,
    @SerialName("category_id")
    val categoryId: String? = null,
    val tags: List<String> = emptyList()
)
