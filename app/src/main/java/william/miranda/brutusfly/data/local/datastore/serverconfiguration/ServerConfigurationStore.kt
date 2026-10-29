package william.miranda.brutusfly.data.local.datastore.serverconfiguration

import kotlinx.coroutines.flow.Flow
import william.miranda.brutusfly.domain.model.serverconfiguration.ServerConfiguration

/** Persistence contract for the server URL and the token needed to authenticate requests. */
interface ServerConfigurationStore {
    /** Emits the current configuration, or null when none is saved; storage failures propagate. */
    val configuration: Flow<ServerConfiguration?>

    /** Replaces the saved URL and token together; implementations must protect the stored token. */
    suspend fun save(configuration: ServerConfiguration)

    /** Removes the saved configuration so the user can connect to a server again. */
    suspend fun clear()
}
