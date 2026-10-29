package william.miranda.brutusfly.data.mapper

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.remote.dto.TransactionMetaDto
import william.miranda.brutusfly.data.remote.dto.TransactionPaginationDto
import william.miranda.brutusfly.data.remote.dto.TransactionsResponse

class TransactionPageDtoMapperTest {
    private val mapper = TransactionPageDtoMapper(TransactionGroupDtoMapper(TransactionSplitDtoMapper()))

    @Test
    fun readsServerPaginationWithoutInferringTheLastPageFromItemCount() {
        val response = Json { ignoreUnknownKeys = true }.decodeFromString<TransactionsResponse>(
            """{"data":[],"meta":{"pagination":{"current_page":2,"total_pages":3,"count":0,"total":100}}}""",
        )

        val page = mapper.map(response)

        assertEquals(2, page.currentPage)
        assertEquals(3, page.nextPage)
    }

    @Test
    fun lastPageAndEmptyCollectionHaveNoNextPage() {
        assertNull(mapper.map(response(3, 3)).nextPage)
        assertNull(mapper.map(response(1, 0)).nextPage)
    }

    @Test
    fun missingMetadataFailsInsteadOfGuessingThatTheResponseIsComplete() {
        assertThrows(SerializationException::class.java) {
            Json.decodeFromString<TransactionsResponse>("""{"data":[]}""")
        }
    }

    @Test
    fun invalidPaginationBoundariesAreRejected() {
        listOf(response(0, 1), response(1, -1), response(3, 2)).forEach { response ->
            assertThrows(MappingException::class.java) { mapper.map(response) }
        }
    }

    private fun response(current: Int, total: Int) = TransactionsResponse(
        meta = TransactionMetaDto(TransactionPaginationDto(current, total)),
    )
}
