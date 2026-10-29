package william.miranda.brutusfly.ui.categorylist

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import william.miranda.brutusfly.core.ui.BaseViewModel
import william.miranda.brutusfly.domain.model.category.Category
import william.miranda.brutusfly.domain.model.category.CategoryFailure
import william.miranda.brutusfly.domain.model.category.CategoryIcon
import william.miranda.brutusfly.domain.model.category.CategoryLoadException
import william.miranda.brutusfly.domain.usecase.category.GetCategoriesUseCase
import william.miranda.brutusfly.domain.usecase.categoryicon.AssignCategoryIconUseCase
import william.miranda.brutusfly.domain.usecase.categoryicon.ObserveCategoryIconsUseCase
import william.miranda.brutusfly.domain.usecase.categoryicon.RemoveCategoryIconUseCase
import william.miranda.brutusfly.ui.categorylist.mapper.CategoryListUiMapper
import javax.inject.Inject

@HiltViewModel
internal class CategoryListViewModel @Inject constructor(
    private val getCategories: GetCategoriesUseCase,
    private val observeCategoryIcons: ObserveCategoryIconsUseCase,
    private val assignCategoryIcon: AssignCategoryIconUseCase,
    private val removeCategoryIcon: RemoveCategoryIconUseCase,
    private val mapper: CategoryListUiMapper
) : BaseViewModel<CategoryListState, CategoryListAction, Nothing>(
    initialState = CategoryListState()
) {
    private var loadJob: Job? = null
    private var iconsJob: Job? = null
    private var loadedCategories: List<Category> = emptyList()
    private var categoryIcons: Map<String, CategoryIcon> = emptyMap()

    init {
        observeIcons()
        loadPage(
            page = 1,
            append = false
        )
    }

    override fun onAction(
        action: CategoryListAction
    ) {
        when (action) {
            CategoryListAction.Refresh,
            CategoryListAction.Retry -> loadPage(
                page = 1,
                append = false
            )
            CategoryListAction.LoadNextPage -> loadNextPage()
            CategoryListAction.RetryNextPage -> loadNextPage(
                retry = true
            )
            CategoryListAction.RetryIcons -> observeIcons()
            is CategoryListAction.ChooseIcon -> {
                val current = state.value
                if (!current.isLoadingIcons && !current.iconReadError && !current.isSavingIcon &&
                    current.categories.any { it.id == action.categoryId }
                ) {
                    updateState {
                        copy(iconPickerCategoryId = action.categoryId, iconSaveError = false)
                    }
                }
            }
            CategoryListAction.DismissIconPicker -> {
                if (!state.value.isSavingIcon) {
                    updateState { copy(iconPickerCategoryId = null, iconSaveError = false) }
                }
            }
            is CategoryListAction.AssignIcon -> saveIcon(icon = action.icon)
            CategoryListAction.RemoveIcon -> saveIcon(icon = null)
            is CategoryListAction.SearchChanged -> updateState {
                copy(
                    query = action.query
                )
            }
        }
    }

    private fun observeIcons() {
        iconsJob?.cancel()
        updateState { copy(isLoadingIcons = true, iconReadError = false) }
        iconsJob = viewModelScope.launch {
            try {
                observeCategoryIcons(input = Unit).collect { icons ->
                    categoryIcons = icons
                    val mappedCategories = mapper.map(
                        input = CategoryListUiMapper.Input(
                            categories = loadedCategories,
                            icons = icons
                        )
                    )
                    updateState {
                        copy(categories = mappedCategories, isLoadingIcons = false, iconReadError = false)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                updateState { copy(isLoadingIcons = false, iconReadError = true) }
            }
        }
    }

    private fun saveIcon(
        icon: CategoryIcon?
    ) {
        val current = state.value
        val categoryId = current.iconPickerCategory?.id ?: return
        if (current.isSavingIcon || current.isLoadingIcons || current.iconReadError) return
        updateState { copy(isSavingIcon = true, iconSaveError = false) }
        viewModelScope.launch {
            try {
                if (icon == null) {
                    removeCategoryIcon(
                        input = RemoveCategoryIconUseCase.Input(categoryId = categoryId)
                    )
                } else {
                    assignCategoryIcon(
                        input = AssignCategoryIconUseCase.Input(categoryId = categoryId, icon = icon)
                    )
                }
                updateState { copy(isSavingIcon = false, iconPickerCategoryId = null) }
            } catch (cancelled: CancellationException) {
                updateState { copy(isSavingIcon = false) }
                throw cancelled
            } catch (exception: Exception) {
                updateState { copy(isSavingIcon = false, iconSaveError = true) }
            }
        }
    }

    private fun loadNextPage(
        retry: Boolean = false
    ) {
        val current = state.value
        if (!current.hasLoaded || current.error != null || (!retry && current.loadMoreError != null)) return
        loadPage(
            page = current.nextPage ?: return,
            append = true
        )
    }

    private fun loadPage(
        page: Int,
        append: Boolean
    ) {
        if (loadJob?.isActive == true || state.value.isLoading || state.value.isLoadingMore) return

        updateState {
            copy(
                isLoading = !append,
                isLoadingMore = append,
                error = null,
                loadMoreError = null
            )
        }
        loadJob = viewModelScope.launch {
            try {
                val result = getCategories(
                    input = GetCategoriesUseCase.Input(
                        page = page
                    )
                )
                val combined = ((if (append) loadedCategories else emptyList()) + result.categories)
                    .associateBy { it.id }.values.toList()
                val mappedCategories = mapper.map(
                    input = CategoryListUiMapper.Input(
                        categories = combined,
                        icons = categoryIcons
                    )
                )
                loadedCategories = combined
                updateState {
                    copy(
                        categories = mappedCategories,
                        hasLoaded = true,
                        isLoading = false,
                        isLoadingMore = false,
                        error = null,
                        loadMoreError = null,
                        nextPage = result.nextPage
                    )
                }
            } catch (cancelled: CancellationException) {
                updateState {
                    copy(
                        isLoading = false,
                        isLoadingMore = false
                    )
                }
                throw cancelled
            } catch (exception: Exception) {
                val failure = when ((exception as? CategoryLoadException)?.failure) {
                    CategoryFailure.NETWORK -> CategoryListError.Network
                    CategoryFailure.UNAUTHORIZED -> CategoryListError.Unauthorized
                    CategoryFailure.SERVER -> CategoryListError.Server
                    CategoryFailure.UNKNOWN, null -> CategoryListError.Unknown
                }
                updateState {
                    if (append) copy(
                        isLoadingMore = false,
                        loadMoreError = failure
                    ) else copy(
                        isLoading = false,
                        error = failure
                    )
                }
            }
        }
    }
}
