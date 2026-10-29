package william.miranda.brutusfly.ui.categorylist.model

import william.miranda.brutusfly.domain.model.category.CategoryIcon

internal data class CategoryUiModel(
    val id: String,
    val name: String,
    val notes: String? = null,
    val icon: CategoryIcon = CategoryIcon.LABEL
)

// Preview-only examples. Runtime categories are loaded through CategoryRepository.
internal val sampleCategories = listOf(
    CategoryUiModel(
        id = "food", name = "Food", notes = "Restaurants, coffee shops and delivery",
        icon = CategoryIcon.RESTAURANT
    ),
    CategoryUiModel(
        id = "education", name = "Education", notes = "Courses, books and study materials",
        icon = CategoryIcon.EDUCATION
    ),
    CategoryUiModel(id = "leisure", name = "Leisure", icon = CategoryIcon.GAME),
    CategoryUiModel(
        id = "home", name = "Housing", notes = "Rent and household expenses",
        icon = CategoryIcon.HOME
    ),
    CategoryUiModel(id = "gifts", name = "Gifts", icon = CategoryIcon.GIFTS),
    CategoryUiModel(id = "salary", name = "Salary"),
    CategoryUiModel(
        id = "health", name = "Health", notes = "Appointments, tests and medication",
        icon = CategoryIcon.HEALTH
    ),
    CategoryUiModel(id = "groceries", name = "Groceries", icon = CategoryIcon.CART),
    CategoryUiModel(
        id = "transport", name = "Transport", notes = "Fuel, public transport and ride-hailing",
        icon = CategoryIcon.CAR
    )
)
