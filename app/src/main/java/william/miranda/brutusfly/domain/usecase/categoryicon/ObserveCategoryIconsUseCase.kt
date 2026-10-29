package william.miranda.brutusfly.domain.usecase.categoryicon

import kotlinx.coroutines.flow.Flow
import william.miranda.brutusfly.core.common.usecase.UseCase
import william.miranda.brutusfly.domain.model.category.CategoryIcon
import william.miranda.brutusfly.domain.repository.categoryicon.CategoryIconRepository
import javax.inject.Inject

class ObserveCategoryIconsUseCase @Inject constructor(
    private val repository: CategoryIconRepository
) : UseCase<Unit, Flow<Map<String, CategoryIcon>>> {
    override fun invoke(
        input: Unit
    ): Flow<Map<String, CategoryIcon>> = repository.icons
}
