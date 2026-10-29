package william.miranda.brutusfly.data.mapper.account

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.remote.dto.account.AccountDto
import william.miranda.brutusfly.domain.model.account.Account
import william.miranda.brutusfly.domain.model.account.AccountType
import javax.inject.Inject

/** Converts an account resource into a domain model without inventing missing financial values. */
class AccountDtoMapper @Inject constructor() : Mapper<AccountDto, Account> {
    /** Validates identity, type and balance; unsupported types or invalid values fail the page. */
    override fun map(
        input: AccountDto
    ): Account {
        if (input.id.isBlank() || input.attributes.name.isBlank()) {
            throw MappingException(
                message = "Account ID and name must not be blank"
            )
        }
        val attributes = input.attributes
        val balance = attributes.currentBalance?.let {
            it.toBigDecimalOrNull() ?: throw MappingException(
                message = "Invalid account balance"
            )
        }
        return Account(
            id = input.id,
            name = attributes.name,
            type = when (attributes.type) {
                "asset" -> AccountType.ASSET
                "expense" -> AccountType.EXPENSE
                "import" -> AccountType.IMPORT
                "revenue" -> AccountType.REVENUE
                "cash" -> AccountType.CASH
                "liability", "liabilities" -> AccountType.LIABILITY
                "initial-balance" -> AccountType.INITIAL_BALANCE
                "reconciliation" -> AccountType.RECONCILIATION
                else -> throw MappingException(
                    message = "Unsupported account type: ${attributes.type}"
                )
            },
            isActive = attributes.active,
            currentBalance = balance,
            currencyCode = attributes.currencyCode
        )
    }
}
