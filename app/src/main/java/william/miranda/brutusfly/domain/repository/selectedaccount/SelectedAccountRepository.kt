package william.miranda.brutusfly.domain.repository.selectedaccount

import kotlinx.coroutines.flow.Flow

interface SelectedAccountRepository {
    /** Emits the selected account ID, or null when there is no selection. */
    val selectedAccountId: Flow<String?>

    /** Replaces the selection with a nonblank account ID. */
    suspend fun select(accountId: String)

    /** Selects the supplied account only if no selection exists, without overwriting concurrent choices. */
    suspend fun initialize(accountId: String)

    /** Removes the selection without changing the server configuration. */
    suspend fun clear()
}
