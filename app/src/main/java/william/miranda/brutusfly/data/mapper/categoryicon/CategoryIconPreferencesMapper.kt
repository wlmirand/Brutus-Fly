package william.miranda.brutusfly.data.mapper.categoryicon

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.domain.model.category.CategoryIcon
import javax.inject.Inject

/** Resolves persistent keys without exposing their storage format to presentation code. */
class CategoryIconPreferencesMapper @Inject constructor() :
    Mapper<Map<String, String>, Map<String, CategoryIcon>> {
    override fun map(
        input: Map<String, String>
    ): Map<String, CategoryIcon> = input.mapValues { (_, key) ->
        CategoryIcon.fromKey(key = key)
    }
}
