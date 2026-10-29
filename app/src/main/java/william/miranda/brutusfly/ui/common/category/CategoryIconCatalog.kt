package william.miranda.brutusfly.ui.common.category

import androidx.annotation.StringRes
import william.miranda.brutusfly.R
import william.miranda.brutusfly.domain.model.category.CategoryIcon

internal data class CategoryIconOption(
    val icon: CategoryIcon,
    @param:StringRes val labelRes: Int
)

/** Presentation groups help browsing; they do not restrict user-defined categories. */
internal data class CategoryIconGroup(
    @param:StringRes val labelRes: Int,
    val tone: CategoryIconTone,
    val options: List<CategoryIconOption>
)

internal object CategoryIconCatalog {
    val groups: List<CategoryIconGroup> = listOf(
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_food,
            tone = CategoryIconTone.ORANGE,
            options = listOf(
                CategoryIconOption(CategoryIcon.RESTAURANT, R.string.category_icon_restaurant),
                CategoryIconOption(CategoryIcon.CART, R.string.category_icon_cart),
                CategoryIconOption(CategoryIcon.COFFEE, R.string.category_icon_coffee),
                CategoryIconOption(CategoryIcon.DELIVERY, R.string.category_icon_delivery)
            )
        ),
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_home,
            tone = CategoryIconTone.PURPLE,
            options = listOf(
                CategoryIconOption(CategoryIcon.HOME, R.string.category_icon_home),
                CategoryIconOption(CategoryIcon.RENT, R.string.category_icon_rent),
                CategoryIconOption(CategoryIcon.MAINTENANCE, R.string.category_icon_maintenance),
                CategoryIconOption(CategoryIcon.FURNITURE, R.string.category_icon_furniture),
                CategoryIconOption(CategoryIcon.CLEANING, R.string.category_icon_cleaning)
            )
        ),
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_transport,
            tone = CategoryIconTone.BLUE,
            options = listOf(
                CategoryIconOption(CategoryIcon.CAR, R.string.category_icon_car),
                CategoryIconOption(CategoryIcon.BUS, R.string.category_icon_bus),
                CategoryIconOption(CategoryIcon.FUEL, R.string.category_icon_fuel),
                CategoryIconOption(CategoryIcon.PARKING, R.string.category_icon_parking),
                CategoryIconOption(CategoryIcon.BICYCLE, R.string.category_icon_bicycle)
            )
        ),
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_health,
            tone = CategoryIconTone.TEAL,
            options = listOf(
                CategoryIconOption(CategoryIcon.HEALTH, R.string.category_icon_health),
                CategoryIconOption(CategoryIcon.PHARMACY, R.string.category_icon_pharmacy),
                CategoryIconOption(CategoryIcon.DENTIST, R.string.category_icon_dentist),
                CategoryIconOption(CategoryIcon.FITNESS, R.string.category_icon_fitness)
            )
        ),
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_education,
            tone = CategoryIconTone.AMBER,
            options = listOf(
                CategoryIconOption(CategoryIcon.EDUCATION, R.string.category_icon_education),
                CategoryIconOption(CategoryIcon.BOOKS, R.string.category_icon_books),
                CategoryIconOption(CategoryIcon.COURSES, R.string.category_icon_courses)
            )
        ),
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_shopping,
            tone = CategoryIconTone.ROSE,
            options = listOf(
                CategoryIconOption(CategoryIcon.SHOPPING, R.string.category_icon_shopping),
                CategoryIconOption(CategoryIcon.CLOTHING, R.string.category_icon_clothing),
                CategoryIconOption(CategoryIcon.ELECTRONICS, R.string.category_icon_electronics),
                CategoryIconOption(CategoryIcon.GIFTS, R.string.category_icon_gifts)
            )
        ),
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_leisure,
            tone = CategoryIconTone.PURPLE,
            options = listOf(
                CategoryIconOption(CategoryIcon.GAME, R.string.category_icon_game),
                CategoryIconOption(CategoryIcon.CINEMA, R.string.category_icon_cinema),
                CategoryIconOption(CategoryIcon.MUSIC, R.string.category_icon_music),
                CategoryIconOption(CategoryIcon.TRAVEL, R.string.category_icon_travel),
                CategoryIconOption(CategoryIcon.SPORTS, R.string.category_icon_sports)
            )
        ),
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_services,
            tone = CategoryIconTone.BLUE,
            options = listOf(
                CategoryIconOption(CategoryIcon.SERVICES, R.string.category_icon_services),
                CategoryIconOption(CategoryIcon.INTERNET, R.string.category_icon_internet),
                CategoryIconOption(CategoryIcon.PHONE, R.string.category_icon_phone),
                CategoryIconOption(CategoryIcon.ELECTRICITY, R.string.category_icon_electricity),
                CategoryIconOption(CategoryIcon.WATER, R.string.category_icon_water),
                CategoryIconOption(CategoryIcon.SUBSCRIPTIONS, R.string.category_icon_subscriptions)
            )
        ),
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_finance,
            tone = CategoryIconTone.TEAL,
            options = listOf(
                CategoryIconOption(CategoryIcon.MONEY, R.string.category_icon_money),
                CategoryIconOption(CategoryIcon.BANKNOTES, R.string.category_icon_banknotes),
                CategoryIconOption(CategoryIcon.TRANSFERS, R.string.category_icon_transfers),
                CategoryIconOption(CategoryIcon.BANK, R.string.category_icon_bank),
                CategoryIconOption(CategoryIcon.CARD, R.string.category_icon_card),
                CategoryIconOption(CategoryIcon.INVESTMENTS, R.string.category_icon_investments)
            )
        ),
        CategoryIconGroup(
            labelRes = R.string.category_icon_group_other,
            tone = CategoryIconTone.NEUTRAL,
            options = listOf(
                CategoryIconOption(CategoryIcon.FAMILY, R.string.category_icon_family),
                CategoryIconOption(CategoryIcon.PETS, R.string.category_icon_pets),
                CategoryIconOption(CategoryIcon.WORK, R.string.category_icon_work),
                CategoryIconOption(CategoryIcon.DONATIONS, R.string.category_icon_donations),
                CategoryIconOption(CategoryIcon.LABEL, R.string.category_icon_label)
            )
        )
    )

    private val tonesByIcon: Map<CategoryIcon, CategoryIconTone> = groups
        .flatMap { group -> group.options.map { it.icon to group.tone } }
        .toMap()

    fun toneFor(icon: CategoryIcon): CategoryIconTone = tonesByIcon.getValue(key = icon)
}
