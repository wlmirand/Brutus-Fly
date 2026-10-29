package william.miranda.brutusfly.ui.common.category

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DeliveryDining
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MiscellaneousServices
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector
import william.miranda.brutusfly.domain.model.category.CategoryIcon

/** Shared shapes; decorative tint and background are resolved separately by CategoryIcon.colors. */
internal fun CategoryIcon.imageVector(): ImageVector = when (this) {
    CategoryIcon.LABEL -> Icons.Outlined.Label
    CategoryIcon.RESTAURANT -> Icons.Outlined.Restaurant
    CategoryIcon.CART -> Icons.Outlined.ShoppingCart
    CategoryIcon.COFFEE -> Icons.Outlined.LocalCafe
    CategoryIcon.DELIVERY -> Icons.Outlined.DeliveryDining
    CategoryIcon.HOME -> Icons.Outlined.Home
    CategoryIcon.RENT -> Icons.Outlined.Key
    CategoryIcon.MAINTENANCE -> Icons.Outlined.Build
    CategoryIcon.FURNITURE -> Icons.Outlined.Weekend
    CategoryIcon.CLEANING -> Icons.Outlined.CleaningServices
    CategoryIcon.CAR -> Icons.Outlined.DirectionsCar
    CategoryIcon.BUS -> Icons.Outlined.DirectionsBus
    CategoryIcon.FUEL -> Icons.Outlined.LocalGasStation
    CategoryIcon.PARKING -> Icons.Outlined.LocalParking
    CategoryIcon.BICYCLE -> Icons.Outlined.DirectionsBike
    CategoryIcon.HEALTH -> Icons.Outlined.LocalHospital
    CategoryIcon.PHARMACY -> Icons.Outlined.LocalPharmacy
    CategoryIcon.DENTIST -> Icons.Outlined.HealthAndSafety
    CategoryIcon.FITNESS -> Icons.Outlined.FitnessCenter
    CategoryIcon.EDUCATION -> Icons.Outlined.School
    CategoryIcon.BOOKS -> Icons.Outlined.Book
    CategoryIcon.COURSES -> Icons.Outlined.MenuBook
    CategoryIcon.SHOPPING -> Icons.Outlined.ShoppingBag
    CategoryIcon.CLOTHING -> Icons.Outlined.Checkroom
    CategoryIcon.ELECTRONICS -> Icons.Outlined.Devices
    CategoryIcon.GIFTS -> Icons.Outlined.CardGiftcard
    CategoryIcon.GAME -> Icons.Outlined.SportsEsports
    CategoryIcon.CINEMA -> Icons.Outlined.Movie
    CategoryIcon.MUSIC -> Icons.Outlined.MusicNote
    CategoryIcon.TRAVEL -> Icons.Outlined.Flight
    CategoryIcon.SPORTS -> Icons.Outlined.SportsSoccer
    CategoryIcon.SERVICES -> Icons.Outlined.MiscellaneousServices
    CategoryIcon.INTERNET -> Icons.Outlined.Wifi
    CategoryIcon.PHONE -> Icons.Outlined.PhoneAndroid
    CategoryIcon.ELECTRICITY -> Icons.Outlined.Bolt
    CategoryIcon.WATER -> Icons.Outlined.WaterDrop
    CategoryIcon.SUBSCRIPTIONS -> Icons.Outlined.Subscriptions
    CategoryIcon.MONEY -> Icons.Outlined.AttachMoney
    CategoryIcon.BANKNOTES -> Icons.Outlined.Payments
    CategoryIcon.TRANSFERS -> Icons.Outlined.SwapHoriz
    CategoryIcon.BANK -> Icons.Outlined.AccountBalance
    CategoryIcon.CARD -> Icons.Outlined.CreditCard
    CategoryIcon.INVESTMENTS -> Icons.Outlined.TrendingUp
    CategoryIcon.FAMILY -> Icons.Outlined.FamilyRestroom
    CategoryIcon.PETS -> Icons.Outlined.Pets
    CategoryIcon.WORK -> Icons.Outlined.Work
    CategoryIcon.DONATIONS -> Icons.Outlined.VolunteerActivism
}
