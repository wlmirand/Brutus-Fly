package william.miranda.brutusfly.domain.usecase.category

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.model.category.CategoryPage
import william.miranda.brutusfly.domain.repository.category.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) : SuspendUseCase<GetCategoriesUseCase.Input, CategoryPage> {

    data class Input(
        val page: Int = 1,
        val limit: Int = 50
    )

    override suspend fun invoke(
        input: Input
    ): CategoryPage = repository.getCategories(
        page = input.page,
        limit = input.limit
    )
}
