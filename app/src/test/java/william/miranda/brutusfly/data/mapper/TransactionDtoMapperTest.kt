package william.miranda.brutusfly.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.remote.dto.TransactionGroupAttributesDto
import william.miranda.brutusfly.data.remote.dto.TransactionGroupDto
import william.miranda.brutusfly.data.remote.dto.TransactionSplitDto
import william.miranda.brutusfly.domain.model.TransactionGroup
import william.miranda.brutusfly.domain.model.TransactionSplit

class TransactionDtoMapperTest {
    @Test
    fun mapsGroupAndSplitsThroughMapperContract() {
        val mapper: Mapper<TransactionGroupDto, TransactionGroup> =
            TransactionGroupDtoMapper(TransactionSplitDtoMapper())
        val split = split("58.90")
        val input = TransactionGroupDto(
            id = "group",
            attributes = TransactionGroupAttributesDto(groupTitle = "title", transactions = listOf(split)),
        )

        val result = mapper.map(input)

        assertEquals("group", result.id)
        assertEquals("title", result.groupTitle)
        assertEquals(
            TransactionSplit(
                type = split.type,
                date = split.date,
                amount = "58.90".toBigDecimal(),
                description = split.description,
                currencyCode = split.currencyCode,
                sourceName = split.sourceName,
                destinationName = split.destinationName,
                categoryName = split.categoryName,
            ),
            result.splits.single(),
        )
    }

    @Test
    fun invalidAmountFailsInsteadOfBecomingZero() {
        val mapper: Mapper<TransactionSplitDto, TransactionSplit> = TransactionSplitDtoMapper()

        assertThrows(MappingException::class.java) {
            mapper.map(split("invalid"))
        }
    }

    @Test
    fun invalidSplitFailsTheEntireGroup() {
        val mapper = TransactionGroupDtoMapper(TransactionSplitDtoMapper())
        val input = TransactionGroupDto(
            id = "group",
            attributes = TransactionGroupAttributesDto(transactions = listOf(split("58.90"), split("invalid"))),
        )

        assertThrows(MappingException::class.java) { mapper.map(input) }
    }

    private fun split(amount: String) = TransactionSplitDto(
        type = "withdrawal",
        date = "2026-10-25T10:00:00Z",
        amount = amount,
        description = "description",
        currencyCode = "BRL",
        sourceName = "source",
        destinationName = "destination",
        categoryName = "category",
    )
}
