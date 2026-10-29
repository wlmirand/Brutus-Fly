package william.miranda.brutusfly.ui.categorylist.mapper

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.domain.model.category.Category
import william.miranda.brutusfly.domain.model.category.CategoryIcon
import william.miranda.brutusfly.ui.categorylist.model.CategoryUiModel
import javax.inject.Inject

internal class CategoryListUiMapper @Inject constructor() :
    Mapper<CategoryListUiMapper.Input, List<CategoryUiModel>> {

    data class Input(
        val categories: List<Category>,
        val icons: Map<String, CategoryIcon> = emptyMap()
    )

    override fun map(
        input: Input
    ): List<CategoryUiModel> = input.categories.map { category ->
        CategoryUiModel(
            id = category.id,
            name = category.name,
            notes = category.notes,
            icon = input.icons[category.id] ?: CategoryIcon.LABEL
        )
    }
}
