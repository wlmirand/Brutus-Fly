package william.miranda.brutusfly.ui.transactionlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.ui.transactionlist.model.TransactionUiGroup
import william.miranda.brutusfly.ui.transactionlist.model.formatGroupTotal
import william.miranda.brutusfly.ui.transactionlist.model.sampleTransactionGroups
import java.math.BigDecimal

@Composable
internal fun TransactionDateSection(
    group: TransactionUiGroup,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TransactionSectionHeader(
            label = group.label,
            totalsByCurrency = group.totalsByCurrency
        )
        TransactionCard(
            transactions = group.transactions
        )
    }
}

@Composable
internal fun TransactionSectionHeader(
    label: String,
    totalsByCurrency: Map<String?, BigDecimal>,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = BrutusTheme.spacing.extraLarge,
                bottom = BrutusTheme.spacing.medium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(weight = 1f),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.xs)
        ) {
            // Keep each currency separate: a daily section can contain splits in different currencies.
            totalsByCurrency.forEach { (currencyCode, amount) ->
                Text(
                    text = formatGroupTotal(
                        amount = amount,
                        currencyCode = currencyCode,
                        locale = locale
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
internal fun TransactionSearchSummary(
    count: Int,
    query: String,
    modifier: Modifier = Modifier
) {
    val resultLabel = if (count == 1) "result" else "results"

    Text(
        text = "$count $resultLabel for “${query.trim()}”",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = BrutusTheme.spacing.large,
                bottom = BrutusTheme.spacing.medium
            )
    )
}

@Composable
internal fun TransactionEmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = BrutusTheme.spacing.huge
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun TransactionDateSectionPreview() {
    BrutusFlyTheme {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background
                )
                .padding(
                    horizontal = BrutusTheme.spacing.screenHorizontal
                )
        ) {
            TransactionDateSection(
                group = sampleTransactionGroups.first()
            )
            TransactionSearchSummary(
                count = 2,
                query = "grocery"
            )
            TransactionEmptyState(
                message = "No transactions found."
            )
        }
    }
}

@Preview(name = "Currencies — English", showBackground = true, widthDp = 393, locale = "en")
@Preview(name = "Currencies — Portuguese", showBackground = true, widthDp = 393, locale = "pt")
@Composable
private fun TransactionCurrenciesPreview() {
    val sample = sampleTransactionGroups.first().transactions.first()
    BrutusFlyTheme(darkTheme = false) {
        TransactionDateSection(
            group = sampleTransactionGroups.first().copy(
                transactions = listOf(
                    sample.copy(id = "eur", amount = BigDecimal("-58.90"), currencyCode = "EUR"),
                    sample.copy(id = "jpy", amount = BigDecimal("-1500"), currencyCode = "JPY"),
                    sample.copy(id = "kwd", amount = BigDecimal("-12.345"), currencyCode = "KWD"),
                    sample.copy(id = "custom", amount = BigDecimal("-1.2345"), currencyCode = "POINTS"),
                    sample.copy(id = "missing", amount = BigDecimal("-10.50"), currencyCode = null)
                )
            ),
            modifier = Modifier.padding(horizontal = BrutusTheme.spacing.screenHorizontal)
        )
    }
}
