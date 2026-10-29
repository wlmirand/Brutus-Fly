package william.miranda.brutusfly.data.remote.dto.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Category resources and server-reported pagination for GET api/v1/categories. */
@Serializable
data class CategoriesResponse(
    val data: List<CategoryDto>,
    val meta: CategoryMetaDto
)

/** Transport envelope identifying one category and its attributes. */
@Serializable
data class CategoryDto(
    val id: String,
    val attributes: CategoryAttributesDto
)

@Serializable
data class CategoryAttributesDto(
    val name: String,
    val notes: String? = null
)

/** Pagination metadata associated with a category response. */
@Serializable
data class CategoryMetaDto(
    val pagination: CategoryPaginationDto
)

/** Page boundaries used to stop loading without guessing from the number of returned items. */
@Serializable
data class CategoryPaginationDto(
    @SerialName("current_page") val currentPage: Int,
    @SerialName("total_pages") val totalPages: Int
)
