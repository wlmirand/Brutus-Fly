package william.miranda.brutusfly.domain.model.account

import java.math.BigDecimal

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val isActive: Boolean,
    val currentBalance: BigDecimal?,
    val currencyCode: String?
) {
    /** Whether this account can be selected as the source of the transaction list. */
    val isSelectable: Boolean
        get() = isActive && (type == AccountType.ASSET || type == AccountType.CASH)
}

data class AccountPage(
    val accounts: List<Account>,
    val currentPage: Int,
    val totalPages: Int
) {
    val nextPage: Int?
        get() = if (currentPage < totalPages) currentPage + 1 else null
}
