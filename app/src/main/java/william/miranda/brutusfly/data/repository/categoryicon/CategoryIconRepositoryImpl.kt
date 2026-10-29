package william.miranda.brutusfly.data.repository.categoryicon

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import william.miranda.brutusfly.data.local.datastore.categoryicon.CategoryIconStore
import william.miranda.brutusfly.data.mapper.categoryicon.CategoryIconPreferencesMapper
import william.miranda.brutusfly.domain.model.category.CategoryIcon
import william.miranda.brutusfly.domain.repository.categoryicon.CategoryIconRepository
import javax.inject.Inject

class CategoryIconRepositoryImpl @Inject constructor(
    private val store: CategoryIconStore,
    private val mapper: CategoryIconPreferencesMapper
) : CategoryIconRepository {
    override val icons = store.icons
        .map { assignments -> mapper.map(input = assignments) }
        .distinctUntilChanged()

    override suspend fun assign(
        categoryId: String,
        icon: CategoryIcon
    ) = store.save(
        categoryId = categoryId,
        iconKey = icon.key
    )

    override suspend fun remove(
        categoryId: String
    ) = store.remove(
        categoryId = categoryId
    )
}
