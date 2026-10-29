package william.miranda.brutusfly.domain.usecase.categoryicon

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.model.category.CategoryIcon
import william.miranda.brutusfly.domain.repository.categoryicon.CategoryIconRepository
import javax.inject.Inject

class AssignCategoryIconUseCase @Inject constructor(
    private val repository: CategoryIconRepository
) : SuspendUseCase<AssignCategoryIconUseCase.Input, Unit> {
    data class Input(
        val categoryId: String,
        val icon: CategoryIcon
    )

    override suspend fun invoke(
        input: Input
    ) {
        require(input.categoryId.isNotBlank()) { "Category ID must not be blank" }
        repository.assign(
            categoryId = input.categoryId,
            icon = input.icon
        )
    }
}
