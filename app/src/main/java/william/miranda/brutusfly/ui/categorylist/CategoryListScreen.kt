package william.miranda.brutusfly.ui.categorylist

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.distinctUntilChanged
import william.miranda.brutusfly.R
import william.miranda.brutusfly.core.designsystem.components.BrutusScreenHeader
import william.miranda.brutusfly.core.designsystem.components.BrutusSearchField
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.ui.categorylist.components.CategoryCard
import william.miranda.brutusfly.ui.categorylist.components.CategoryIconPicker
import william.miranda.brutusfly.ui.categorylist.components.CategoryListFailure
import william.miranda.brutusfly.ui.categorylist.components.CategoryListFooter
import william.miranda.brutusfly.ui.categorylist.model.sampleCategories

@Composable
internal fun CategoryListScreen(
    state: CategoryListState,
    onAction: (CategoryListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleCategories = state.filteredCategories
    val listState = rememberLazyListState()
    val currentOnAction by rememberUpdatedState(newValue = onAction)
    val footerKey = "categories-pagination-${state.nextPage}"

    LaunchedEffect(
        listState,
        state.nextPage,
        state.hasLoaded,
        state.isLoading,
        state.isLoadingMore,
        state.error,
        state.loadMoreError,
        state.query
    ) {
        if (!state.hasLoaded || state.nextPage == null || state.isLoading || state.isLoadingMore ||
            state.error != null || state.loadMoreError != null
        ) return@LaunchedEffect

        snapshotFlow { listState.layoutInfo.visibleItemsInfo.any { it.key == footerKey } }
            .distinctUntilChanged()
            .collect { visible ->
                if (visible) currentOnAction(CategoryListAction.LoadNextPage)
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        BrutusScreenHeader(
            title = stringResource(id = R.string.categories_title),
            subtitle = stringResource(id = R.string.categories_subtitle),
            modifier = Modifier.padding(
                horizontal = BrutusTheme.spacing.screenHorizontal,
                vertical = BrutusTheme.spacing.large
            ),
            actions = {
                IconButton(
                    onClick = { onAction(CategoryListAction.Refresh) },
                    enabled = !state.isLoading && !state.isLoadingMore,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = stringResource(id = R.string.categories_refresh)
                    )
                }
            }
        )
        BrutusSearchField(
            query = state.query,
            onQueryChange = {
                onAction(
                    CategoryListAction.SearchChanged(
                        query = it
                    )
                )
            },
            placeholder = stringResource(id = R.string.categories_search),
            modifier = Modifier.padding(
                start = BrutusTheme.spacing.screenHorizontal,
                top = BrutusTheme.spacing.small,
                end = BrutusTheme.spacing.screenHorizontal
            )
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(weight = 1f),
            contentPadding = PaddingValues(
                start = BrutusTheme.spacing.screenHorizontal,
                top = BrutusTheme.spacing.large,
                end = BrutusTheme.spacing.screenHorizontal,
                bottom = BrutusTheme.spacing.extraLarge
            ),
            verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.medium)
        ) {
            if (state.isLoadingIcons || state.iconReadError) {
                item(key = "category-icons-status") {
                    Column {
                        Text(
                            text = stringResource(
                                id = if (state.iconReadError) R.string.category_icon_read_error
                                else R.string.category_icon_loading
                            ),
                            color = if (state.iconReadError) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (state.iconReadError) {
                            TextButton(onClick = { onAction(CategoryListAction.RetryIcons) }) {
                                Text(text = stringResource(id = R.string.category_icon_retry))
                            }
                        }
                    }
                }
            }
            if (state.error != null) {
                item(key = "categories-error") {
                    CategoryListFailure(
                        error = state.error,
                        onRetry = { onAction(CategoryListAction.Retry) }
                    )
                }
            }
            if (state.hasLoaded) {
                item(key = "categories-count") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = BrutusTheme.spacing.xs)
                    ) {
                        Text(
                            text = pluralStringResource(
                                R.plurals.categories_count,
                                visibleCategories.size,
                                visibleCategories.size
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (state.nextPage != null) {
                            Text(
                                text = stringResource(id = R.string.categories_partial_results),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            if (state.hasLoaded && !state.isLoading && state.error == null && visibleCategories.isEmpty()) {
                item(key = "categories-empty") {
                    Text(
                        text = stringResource(
                            id = if (state.nextPage != null) R.string.categories_empty_loaded
                            else if (state.isSearching) R.string.categories_empty_search
                            else R.string.categories_empty
                        ),
                        modifier = Modifier.padding(vertical = BrutusTheme.spacing.huge),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(
                items = visibleCategories,
                key = { "category-${it.id}" }
            ) { category ->
                CategoryCard(
                    category = category,
                    iconEditingEnabled = !state.isLoadingIcons && !state.iconReadError && !state.isSavingIcon,
                    onChooseIcon = {
                        onAction(CategoryListAction.ChooseIcon(categoryId = category.id))
                    }
                )
            }
            item(key = footerKey) {
                CategoryListFooter(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
    state.iconPickerCategory?.let { category ->
        CategoryIconPicker(
            category = category,
            isSaving = state.isSavingIcon,
            hasSaveError = state.iconSaveError,
            hasReadError = state.iconReadError,
            editingEnabled = !state.isLoadingIcons && !state.iconReadError,
            onSelect = { onAction(CategoryListAction.AssignIcon(icon = it)) },
            onRemove = { onAction(CategoryListAction.RemoveIcon) },
            onDismiss = { onAction(CategoryListAction.DismissIconPicker) },
            onRetryIcons = { onAction(CategoryListAction.RetryIcons) }
        )
    }
}

@Preview(
    name = "Categories — light", showBackground = true, widthDp = 393, heightDp = 852,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Categories — dark", showBackground = true, widthDp = 393, heightDp = 852,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CategoryListPreview() {
    BrutusFlyTheme {
        CategoryListScreen(
            state = CategoryListState(
                categories = sampleCategories,
                hasLoaded = true,
                isLoadingIcons = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Categories — search", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryListSearchPreview() {
    BrutusFlyTheme(darkTheme = false) {
        CategoryListScreen(
            state = CategoryListState(
                categories = sampleCategories,
                query = "housing",
                hasLoaded = true,
                isLoadingIcons = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Categories — empty", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryListEmptyPreview() {
    BrutusFlyTheme(darkTheme = false) {
        CategoryListScreen(
            state = CategoryListState(hasLoaded = true, isLoadingIcons = false),
            onAction = {}
        )
    }
}

@Preview(name = "Categories — no results", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryListNoResultsPreview() {
    BrutusFlyTheme(darkTheme = true) {
        CategoryListScreen(
            state = CategoryListState(
                categories = sampleCategories,
                query = "travel",
                hasLoaded = true,
                isLoadingIcons = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Categories — loading", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryListLoadingPreview() {
    BrutusFlyTheme(darkTheme = false) {
        CategoryListScreen(
            state = CategoryListState(isLoading = true, isLoadingIcons = false),
            onAction = {}
        )
    }
}

@Preview(name = "Categories — error", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryListErrorPreview() {
    BrutusFlyTheme(darkTheme = true) {
        CategoryListScreen(
            state = CategoryListState(error = CategoryListError.Network, isLoadingIcons = false),
            onAction = {}
        )
    }
}

@Preview(name = "Categories — loading more", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryListLoadingMorePreview() {
    BrutusFlyTheme(darkTheme = false) {
        CategoryListScreen(
            state = CategoryListState(
                categories = sampleCategories.take(3),
                hasLoaded = true,
                nextPage = 2,
                isLoadingMore = true,
                isLoadingIcons = false
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Categories — page retry", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CategoryListPageErrorPreview() {
    BrutusFlyTheme(darkTheme = true) {
        CategoryListScreen(
            state = CategoryListState(
                categories = sampleCategories.take(3),
                hasLoaded = true,
                nextPage = 2,
                loadMoreError = CategoryListError.Network,
                isLoadingIcons = false
            ),
            onAction = {}
        )
    }
}
