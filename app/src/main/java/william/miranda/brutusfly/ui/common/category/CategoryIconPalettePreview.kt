package william.miranda.brutusfly.ui.common.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme

/** Shows all assignments without opening a modal, for visual comparison in both themes. */
@Composable
private fun CategoryIconPaletteGallery() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 3),
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(all = BrutusTheme.spacing.large),
        horizontalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.small)
    ) {
        items(
            items = CategoryIconCatalog.groups.flatMap { it.options },
            key = { it.icon.key }
        ) { option ->
            val colors = option.icon.colors()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(color = colors.background)
                    .padding(all = BrutusTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.small)
            ) {
                Icon(
                    imageVector = option.icon.imageVector(),
                    contentDescription = null,
                    tint = colors.content,
                    modifier = Modifier.size(size = 28.dp)
                )
                Text(
                    text = stringResource(id = option.labelRes),
                    color = colors.content,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(name = "Category icon colors — light", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryIconPaletteLightPreview() {
    BrutusFlyTheme(darkTheme = false) {
        CategoryIconPaletteGallery()
    }
}

@Preview(name = "Category icon colors — dark", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryIconPaletteDarkPreview() {
    BrutusFlyTheme(darkTheme = true) {
        CategoryIconPaletteGallery()
    }
}
