package william.miranda.brutusfly.domain.model.transaction

/** A page of domain groups and the pagination boundaries reported by the server. */
data class TransactionPage(
    val groups: List<TransactionGroup>,
    val currentPage: Int,
    val totalPages: Int
) {
    val nextPage: Int?
        get() = if (currentPage < totalPages) currentPage + 1 else null
}
