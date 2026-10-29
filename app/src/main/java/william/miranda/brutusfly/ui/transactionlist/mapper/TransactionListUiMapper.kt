package william.miranda.brutusfly.ui.transactionlist.mapper

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.domain.model.category.CategoryIcon
import william.miranda.brutusfly.domain.model.transaction.TransactionGroup
import william.miranda.brutusfly.domain.model.transaction.TransactionType
import william.miranda.brutusfly.ui.transactionlist.model.TransactionKind
import william.miranda.brutusfly.ui.transactionlist.model.TransactionPeriod
import william.miranda.brutusfly.ui.transactionlist.model.TransactionUiGroup
import william.miranda.brutusfly.ui.transactionlist.model.TransactionUiModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

internal class TransactionListUiMapper @Inject constructor() :
    Mapper<TransactionListUiMapper.Input, List<TransactionUiGroup>> {

    data class Input(
        val groups: List<TransactionGroup>,
        val zoneId: ZoneId = ZoneId.systemDefault(),
        val today: LocalDate = LocalDate.now(zoneId),
        val categoryIcons: Map<String, CategoryIcon> = emptyMap()
    )

    override fun map(input: Input): List<TransactionUiGroup> {
        val today = input.today
        val transactions = input.groups.flatMap { group ->
            group.splits.mapIndexed { index, split ->
                // Convert the instant, not just the clock time. The local date can change too.
                val dateTime = split.date.atZoneSameInstant(input.zoneId)
                val income = when (split.type) {
                    TransactionType.DEPOSIT -> true
                    TransactionType.WITHDRAWAL -> false
                    TransactionType.TRANSFER,
                    TransactionType.RECONCILIATION,
                    TransactionType.OPENING_BALANCE -> throw MappingException(
                        message = "Unsupported transaction type: ${split.type}"
                    )
                }
                dateTime.toLocalDate() to TransactionUiModel(
                    id = "${group.id}-$index",
                    title = split.description,
                    category = split.categoryName.orEmpty(),
                    categoryId = split.categoryId,
                    account = (if (income) split.destinationName else split.sourceName).orEmpty(),
                    moment = dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    amount = if (income) split.amount.abs() else split.amount.abs().negate(),
                    currencyCode = split.currencyCode,
                    kind = if (income) TransactionKind.INCOME else TransactionKind.EXPENSE,
                    icon = split.categoryId?.let { input.categoryIcons[it] } ?: CategoryIcon.LABEL,
                    tags = split.tags
                )
            }
        }

        return transactions.groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )
            .toSortedMap(
                comparator = compareByDescending { it }
            )
            .map { (date, items) ->
                TransactionUiGroup(
                    label = date.format(
                        DateTimeFormatter.ofLocalizedDate(
                            FormatStyle.MEDIUM
                        )
                    ),
                    period = when {
                        date.year == today.year && date.month == today.month -> TransactionPeriod.THIS_MONTH
                        date.year == today.minusMonths(1).year && date.month == today.minusMonths(1).month -> TransactionPeriod.LAST_MONTH
                        else -> TransactionPeriod.THIS_YEAR
                    },
                    transactions = items,
                    date = date
                )
            }
    }
}
