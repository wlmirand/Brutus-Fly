package william.miranda.brutusfly.data.local.datastore.selectedaccount

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import william.miranda.brutusfly.data.local.datastore.ServerPreferenceKeys.SELECTED_ACCOUNT_ID
import javax.inject.Inject
import javax.inject.Singleton

/** Shares the configuration DataStore so connection changes can atomically invalidate the selection. */
@Singleton
class DataStoreSelectedAccountStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SelectedAccountStore {
    /** Observes selection changes without emitting again for unrelated preference updates. */
    override val selectedAccountId: Flow<String?> = dataStore.data
        .map { it[SELECTED_ACCOUNT_ID] }
        .distinctUntilChanged()

    /** Persists the supplied ID exactly; blank IDs are rejected and storage failures propagate. */
    override suspend fun save(accountId: String) {
        require(accountId.isNotBlank()) { "Account ID must not be blank" }
        dataStore.edit { it[SELECTED_ACCOUNT_ID] = accountId }
    }

    /** Checks and initializes within the same transaction so an existing selection is never overwritten. */
    override suspend fun saveIfAbsent(accountId: String) {
        require(accountId.isNotBlank()) { "Account ID must not be blank" }
        dataStore.edit { preferences ->
            if (preferences[SELECTED_ACCOUNT_ID] == null) {
                preferences[SELECTED_ACCOUNT_ID] = accountId
            }
        }
    }

    /** Clears only the account ID, preserving the URL and encrypted token. */
    override suspend fun clear() {
        dataStore.edit {
            it.remove(
                key = SELECTED_ACCOUNT_ID
            )
        }
    }
}
