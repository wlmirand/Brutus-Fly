package william.miranda.brutusfly.ui.accountlist.mapper

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.domain.model.account.Account
import william.miranda.brutusfly.domain.model.account.AccountType
import william.miranda.brutusfly.ui.accountlist.model.AccountKind
import william.miranda.brutusfly.ui.accountlist.model.AccountUiModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

internal class AccountListUiMapper @Inject constructor() :
    Mapper<AccountListUiMapper.Input, List<AccountUiModel>> {

    data class Input(
        val accounts: List<Account>,
        val locale: Locale = Locale.getDefault()
    )

    override fun map(
        input: Input
    ): List<AccountUiModel> = input.accounts.map { account ->
        AccountUiModel(
            id = account.id,
            name = account.name,
            kind = when (account.type) {
                AccountType.ASSET -> AccountKind.ASSET
                AccountType.CASH -> AccountKind.CASH
                AccountType.EXPENSE -> AccountKind.EXPENSE
                AccountType.REVENUE -> AccountKind.REVENUE
                AccountType.LIABILITY -> AccountKind.LIABILITY
                AccountType.INITIAL_BALANCE -> AccountKind.INITIAL_BALANCE
                AccountType.RECONCILIATION -> AccountKind.RECONCILIATION
                AccountType.IMPORT -> AccountKind.OTHER
            },
            balance = account.currentBalance?.let {
                formatBalance(
                    balance = it,
                    code = account.currencyCode,
                    locale = input.locale
                )
            },
            isActive = account.isActive,
            hasNegativeBalance = account.currentBalance?.signum() == -1,
            isSelectable = account.isSelectable
        )
    }

    private fun formatBalance(
        balance: BigDecimal,
        code: String?,
        locale: Locale
    ): String {
        val currency = try {
            code?.let {
                Currency.getInstance(
                    it
                )
            }
        } catch (_: IllegalArgumentException) {
            null
        }
        if (currency != null && currency.defaultFractionDigits >= 0) {
            return NumberFormat.getCurrencyInstance(
                locale
            ).apply {
                this.currency = currency
                minimumFractionDigits = currency.defaultFractionDigits
                maximumFractionDigits = currency.defaultFractionDigits
            }.format(
                balance
            )
        }
        // Custom or missing currency codes must not be silently replaced with the device currency.
        val number = NumberFormat.getNumberInstance(
            locale
        ).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = maxOf(
                a = 2,
                b = balance.stripTrailingZeros().scale()
            )
        }.format(
            balance
        )
        return if (code.isNullOrBlank()) number else "$number $code"
    }
}
