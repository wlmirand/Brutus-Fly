package william.miranda.brutusfly.data.local.datastore.categoryicon

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import william.miranda.brutusfly.data.local.datastore.ServerPreferenceKeys.CATEGORY_ICONS
import javax.inject.Inject
import javax.inject.Singleton

/** Shares the configuration DataStore so resetting the connection also removes icon assignments. */
@Singleton
class DataStoreCategoryIconStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : CategoryIconStore {
    override val icons: Flow<Map<String, String>> = dataStore.data
        .map { preferences -> readIcons(preferences = preferences) }
        .distinctUntilChanged()

    /** Reads and updates within one transaction to preserve concurrent assignments. */
    override suspend fun save(
        categoryId: String,
        iconKey: String
    ) {
        require(categoryId.isNotBlank()) { "Category ID must not be blank" }
        require(iconKey.isNotBlank()) { "Icon key must not be blank" }
        dataStore.edit { preferences ->
            val icons = readIcons(preferences = preferences) + (categoryId to iconKey)
            preferences[CATEGORY_ICONS] = Json.encodeToString(value = icons)
        }
    }

    /** Removes only the supplied assignment, preserving all unrelated preferences. */
    override suspend fun remove(
        categoryId: String
    ) {
        require(categoryId.isNotBlank()) { "Category ID must not be blank" }
        dataStore.edit { preferences ->
            val icons = readIcons(preferences = preferences) - categoryId
            if (icons.isEmpty()) {
                preferences.remove(key = CATEGORY_ICONS)
            } else {
                preferences[CATEGORY_ICONS] = Json.encodeToString(value = icons)
            }
        }
    }

    /** Missing preferences are empty; malformed JSON and storage failures are not silently discarded. */
    private fun readIcons(
        preferences: Preferences
    ): Map<String, String> {
        val saved = preferences[CATEGORY_ICONS] ?: return emptyMap()
        return Json.decodeFromString<Map<String, String>>(string = saved)
    }
}
