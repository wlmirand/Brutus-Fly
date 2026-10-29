package william.miranda.brutusfly.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.R
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.ui.navigation.AppDestination

@Composable
internal fun AppBottomBar(
    destinations: List<AppDestination>,
    selectedDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface
            )
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    height = 72.dp
                )
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { destination ->
                val selected = destination == selectedDestination
                val contentColor = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Column(
                    modifier = Modifier
                        .weight(
                            weight = 1f
                        )
                        .fillMaxHeight()
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = {
                                onDestinationSelected(destination)
                            }
                        )
                        .padding(
                            top = BrutusTheme.spacing.small,
                            bottom = BrutusTheme.spacing.small
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = destination.icon(),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(
                            size = 23.dp
                        )
                    )
                    Spacer(
                        modifier = Modifier.height(
                            height = BrutusTheme.spacing.xs
                        )
                    )
                    Text(
                        text = stringResource(
                            id = when (destination) {
                                AppDestination.Summary -> R.string.summary_title
                                AppDestination.Transactions -> R.string.transactions_title
                                AppDestination.Accounts -> R.string.accounts_title
                                AppDestination.Categories -> R.string.categories_title
                            }
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Medium
                            }
                        ),
                        color = contentColor
                    )
                }
            }
        }
    }
}

private fun AppDestination.icon(): ImageVector =
    when (this) {
        AppDestination.Summary -> Icons.Outlined.BarChart
        AppDestination.Transactions -> Icons.AutoMirrored.Outlined.FormatListBulleted
        AppDestination.Accounts -> Icons.Outlined.AccountBalanceWallet
        AppDestination.Categories -> Icons.Outlined.PieChart
    }

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun AppBottomBarPreview() {
    BrutusFlyTheme {
        AppBottomBar(
            destinations = listOf(
                AppDestination.Accounts,
                AppDestination.Transactions,
                AppDestination.Categories
            ),
            selectedDestination = AppDestination.Transactions,
            onDestinationSelected = {}
        )
    }
}
