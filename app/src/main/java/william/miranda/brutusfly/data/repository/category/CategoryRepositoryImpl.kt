package william.miranda.brutusfly.data.repository.category

import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.core.network.model.ApiResult
import william.miranda.brutusfly.data.mapper.category.CategoryApiErrorMapper
import william.miranda.brutusfly.data.mapper.category.CategoryPageDtoMapper
import william.miranda.brutusfly.data.remote.FireflyApi
import william.miranda.brutusfly.domain.model.category.CategoryFailure
import william.miranda.brutusfly.domain.model.category.CategoryLoadException
import william.miranda.brutusfly.domain.model.category.CategoryPage
import william.miranda.brutusfly.domain.repository.category.CategoryRepository
import javax.inject.Inject

/** Loads category pages using the configured server URL and personal access token. */
class CategoryRepositoryImpl @Inject constructor(
    private val api: FireflyApi,
    private val mapper: CategoryPageDtoMapper,
    private val errorMapper: CategoryApiErrorMapper
) : CategoryRepository {
    /** Translates expected failures; cancellation and unexpected exceptions propagate. */
    override suspend fun getCategories(
        page: Int,
        limit: Int
    ): CategoryPage = try {
        when (val result = api.getCategories(
            page = page,
            limit = limit
        )) {
            is ApiResult.Success -> mapper.map(
                input = result.data
            ).also { mapped ->
                if (mapped.currentPage != page) throw MappingException(
                    message = "Server returned a different category page"
                )
            }
            is ApiResult.Error -> throw errorMapper.map(
                input = result.error
            )
        }
    } catch (exception: MappingException) {
        throw CategoryLoadException(
            failure = CategoryFailure.UNKNOWN,
            cause = exception
        )
    }
}
