package william.miranda.brutusfly.domain.model.category

data class CategoryPage(
    val categories: List<Category>,
    val currentPage: Int,
    val totalPages: Int
) {
    val nextPage: Int?
        get() = if (currentPage < totalPages) currentPage + 1 else null
}
