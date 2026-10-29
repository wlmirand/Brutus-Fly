package william.miranda.brutusfly.data.mapper

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.remote.dto.AccountAttributesDto
import william.miranda.brutusfly.data.remote.dto.AccountDto
import william.miranda.brutusfly.data.remote.dto.AccountMetaDto
import william.miranda.brutusfly.data.remote.dto.AccountPaginationDto
import william.miranda.brutusfly.data.remote.dto.AccountsResponse

class AccountDtoMapperTest {
    private val mapper = AccountDtoMapper()

    @Test
    fun preservesExactBalancesAndAccountIdentity() {
        val result = mapper.map(account("-1234.567890"))

        assertEquals("account", result.id)
        assertEquals("Checking account", result.name)
        assertEquals("asset", result.type)
        assertEquals("-1234.567890".toBigDecimal(), result.currentBalance)
        assertEquals("USD", result.currencyCode)
    }

    @Test
    fun missingBalanceIsNotInventedAsZero() {
        assertNull(mapper.map(account(null)).currentBalance)
    }

    @Test
    fun rejectsInvalidFinancialValuesAndBlankIdentities() {
        assertThrows(MappingException::class.java) { mapper.map(account("invalid")) }
        assertThrows(MappingException::class.java) { mapper.map(account("10").copy(id = "")) }
    }

    @Test
    fun responseDecodesApiFieldNamesAndServerPagination() {
        val response = Json { ignoreUnknownKeys = true }.decodeFromString<AccountsResponse>(
            """{"data":[{"id":"account","attributes":{"name":"Checking account","type":"asset","active":true,"current_balance":"10.00","currency_code":"USD","iban":null}}],"meta":{"pagination":{"current_page":1,"total_pages":2}}}""",
        )

        val result = AccountPageDtoMapper(mapper).map(response)

        assertEquals("10.00".toBigDecimal(), result.accounts.single().currentBalance)
        assertEquals(2, result.nextPage)
    }

    @Test
    fun invalidPageMetadataFailsInsteadOfLooping() {
        val response = AccountsResponse(emptyList(), AccountMetaDto(AccountPaginationDto(3, 2)))
        assertThrows(MappingException::class.java) { AccountPageDtoMapper(mapper).map(response) }
    }

    private fun account(balance: String?) = AccountDto(
        id = "account",
        attributes = AccountAttributesDto("Checking account", "asset", true, balance, "USD"),
    )
}
