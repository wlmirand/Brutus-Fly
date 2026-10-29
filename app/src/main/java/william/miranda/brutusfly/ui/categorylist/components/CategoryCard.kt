package william.miranda.brutusfly.ui.categorylist.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import william.miranda.brutusfly.core.designsystem.components.BrutusCard
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.ui.categorylist.model.CategoryUiModel
import william.miranda.brutusfly.ui.categorylist.model.sampleCategories
import william.miranda.brutusfly.ui.common.category.colors
import william.miranda.brutusfly.ui.common.category.imageVector

@Composable
internal fun CategoryCard(
    category: CategoryUiModel,
    onChooseIcon: () -> Unit,
    iconEditingEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val iconColors = category.icon.colors()
    var menuExpanded by remember(key1 = category.id) { mutableStateOf(value = false) }
    BrutusCard(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = BrutusTheme.spacing.medium
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(size = 44.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(color = iconColors.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon.imageVector(),
                    contentDescription = null,
                    tint = iconColors.content
                )
            }
            Column(
                modifier = Modifier.weight(weight = 1f),
                verticalArrangement = Arrangement.spacedBy(
                    space = BrutusTheme.spacing.xs
                )
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!category.notes.isNullOrBlank()) {
                    Text(
                        text = category.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    enabled = iconEditingEnabled
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(
                            id = R.string.category_icon_menu,
                            category.name
                        )
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded && iconEditingEnabled,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.category_icon_choose)) },
                        onClick = {
                            menuExpanded = false
                            onChooseIcon()
                        }
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Category card — light", showBackground = true, widthDp = 393,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Category card — dark", showBackground = true, widthDp = 393,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CategoryCardPreview() {
    BrutusFlyTheme {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(all = BrutusTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.medium)
        ) {
            CategoryCard(category = sampleCategories.first(), onChooseIcon = {})
            CategoryCard(category = sampleCategories[2], onChooseIcon = {})
        }
    }
}
