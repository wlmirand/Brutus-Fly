package william.miranda.brutusfly.data.mapper.category

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.remote.dto.category.CategoriesResponse
import william.miranda.brutusfly.domain.model.category.CategoryPage
import javax.inject.Inject

/** Converts a complete page, rejecting invalid metadata rather than silently skipping resources. */
class CategoryPageDtoMapper @Inject constructor(
    private val categoryMapper: CategoryDtoMapper
) : Mapper<CategoriesResponse, CategoryPage> {
    override fun map(
        input: CategoriesResponse
    ): CategoryPage {
        val pagination = input.meta.pagination
        if (pagination.currentPage < 1 || pagination.totalPages < 0 ||
            (pagination.totalPages > 0 && pagination.currentPage > pagination.totalPages) ||
            (pagination.totalPages == 0 && input.data.isNotEmpty())
        ) {
            throw MappingException(
                message = "Invalid category pagination metadata"
            )
        }
        return CategoryPage(
            categories = input.data.map(
                transform = categoryMapper::map
            ),
            currentPage = pagination.currentPage,
            totalPages = pagination.totalPages
        )
    }
}
