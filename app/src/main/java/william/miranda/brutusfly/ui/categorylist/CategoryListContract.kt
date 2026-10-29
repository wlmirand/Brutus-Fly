package william.miranda.brutusfly.ui.categorylist

import william.miranda.brutusfly.domain.model.category.CategoryIcon
import william.miranda.brutusfly.ui.categorylist.model.CategoryUiModel

internal data class CategoryListState(
    val categories: List<CategoryUiModel> = emptyList(),
    val query: String = "",
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val error: CategoryListError? = null,
    val nextPage: Int? = null,
    val isLoadingMore: Boolean = false,
    val loadMoreError: CategoryListError? = null,
    val isLoadingIcons: Boolean = true,
    val iconReadError: Boolean = false,
    val iconPickerCategoryId: String? = null,
    val isSavingIcon: Boolean = false,
    val iconSaveError: Boolean = false
) {
    val iconPickerCategory: CategoryUiModel?
        get() = categories.firstOrNull { it.id == iconPickerCategoryId }

    val isSearching: Boolean
        get() = query.isNotBlank()

    val filteredCategories: List<CategoryUiModel>
        get() {
            val search = query.trim()
            return categories.filter {
                it.name.contains(search, ignoreCase = true) ||
                    it.notes?.contains(search, ignoreCase = true) == true
            }
        }
}

internal sealed interface CategoryListAction {
    data object Refresh : CategoryListAction
    data object Retry : CategoryListAction
    data object LoadNextPage : CategoryListAction
    data object RetryNextPage : CategoryListAction
    data object RetryIcons : CategoryListAction
    data class ChooseIcon(
        val categoryId: String
    ) : CategoryListAction
    data object DismissIconPicker : CategoryListAction
    data class AssignIcon(
        val icon: CategoryIcon
    ) : CategoryListAction
    data object RemoveIcon : CategoryListAction
    data class SearchChanged(
        val query: String
    ) : CategoryListAction
}

internal sealed interface CategoryListError {
    data object Network : CategoryListError
    data object Unauthorized : CategoryListError
    data object Server : CategoryListError
    data object Unknown : CategoryListError
}
