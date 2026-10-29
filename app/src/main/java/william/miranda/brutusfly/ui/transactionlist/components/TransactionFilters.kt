package william.miranda.brutusfly.ui.transactionlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.R
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.domain.model.category.Category
import william.miranda.brutusfly.ui.transactionlist.model.TransactionFilter
import william.miranda.brutusfly.ui.transactionlist.model.TransactionPeriod

@Composable
internal fun TransactionFilterRow(
    selectedFilter: TransactionFilter,
    onFilterSelected: (TransactionFilter) -> Unit,
    selectedPeriod: TransactionPeriod,
    onPeriodSelected: (TransactionPeriod) -> Unit,
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(BrutusTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransactionFilterChip(
            label = TransactionFilter.ALL.label,
            selected = selectedFilter == TransactionFilter.ALL,
            onClick = { onFilterSelected(TransactionFilter.ALL) }
        )
        TransactionFilterChip(
            label = TransactionFilter.INCOME.label,
            selected = selectedFilter == TransactionFilter.INCOME,
            onClick = { onFilterSelected(TransactionFilter.INCOME) }
        )
        TransactionFilterChip(
            label = TransactionFilter.EXPENSE.label,
            selected = selectedFilter == TransactionFilter.EXPENSE,
            onClick = { onFilterSelected(TransactionFilter.EXPENSE) }
        )

        TransactionPeriodChip(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected
        )

        TransactionCategoryChip(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )
    }
}

@Composable
private fun TransactionFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(shape = CircleShape)
            .background(
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
                }
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = BrutusTheme.spacing.large
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
    }
}

@Composable
private fun TransactionPeriodChip(
    selectedPeriod: TransactionPeriod,
    onPeriodSelected: (TransactionPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    TransactionDropdownChip(
        label = selectedPeriod.label,
        options = TransactionPeriod.entries.map { it.label },
        selectedIndex = TransactionPeriod.entries.indexOf(element = selectedPeriod),
        onOptionSelected = { onPeriodSelected(TransactionPeriod.entries[it]) },
        modifier = modifier
    )
}

@Composable
private fun TransactionCategoryChip(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        stringResource(id = R.string.transactions_categories_all)
    ) + categories.map { it.name }
    val selectedIndex = categories.indexOfFirst { it.id == selectedCategory?.id } + 1

    TransactionDropdownChip(
        label = if (selectedIndex == 0) stringResource(id = R.string.categories_title)
        else categories[selectedIndex - 1].name,
        options = options,
        selectedIndex = selectedIndex,
        onOptionSelected = { index ->
            onCategorySelected(categories.getOrNull(index = index - 1))
        },
        modifier = modifier
    )
}

@Composable
private fun TransactionDropdownChip(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .height(36.dp)
                .clip(shape = CircleShape)
                .background(color = MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape
                )
                .clickable { expanded = true }
                .padding(
                    start = BrutusTheme.spacing.large,
                    end = BrutusTheme.spacing.medium
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(
                modifier = Modifier.width(BrutusTheme.spacing.small)
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(size = 18.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (index == selectedIndex) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        onOptionSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun TransactionFilterRowPreview() {
    BrutusFlyTheme {
        var filter by remember {
            mutableStateOf(TransactionFilter.ALL)
        }
        var period by remember {
            mutableStateOf(TransactionPeriod.THIS_MONTH)
        }
        val categories = listOf(
            Category(
                id = "sample-food",
                name = "Food",
                notes = null
            )
        )
        var category by remember { mutableStateOf<Category?>(null) }

        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(all = BrutusTheme.spacing.large)
        ) {
            TransactionFilterRow(
                selectedFilter = filter,
                onFilterSelected = { filter = it },
                selectedPeriod = period,
                onPeriodSelected = { period = it },
                categories = categories,
                selectedCategory = category,
                onCategorySelected = { category = it }
            )
        }
    }
}
