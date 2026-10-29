package william.miranda.brutusfly.domain.usecase.categoryicon

import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.repository.categoryicon.CategoryIconRepository
import javax.inject.Inject

class RemoveCategoryIconUseCase @Inject constructor(
    private val repository: CategoryIconRepository
) : SuspendUseCase<RemoveCategoryIconUseCase.Input, Unit> {
    data class Input(
        val categoryId: String
    )

    override suspend fun invoke(
        input: Input
    ) {
        require(input.categoryId.isNotBlank()) { "Category ID must not be blank" }
        repository.remove(
            categoryId = input.categoryId
        )
    }
}
