package william.miranda.brutusfly.domain.model.transaction

import java.math.BigDecimal
import java.time.OffsetDateTime

data class TransactionGroup(
    val id: String,
    val groupTitle: String?,
    val splits: List<TransactionSplit>
)

/** Transaction types accepted by the API; wire values are interpreted by the data mapper. */
enum class TransactionType {
    WITHDRAWAL,
    DEPOSIT,
    TRANSFER,
    RECONCILIATION,
    OPENING_BALANCE,
}

data class TransactionSplit(
    val type: TransactionType,
    // Preserve the API offset; conversion to the device time zone belongs to presentation.
    val date: OffsetDateTime,
    val amount: BigDecimal,
    val description: String,
    val currencyCode: String?,
    val sourceName: String?,
    val destinationName: String?,
    val categoryName: String? = null,
    val categoryId: String? = null,
    val tags: List<String> = emptyList()
)
