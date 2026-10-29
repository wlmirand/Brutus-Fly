package william.miranda.brutusfly.domain.model.category

/** User-selectable category icons. Keys are persistent identifiers, independent of enum names/order. */
enum class CategoryIcon(val key: String) {
    LABEL("label"),
    RESTAURANT("restaurant"),
    CART("cart"),
    COFFEE("coffee"),
    DELIVERY("delivery"),
    HOME("home"),
    RENT("rent"),
    MAINTENANCE("maintenance"),
    FURNITURE("furniture"),
    CLEANING("cleaning"),
    CAR("car"),
    BUS("bus"),
    FUEL("fuel"),
    PARKING("parking"),
    BICYCLE("bicycle"),
    HEALTH("health"),
    PHARMACY("pharmacy"),
    DENTIST("dentist"),
    FITNESS("fitness"),
    EDUCATION("education"),
    BOOKS("books"),
    COURSES("courses"),
    SHOPPING("shopping"),
    CLOTHING("clothing"),
    ELECTRONICS("electronics"),
    GIFTS("gifts"),
    GAME("game"),
    CINEMA("cinema"),
    MUSIC("music"),
    TRAVEL("travel"),
    SPORTS("sports"),
    SERVICES("services"),
    INTERNET("internet"),
    PHONE("phone"),
    ELECTRICITY("electricity"),
    WATER("water"),
    SUBSCRIPTIONS("subscriptions"),
    MONEY("money"),
    BANKNOTES("banknotes"),
    TRANSFERS("transfers"),
    BANK("bank"),
    CARD("card"),
    INVESTMENTS("investments"),
    FAMILY("family"),
    PETS("pets"),
    WORK("work"),
    DONATIONS("donations");

    companion object {
        /** Missing or unsupported saved choices use the neutral icon. */
        fun fromKey(key: String?): CategoryIcon =
            entries.firstOrNull { it.key == key } ?: LABEL
    }
}
