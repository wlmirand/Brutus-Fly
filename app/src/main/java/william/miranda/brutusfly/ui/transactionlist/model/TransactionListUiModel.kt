package william.miranda.brutusfly.ui.transactionlist.model

import william.miranda.brutusfly.domain.model.category.CategoryIcon
import java.math.BigDecimal
import java.text.Normalizer
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

/** Display direction of a transaction split; transfers are not currently mapped by the UI. */
internal enum class TransactionKind {
    INCOME,
    EXPENSE,
}

/** Local direction filter used by the chips below the search field. */
internal enum class TransactionFilter(val label: String) {
    ALL(label = "All"),
    INCOME(label = "Income"),
    EXPENSE(label = "Expenses"),
}

/** Calendar-based period options; THIS_YEAR means the current calendar year, not the last 12 months. */
internal enum class TransactionPeriod(val label: String) {
    THIS_MONTH(label = "This month"),
    LAST_MONTH(label = "Last month"),
    THIS_YEAR(label = "This year"),
}

/**
 * Display-ready representation of one split, not an entire Firefly transaction group.
 * The mapper combines the domain group ID and split index to identify each displayed item.
 */
internal data class TransactionUiModel(
    val id: String,
    val title: String,
    // Display/search name; filtering uses categoryId so categories are not matched by name.
    val category: String,
    // Destination account for income, source account for expenses.
    val account: String,
    // Time in the device time zone; calendar filtering uses the section's date in that same zone.
    val moment: String,
    // Signed decimal amount in the split's own currency, without assuming two decimal places.
    val amount: BigDecimal,
    // API currency code; null and custom codes must not fall back to the device currency.
    val currencyCode: String?,
    val kind: TransactionKind,
    // User choice resolved by category ID, independent of the split direction or category name.
    val icon: CategoryIcon,
    // Null when the API does not provide a category ID; matches only the all-categories option.
    val categoryId: String? = null,
    val tags: List<String> = emptyList()
)

/**
 * Daily UI section combining splits across domain transaction groups.
 * Unlike a Firefly group, this groups items by date, not by their API group ID.
 */
internal data class TransactionUiGroup(
    // Localized display label; never parsed back into a date.
    val label: String,
    // Fallback for fixtures without a date; production period checks use date directly.
    val period: TransactionPeriod,
    val transactions: List<TransactionUiModel>,
    // Date after converting the split's instant to the device time zone.
    val date: LocalDate? = null
) {
    /** Derived from the current items, so filtering cannot leave stale or mixed-currency totals. */
    val totalsByCurrency: Map<String?, BigDecimal>
        get() = transactions.groupBy { it.currencyCode }
            .mapValues { (_, items) -> items.sumOf { it.amount } }
}

/**
 * Tests a real section date against the selected calendar period.
 * [today] can be supplied for deterministic checks. Sections without a date use their period tag.
 */
internal fun TransactionUiGroup.matchesPeriod(
    selectedPeriod: TransactionPeriod,
    today: LocalDate = LocalDate.now()
): Boolean {
    val transactionDate = date ?: return period == selectedPeriod
    return when (selectedPeriod) {
        TransactionPeriod.THIS_MONTH -> YearMonth.from(transactionDate) == YearMonth.from(today)
        TransactionPeriod.LAST_MONTH -> YearMonth.from(transactionDate) == YearMonth.from(today.minusMonths(1))
        TransactionPeriod.THIS_YEAR -> transactionDate.year == today.year
    }
}

/** Tests direction only; category and period filtering are applied separately by the state. */
internal fun TransactionUiModel.matchesFilter(
    filter: TransactionFilter
): Boolean =
    when (filter) {
        TransactionFilter.ALL -> true
        TransactionFilter.INCOME -> kind == TransactionKind.INCOME
        TransactionFilter.EXPENSE -> kind == TransactionKind.EXPENSE
    }

