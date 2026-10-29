package william.miranda.brutusfly.ui.common.category

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import william.miranda.brutusfly.domain.model.category.CategoryIcon

class CategoryIconCatalogTest {
    @Test
    fun catalogIncludesEveryIconExactlyOnce() {
        val icons = CategoryIconCatalog.groups.flatMap { group -> group.options.map { it.icon } }

        assertEquals(CategoryIcon.entries.toSet(), icons.toSet())
        assertEquals(icons.size, icons.toSet().size)
    }

    @Test
    fun groupsAndOptionsHaveLabels() {
        assertTrue(CategoryIconCatalog.groups.isNotEmpty())
        CategoryIconCatalog.groups.forEach { group ->
            assertTrue(group.labelRes != 0)
            assertTrue(group.options.isNotEmpty())
            assertTrue(group.options.all { it.labelRes != 0 })
        }
    }

    @Test
    fun everyIconHasAVisual() {
        CategoryIcon.entries.forEach { icon ->
            assertTrue(icon.imageVector().name.isNotBlank())
        }
    }
}
