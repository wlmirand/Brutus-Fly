package william.miranda.brutusfly.data.mapper.category

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.data.remote.dto.category.CategoryDto
import william.miranda.brutusfly.domain.model.category.Category
import javax.inject.Inject

/** Converts a category resource into a domain model, rejecting invalid identity or name. */
class CategoryDtoMapper @Inject constructor() : Mapper<CategoryDto, Category> {
    override fun map(
        input: CategoryDto
    ): Category {
        if (input.id.isBlank() || input.attributes.name.isBlank()) {
            throw MappingException(
                message = "Category ID and name must not be blank"
            )
        }
        return Category(
            id = input.id,
            name = input.attributes.name,
            notes = input.attributes.notes
        )
    }
}
