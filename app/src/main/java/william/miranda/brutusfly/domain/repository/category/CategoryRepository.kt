package william.miranda.brutusfly.domain.repository.category

import william.miranda.brutusfly.domain.model.category.CategoryPage

interface CategoryRepository {

    /** Returns one page and its server-reported boundaries; callers decide when to request more. */
    suspend fun getCategories(
        page: Int,
        limit: Int
    ): CategoryPage
}
