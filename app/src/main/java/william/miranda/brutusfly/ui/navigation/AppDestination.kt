package william.miranda.brutusfly.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface AppDestination : NavKey {
    @Serializable
    data object Summary : AppDestination

    @Serializable
    data object Accounts : AppDestination

    @Serializable
    data object Transactions : AppDestination

    @Serializable
    data object Categories : AppDestination
}
