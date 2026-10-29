package william.miranda.brutusfly.data.local.datastore.selectedaccount

import kotlinx.coroutines.flow.Flow

/** Persists only the account ID selected for the current server, not an account snapshot. */
interface SelectedAccountStore {
    /** Emits the selected ID, or null when no account is selected; storage failures propagate. */
    val selectedAccountId: Flow<String?>

    /** Saves a nonblank account ID as the current selection. */
    suspend fun save(accountId: String)

    /** Atomically saves a nonblank account ID only when no selection exists. */
    suspend fun saveIfAbsent(accountId: String)

    /** Removes the selection without changing the saved connection settings. */
    suspend fun clear()
}
