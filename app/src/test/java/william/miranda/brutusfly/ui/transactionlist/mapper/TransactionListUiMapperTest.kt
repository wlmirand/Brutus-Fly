package william.miranda.brutusfly.ui.transactionlist.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.domain.model.TransactionGroup
import william.miranda.brutusfly.domain.model.TransactionSplit
import william.miranda.brutusfly.ui.transactionlist.model.TransactionKind
import william.miranda.brutusfly.ui.transactionlist.model.TransactionPeriod
import william.miranda.brutusfly.ui.transactionlist.model.TransactionUiGroup
import william.miranda.brutusfly.ui.transactionlist.model.matchesPeriod
import java.time.LocalDate

class TransactionListUiMapperTest {
    @Test
    fun mapsSplitsIntoDailyGroupsWithSignedAmountsAndCalculatedTotals() {
        val today = LocalDate.of(2026, 10, 25)
        val group = TransactionGroup(
            id = "group",
            groupTitle = null,
            splits = listOf(
                split("withdrawal", "2026-10-25T10:00:00Z", "58.90"),
                split("deposit", "2026-10-25T11:00:00Z", "100.00"),
                split("withdrawal", "2026-09-20T10:00:00Z", "12.00"),
            ),
        )

        val mapper: Mapper<TransactionListUiMapper.Input, List<TransactionUiGroup>> = TransactionListUiMapper()
        val result = mapper.map(TransactionListUiMapper.Input(groups = listOf(group), today = today))

        assertEquals(listOf(today, LocalDate.of(2026, 9, 20)), result.map { it.date })
        val current = result.first()
        assertEquals(4110L, current.totalCents)
        assertEquals(listOf(-5890L, 10000L), current.transactions.map { it.amountCents })
        assertEquals(listOf("group-0", "group-1"), current.transactions.map { it.id })
        assertEquals(TransactionKind.INCOME, current.transactions.last().kind)
        assertEquals("destination", current.transactions.last().account)
        assertEquals("source", current.transactions.first().account)
        assertTrue(result.all { it.matchesPeriod(TransactionPeriod.THIS_YEAR, today) })
        assertTrue(result.last().matchesPeriod(TransactionPeriod.LAST_MONTH, today))
    }

    private fun split(type: String, date: String, amount: String) = TransactionSplit(
        type = type,
        date = date,
        amount = amount.toBigDecimal(),
        description = "description",
        currencyCode = "BRL",
        sourceName = "source",
        destinationName = "destination",
        categoryName = "category",
    )
}
