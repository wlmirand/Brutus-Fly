package william.miranda.brutusfly.ui.accountlist.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import william.miranda.brutusfly.domain.model.Account
import william.miranda.brutusfly.ui.accountlist.model.AccountKind
import java.util.Locale

class AccountListUiMapperTest {
    private val mapper = AccountListUiMapper()

    @Test
    fun usesTheAccountCurrencyRatherThanTheDeviceCurrency() {
        val result = map(account("-1234.50", "USD"))

        assertEquals("-\$1,234.50", result.balance)
        assertTrue(result.hasNegativeBalance)
        assertEquals(AccountKind.ASSET, result.kind)
    }

    @Test
    fun keepsMissingBalancesMissingAndUnknownTypesVisible() {
        val result = map(account(null, null).copy(type = "future-account-type"))

        assertNull(result.balance)
        assertEquals(AccountKind.OTHER, result.kind)
    }

    @Test
    fun customCurrencyCodesRemainExplicitWithoutLosingDecimalPlaces() {
        val result = map(account("1.00000001", "BTC"))
        assertEquals("1.00000001 BTC", result.balance)
    }

    private fun map(account: Account) = mapper.map(AccountListUiMapper.Input(listOf(account), Locale.US)).single()

    private fun account(balance: String?, code: String?) = Account(
        id = "account", name = "Checking", type = "asset", isActive = true,
        currentBalance = balance?.toBigDecimal(), currencyCode = code,
    )
}
