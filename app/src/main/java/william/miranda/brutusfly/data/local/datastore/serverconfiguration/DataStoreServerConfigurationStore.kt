package william.miranda.brutusfly.data.local.datastore.serverconfiguration

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import william.miranda.brutusfly.data.local.datastore.ServerPreferenceKeys.BASE_URL
import william.miranda.brutusfly.data.local.datastore.ServerPreferenceKeys.ENCRYPTED_TOKEN
import william.miranda.brutusfly.data.security.TokenCipher
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationException
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationFailure
import william.miranda.brutusfly.domain.model.serverconfiguration.ServerConfiguration
import javax.inject.Inject
import javax.inject.Singleton

/** Stores the URL as a preference and the token as ciphertext, never as a plain-text preference. */
@Singleton
class DataStoreServerConfigurationStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cipher: TokenCipher
) : ServerConfigurationStore {
    /**
     * Reads each preferences snapshot on the IO dispatcher and decrypts the token in memory.
     * Emits null only when both fields are absent. Incomplete data, decryption failures and
     * read failures become STORAGE errors; coroutine cancellation is allowed to propagate.
     */
    override val configuration: Flow<ServerConfiguration?> = dataStore.data
        .map { preferences ->
            val url = preferences[BASE_URL]
            val token = preferences[ENCRYPTED_TOKEN]
            if (url == null && token == null) {
                null
            } else {
                check(!url.isNullOrBlank() && !token.isNullOrBlank()) { "Incomplete server configuration" }
                ServerConfiguration(
                    baseUrl = url,
                    personalAccessToken = cipher.decrypt(
                        encryptedToken = token
                    )
                )
            }
        }
        .catch { cause ->
            if (cause is CancellationException) throw cause
            throw ConfigurationException(
                failure = ConfigurationFailure.STORAGE,
                cause = cause
            )
        }
        .flowOn(context = Dispatchers.IO)

    /**
     * Encrypts the token before writing, then atomically replaces both preferences.
     * Clears connection-scoped preferences when the URL or token changes. Identical credentials
     * preserve local choices; token rotation is conservatively treated as a connection change.
     * Encryption or previous-token decryption failures leave the preferences untouched.
     */
    override suspend fun save(
        configuration: ServerConfiguration
    ) = storageOperation {
        val encryptedToken = cipher.encrypt(
            token = configuration.personalAccessToken
        )
        dataStore.edit { preferences ->
            val previousToken = preferences[ENCRYPTED_TOKEN]
            val connectionChanged = preferences[BASE_URL] != configuration.baseUrl ||
                previousToken == null ||
                cipher.decrypt(encryptedToken = previousToken) != configuration.personalAccessToken
            if (connectionChanged) {
                preferences.clear()
            }
            preferences[BASE_URL] = configuration.baseUrl
            preferences[ENCRYPTED_TOKEN] = encryptedToken
        }
        Unit
    }

    /** Clears connection settings and all local preferences without deleting the Keystore encryption key. */
    override suspend fun clear() = storageOperation {
        dataStore.edit { it.clear() }
        Unit
    }

    /**
     * Runs a storage operation on the IO dispatcher and returns its result.
     * Wraps non-cancellation exceptions as domain-level STORAGE failures.
     */
    private suspend fun <T> storageOperation(
        block: suspend () -> T
    ): T = withContext(
        context = Dispatchers.IO
    ) {
        try {
            block()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (exception: Exception) {
            throw ConfigurationException(
                failure = ConfigurationFailure.STORAGE,
                cause = exception
            )
        }
    }
}
