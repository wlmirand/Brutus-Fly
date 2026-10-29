package william.miranda.brutusfly.ui.categorylist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.R
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.domain.model.category.CategoryIcon
import william.miranda.brutusfly.ui.categorylist.model.CategoryUiModel
import william.miranda.brutusfly.ui.common.category.CategoryIconCatalog
import william.miranda.brutusfly.ui.common.category.CategoryIconOption
import william.miranda.brutusfly.ui.common.category.colors
import william.miranda.brutusfly.ui.common.category.imageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryIconPicker(
    category: CategoryUiModel,
    isSaving: Boolean,
    hasSaveError: Boolean,
    hasReadError: Boolean,
    editingEnabled: Boolean,
    onSelect: (CategoryIcon) -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit,
    onRetryIcons: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentIsSaving by rememberUpdatedState(newValue = isSaving)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { !currentIsSaving }
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetGesturesEnabled = false,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.85f)
                .padding(horizontal = BrutusTheme.spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.small)
        ) {
            Text(
                text = stringResource(id = R.string.category_icon_picker_title, category.name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() }
            )
            if (isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(text = stringResource(id = R.string.category_icon_saving))
            }
            if (hasSaveError) {
                Text(
                    text = stringResource(id = R.string.category_icon_save_error),
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (hasReadError) {
                Text(
                    text = stringResource(id = R.string.category_icon_read_error),
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = onRetryIcons, enabled = !isSaving) {
                    Text(text = stringResource(id = R.string.category_icon_retry))
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 88.dp),
                modifier = Modifier.weight(weight = 1f).selectableGroup(),
                contentPadding = PaddingValues(vertical = BrutusTheme.spacing.small),
                horizontalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.small)
            ) {
                CategoryIconCatalog.groups.forEach { group ->
                    item(
                        key = "group-${group.labelRes}",
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        Text(
                            text = stringResource(id = group.labelRes),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(vertical = BrutusTheme.spacing.small)
                                .semantics { heading() }
                        )
                    }
                    items(items = group.options, key = { it.icon.key }) { option ->
                        CategoryIconTile(
                            option = option,
                            selected = category.icon == option.icon,
                            enabled = editingEnabled && !isSaving,
                            onClick = { onSelect(option.icon) }
                        )
                    }
                }
            }
            TextButton(
                onClick = onRemove,
                enabled = editingEnabled && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.category_icon_reset))
            }
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.category_icon_close))
            }
        }
    }
}

@Preview(name = "Category icon picker — light", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryIconPickerPreview() {
    BrutusFlyTheme(darkTheme = false) {
        CategoryIconPicker(
            category = CategoryUiModel(id = "food", name = "Food", icon = CategoryIcon.RESTAURANT),
            isSaving = false,
            hasSaveError = false,
            hasReadError = false,
            editingEnabled = true,
            onSelect = {},
            onRemove = {},
            onDismiss = {},
            onRetryIcons = {}
        )
    }
}

@Preview(name = "Category icon picker — save error", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryIconPickerErrorPreview() {
    BrutusFlyTheme(darkTheme = true) {
        CategoryIconPicker(
            category = CategoryUiModel(id = "food", name = "Food", icon = CategoryIcon.RESTAURANT),
            isSaving = false,
            hasSaveError = true,
            hasReadError = false,
            editingEnabled = true,
            onSelect = {},
            onRemove = {},
            onDismiss = {},
            onRetryIcons = {}
        )
    }
}

@Composable
private fun CategoryIconTile(
    option: CategoryIconOption,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val iconColors = option.icon.colors()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 88.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .background(color = iconColors.background)
            .border(
                width = 2.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .padding(all = BrutusTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = BrutusTheme.spacing.xs,
            alignment = Alignment.CenterVertically
        )
    ) {
        Icon(
            imageVector = option.icon.imageVector(),
            contentDescription = null,
            tint = iconColors.content,
            modifier = Modifier.size(size = 28.dp)
        )
        Text(
            text = stringResource(id = option.labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = iconColors.content,
            textAlign = TextAlign.Center
        )
    }
}
