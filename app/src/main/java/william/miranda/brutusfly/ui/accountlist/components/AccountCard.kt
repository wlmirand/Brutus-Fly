package william.miranda.brutusfly.ui.accountlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.R
import william.miranda.brutusfly.core.designsystem.components.BrutusCard
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.ui.accountlist.model.AccountKind
import william.miranda.brutusfly.ui.accountlist.model.AccountUiModel
import william.miranda.brutusfly.ui.accountlist.model.sampleAccounts

@Composable
internal fun AccountCard(
    account: AccountUiModel,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    selectionEnabled: Boolean = true
) {
    BrutusCard(
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            )
            .clip(
                shape = MaterialTheme.shapes.large
            )
            .selectable(
                selected = isSelected,
                enabled = account.isSelectable && selectionEnabled,
                role = Role.RadioButton,
                onClick = onSelect
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = BrutusTheme.spacing.large
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = BrutusTheme.spacing.medium
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(
                        size = 44.dp
                    ).clip(
                        shape = MaterialTheme.shapes.medium
                    )
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(
                    modifier = Modifier.weight(
                        weight = 1f
                    )
                ) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = accountTypeLabel(
                            kind = account.kind
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!account.isActive) {
                    Text(
                        text = stringResource(
                            id = R.string.accounts_inactive
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    enabled = account.isSelectable && selectionEnabled
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        id = R.string.accounts_balance
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = account.balance ?: stringResource(
                        id = R.string.accounts_balance_unavailable
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (account.hasNegativeBalance) BrutusTheme.colors.expense else MaterialTheme.colorScheme.onSurface
                )
            }
            if (isSelected) {
                Text(
                    text = stringResource(
                        id = R.string.accounts_selected
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (!account.isSelectable) {
                Text(
                    text = stringResource(
                        id = R.string.accounts_selection_unavailable
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun accountTypeLabel(
    kind: AccountKind
): String = stringResource(
    id = when (kind) {
        AccountKind.ASSET -> R.string.accounts_type_asset
        AccountKind.CASH -> R.string.accounts_type_cash
        AccountKind.EXPENSE -> R.string.accounts_type_expense
        AccountKind.REVENUE -> R.string.accounts_type_revenue
        AccountKind.LIABILITY -> R.string.accounts_type_liability
        AccountKind.INITIAL_BALANCE -> R.string.accounts_type_initial_balance
        AccountKind.RECONCILIATION -> R.string.accounts_type_reconciliation
        AccountKind.OTHER -> R.string.accounts_type_other
    }
)

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun AccountCardPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        Box(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background
            ).padding(
                all = BrutusTheme.spacing.large
            )
        ) {
            AccountCard(
                account = sampleAccounts.first(),
                isSelected = true,
                onSelect = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun AccountCardDarkPreview() {
    BrutusFlyTheme(
        darkTheme = true
    ) {
        Box(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background
            ).padding(
                all = BrutusTheme.spacing.large
            )
        ) {
            AccountCard(
                account = sampleAccounts[2],
                isSelected = false,
                onSelect = {}
            )
        }
    }
}