/** Matches title, category name, or account without case/diacritics; a blank query matches all. */
internal fun TransactionUiModel.matchesQuery(
    query: String
): Boolean {
    if (query.isBlank()) return true

    val normalizedQuery = query.trim().normalizeForSearch()
    return listOf(
        title,
        category,
        account
    ).any { value ->
        value
            .normalizeForSearch()
            .contains(other = normalizedQuery)
    }
}

/** Decomposes accented characters, removes combining marks, and applies locale-neutral lowercase. */
private fun String.normalizeForSearch(): String =
    Normalizer.normalize(
        this,
        Normalizer.Form.NFD
    )
        .replace(diacriticsRegex, "")
        .lowercase(locale = Locale.ROOT)

private val diacriticsRegex = Regex(
    pattern = "\\p{M}+"
)

// Preview/test fixtures only. Runtime data comes from the repository through the use case and mapper.
internal val sampleTransactionGroups: List<TransactionUiGroup> = listOf(
    TransactionUiGroup(
        label = "Today",
        period = TransactionPeriod.THIS_MONTH,
        transactions = listOf(
            TransactionUiModel(
                id = "sample-restaurant",
                title = "Sample restaurant",
                category = "Food",
                account = "Nubank",
                moment = "14:32",
                amount = BigDecimal("-58.90"),
                currencyCode = "EUR",
                kind = TransactionKind.EXPENSE,
                icon = CategoryIcon.RESTAURANT,
            ),
            TransactionUiModel(
                id = "grocery-store",
                title = "Grocery store",
                category = "Groceries",
                account = "Checking account",
                moment = "11:20",
                amount = BigDecimal("-124.60"),
                currencyCode = "EUR",
                kind = TransactionKind.EXPENSE,
                icon = CategoryIcon.CART,
            ),
            TransactionUiModel(
                id = "salary",
                title = "Salary",
                category = "Work",
                account = "Payroll account",
                moment = "09:15",
                amount = BigDecimal("3250.00"),
                currencyCode = "EUR",
                kind = TransactionKind.INCOME,
                icon = CategoryIcon.WORK,
            ),
        ),
    ),
    TransactionUiGroup(
        label = "Yesterday",
        period = TransactionPeriod.THIS_MONTH,
        transactions = listOf(
            TransactionUiModel(
                id = "uber",
                title = "Uber",
                category = "Transport",
                account = "Nubank",
                moment = "18:45",
                amount = BigDecimal("-32.90"),
                currencyCode = "EUR",
                kind = TransactionKind.EXPENSE,
                icon = CategoryIcon.CAR
            ),
            TransactionUiModel(
                id = "electricity-bill",
                title = "Electricity bill",
                category = "Bills",
                account = "Checking account",
                moment = "10:12",
                amount = BigDecimal("-56.80"),
                currencyCode = "EUR",
                kind = TransactionKind.EXPENSE,
                icon = CategoryIcon.ELECTRICITY
            )
        )
    ),
    TransactionUiGroup(
        label = "Mar 12, 2024",
        period = TransactionPeriod.THIS_MONTH,
        transactions = listOf(
            TransactionUiModel(
                id = "playstation-store",
                title = "PlayStation Store",
                category = "Leisure",
                account = "Nubank",
                moment = "15:30",
                amount = BigDecimal("-210.00"),
                currencyCode = "EUR",
                kind = TransactionKind.EXPENSE,
                icon = CategoryIcon.GAME
            )
        )
    ),
    TransactionUiGroup(
        label = "Mar 5, 2024",
        period = TransactionPeriod.THIS_MONTH,
        transactions = listOf(
            TransactionUiModel(
                id = "online-marketplace",
                title = "Online marketplace",
                category = "Online shopping",
                account = "Nubank",
                moment = "Mar 5, 2024",
                amount = BigDecimal("-79.90"),
                currencyCode = "EUR",
                kind = TransactionKind.EXPENSE,
                icon = CategoryIcon.LABEL
            )
        )
    )
)
