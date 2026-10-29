package william.miranda.brutusfly.data.local.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

/** Shared keys for connection settings and local preferences scoped to that connection. */
internal object ServerPreferenceKeys {
    val BASE_URL = stringPreferencesKey(name = "base_url")
    val ENCRYPTED_TOKEN = stringPreferencesKey(name = "encrypted_personal_access_token")
    val SELECTED_ACCOUNT_ID = stringPreferencesKey(name = "selected_account_id")
    val CATEGORY_ICONS = stringPreferencesKey(name = "category_icons")
}
