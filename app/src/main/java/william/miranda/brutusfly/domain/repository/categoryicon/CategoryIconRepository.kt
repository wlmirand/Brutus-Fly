package william.miranda.brutusfly.domain.repository.categoryicon

import kotlinx.coroutines.flow.Flow
import william.miranda.brutusfly.domain.model.category.CategoryIcon

interface CategoryIconRepository {
    /** Emits user choices by category ID. Missing categories use the neutral icon in the UI. */
    val icons: Flow<Map<String, CategoryIcon>>

    suspend fun assign(categoryId: String, icon: CategoryIcon)

    /** Restores the default by removing the explicit choice. */
    suspend fun remove(categoryId: String)
}
