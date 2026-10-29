package william.miranda.brutusfly.ui.transactionlist.model

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/** Formats a split with an explicit sign using its currency and the UI's locale. */
internal fun formatTransactionAmount(
    amount: BigDecimal,
    currencyCode: String?,
    locale: Locale = Locale.getDefault()
): String {
    val sign = if (amount.signum() < 0) "−" else "+"
    return "$sign${formatGroupTotal(
        amount = amount.abs(),
        currencyCode = currencyCode,
        locale = locale
    )}"
}

/** Formats one currency total without conversion or floating-point arithmetic. */
internal fun formatGroupTotal(
    amount: BigDecimal,
    currencyCode: String?,
    locale: Locale = Locale.getDefault()
): String {
    val currency = try {
        currencyCode?.let { Currency.getInstance(it) }
    } catch (_: IllegalArgumentException) {
        null
    }
    if (currency != null && currency.defaultFractionDigits >= 0) {
        return NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = currency
            minimumFractionDigits = currency.defaultFractionDigits
            maximumFractionDigits = currency.defaultFractionDigits
        }.format(amount)
    }

    // Preserve custom codes and precision; a missing code must not silently become EUR, BRL, etc.
    val number = NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = maxOf(a = 2, b = amount.stripTrailingZeros().scale())
    }.format(amount)
    return if (currencyCode.isNullOrBlank()) number else "$number $currencyCode"
}
