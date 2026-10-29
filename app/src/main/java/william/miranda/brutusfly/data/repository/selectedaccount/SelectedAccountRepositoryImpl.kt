package william.miranda.brutusfly.data.repository.selectedaccount

import william.miranda.brutusfly.data.local.datastore.selectedaccount.SelectedAccountStore
import william.miranda.brutusfly.domain.repository.selectedaccount.SelectedAccountRepository
import javax.inject.Inject

/** Provides selected-account persistence without exposing DataStore to domain or presentation code. */
class SelectedAccountRepositoryImpl @Inject constructor(
    private val store: SelectedAccountStore
) : SelectedAccountRepository {
    /** Observes the stored ID; storage failures propagate without being replaced by an empty selection. */
    override val selectedAccountId = store.selectedAccountId

    /** Persists an explicit account selection, propagating validation and storage failures. */
    override suspend fun select(
        accountId: String
    ) = store.save(
        accountId = accountId
    )

    /** Delegates conditional initialization to an atomic storage operation. */
    override suspend fun initialize(
        accountId: String
    ) = store.saveIfAbsent(
        accountId = accountId
    )

    /** Removes the selected ID while preserving the connection settings. */
    override suspend fun clear() = store.clear()
}
