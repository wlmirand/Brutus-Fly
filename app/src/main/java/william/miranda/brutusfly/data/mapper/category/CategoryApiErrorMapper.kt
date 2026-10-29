package william.miranda.brutusfly.data.mapper.category

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.core.network.model.ApiError
import william.miranda.brutusfly.domain.model.category.CategoryFailure
import william.miranda.brutusfly.domain.model.category.CategoryLoadException
import javax.inject.Inject

/** Keeps transport error types and HTTP status codes outside the category domain contract. */
class CategoryApiErrorMapper @Inject constructor() : Mapper<ApiError, CategoryLoadException> {
    override fun map(
        input: ApiError
    ): CategoryLoadException = when (input) {
        is ApiError.Http -> CategoryLoadException(
            failure = when (input.responseCode) {
                401, 403 -> CategoryFailure.UNAUTHORIZED
                in 500..599 -> CategoryFailure.SERVER
                else -> CategoryFailure.UNKNOWN
            }
        )
        is ApiError.Network -> CategoryLoadException(
            failure = CategoryFailure.NETWORK,
            cause = input.cause
        )
        is ApiError.Serialization -> CategoryLoadException(
            failure = CategoryFailure.UNKNOWN,
            cause = input.cause
        )
    }
}
