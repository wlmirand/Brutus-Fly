package william.miranda.brutusfly.ui.transactionlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.ui.common.category.colors
import william.miranda.brutusfly.ui.common.category.imageVector
import william.miranda.brutusfly.ui.transactionlist.model.TransactionKind
import william.miranda.brutusfly.ui.transactionlist.model.TransactionUiModel
import william.miranda.brutusfly.ui.transactionlist.model.formatTransactionAmount
import william.miranda.brutusfly.ui.transactionlist.model.sampleTransactionGroups

@Composable
internal fun TransactionCard(
    transactions: List<TransactionUiModel>,
    modifier: Modifier = Modifier
) {
    val shape = MaterialTheme.shapes.large

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = shape,
                ambientColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.07f),
                spotColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.07f)
            )
            .clip(shape = shape)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        transactions.forEachIndexed { index, transaction ->
            if (index > 0) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
            TransactionItemRow(
                transaction = transaction
            )
        }
    }
}

@Composable
internal fun TransactionItemRow(
    transaction: TransactionUiModel,
    modifier: Modifier = Modifier
) {
    val iconColors = transaction.icon.colors()
    val locale = LocalConfiguration.current.locales[0]

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = BrutusTheme.spacing.large,
                vertical = BrutusTheme.spacing.medium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size = 42.dp)
                .clip(shape = CircleShape)
                .background(color = iconColors.background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = transaction.icon.imageVector(),
                contentDescription = null,
                tint = iconColors.content,
                modifier = Modifier.size(size = 22.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(BrutusTheme.spacing.medium)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (transaction.tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = BrutusTheme.spacing.small),
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {
                    transaction.tags.forEach { tag ->
                        TransactionTag(text = tag)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(BrutusTheme.spacing.small))

        Text(
            text = formatTransactionAmount(
                amount = transaction.amount,
                currencyCode = transaction.currencyCode,
                locale = locale
            ),
            style = MaterialTheme.typography.labelLarge,
            color = if (transaction.kind == TransactionKind.INCOME) {
                BrutusTheme.colors.income
            } else {
                BrutusTheme.colors.expense
            },
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun TransactionCardPreview() {
    BrutusFlyTheme {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(all = BrutusTheme.spacing.large)
        ) {
            TransactionCard(
                transactions = sampleTransactionGroups.first().transactions
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun TransactionItemRowPreview() {
    BrutusFlyTheme {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(all = BrutusTheme.spacing.large)
        ) {
            TransactionItemRow(
                transaction = sampleTransactionGroups.first().transactions.first().copy(
                    tags = listOf("Travel", "Restaurant", "Vacation")
                )
            )
            TransactionItemRow(
                transaction = sampleTransactionGroups.first().transactions.last()
            )
        }
    }
}
