package william.miranda.brutusfly.domain.model.category

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryIconTest {
    @Test
    fun keysAreUniqueAndNonBlank() {
        val keys = CategoryIcon.entries.map { it.key }

        assertEquals(keys.size, keys.toSet().size)
        assertTrue(keys.all { it.isNotBlank() })
    }

    @Test
    fun everyIconCanBeResolvedFromItsPersistentKey() {
        CategoryIcon.entries.forEach { icon ->
            assertEquals(icon, CategoryIcon.fromKey(icon.key))
        }
    }

    @Test
    fun missingOrUnknownKeysUseNeutralIcon() {
        listOf(null, "", "unknown", "RESTAURANT").forEach { key ->
            assertEquals(CategoryIcon.LABEL, CategoryIcon.fromKey(key))
        }
    }

    @Test
    fun resolvesStableKeys() {
        assertEquals(CategoryIcon.LABEL, CategoryIcon.fromKey("label"))
        assertEquals(CategoryIcon.RESTAURANT, CategoryIcon.fromKey("restaurant"))
        assertEquals(CategoryIcon.CAR, CategoryIcon.fromKey("car"))
    }
}
