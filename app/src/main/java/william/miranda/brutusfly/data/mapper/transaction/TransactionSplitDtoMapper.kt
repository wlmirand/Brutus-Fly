package william.miranda.brutusfly.data.mapper.transaction

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.remote.dto.transaction.TransactionSplitDto
import william.miranda.brutusfly.domain.model.transaction.TransactionSplit
import william.miranda.brutusfly.domain.model.transaction.TransactionType
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import javax.inject.Inject

/** Converts the API representation of one transaction split into its domain representation. */
class TransactionSplitDtoMapper @Inject constructor() : Mapper<TransactionSplitDto, TransactionSplit> {
    /**
     * Parses the amount and offset date, and converts the wire type into a domain enum.
     * Throws MappingException for invalid amounts/dates or unknown types instead of using a fallback.
     */
    override fun map(
        input: TransactionSplitDto
    ): TransactionSplit {
        val amount = input.amount.toBigDecimalOrNull()
            ?: throw MappingException(
                message = "Invalid transaction amount"
            )

        val date = try {
            OffsetDateTime.parse(input.date)
        } catch (exception: DateTimeParseException) {
            throw MappingException(
                message = "Invalid transaction date",
                cause = exception
            )
        }

        return TransactionSplit(
            type = when (input.type) {
                "withdrawal" -> TransactionType.WITHDRAWAL
                "deposit" -> TransactionType.DEPOSIT
                "transfer" -> TransactionType.TRANSFER
                "reconciliation" -> TransactionType.RECONCILIATION
                "opening balance" -> TransactionType.OPENING_BALANCE
                else -> throw MappingException(
                    message = "Unknown transaction type: ${input.type}"
                )
            },
            date = date,
            amount = amount,
            description = input.description,
            currencyCode = input.currencyCode,
            sourceName = input.sourceName,
            destinationName = input.destinationName,
            categoryName = input.categoryName,
            categoryId = input.categoryId,
            tags = input.tags
        )
    }
}
