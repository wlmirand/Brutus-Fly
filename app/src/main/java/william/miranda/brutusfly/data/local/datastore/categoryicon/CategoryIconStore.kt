package william.miranda.brutusfly.data.local.datastore.categoryicon

import kotlinx.coroutines.flow.Flow

/** Stores category IDs and stable icon keys for the current connection. */
interface CategoryIconStore {
    /** Emits saved assignments; read failures propagate rather than appearing as empty preferences. */
    val icons: Flow<Map<String, String>>

    suspend fun save(categoryId: String, iconKey: String)

    suspend fun remove(categoryId: String)
}
