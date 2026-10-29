package william.miranda.brutusfly.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import william.miranda.brutusfly.data.security.TokenCipher
import william.miranda.brutusfly.domain.model.ServerConfiguration
import java.io.File

class DataStoreServerConfigurationStoreTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun savesEncryptedTokenAndClearsBothFields() = runBlocking {
        val job = SupervisorJob()
        try {
            val dataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO + job),
                produceFile = { File(temporaryFolder.root, "server.preferences_pb") },
            )
            val cipher = object : TokenCipher {
                override fun encrypt(token: String): String {
                    assertEquals("secret-token", token)
                    return "encrypted-value"
                }
                override fun decrypt(encryptedToken: String): String {
                    assertEquals("encrypted-value", encryptedToken)
                    return "secret-token"
                }
            }
            val store = DataStoreServerConfigurationStore(dataStore, cipher)
            assertNull(store.configuration.first())

            store.save(ServerConfiguration("https://example.com/", "secret-token"))

            val preferences = dataStore.data.first()
            assertFalse(preferences.asMap().values.any { it.toString().contains("secret-token") })
            val saved = store.configuration.first()
            assertEquals("https://example.com/", saved?.baseUrl)
            assertEquals("secret-token", saved?.personalAccessToken)

            store.clear()

            assertNull(store.configuration.first())
        } finally {
            job.cancelAndJoin()
        }
    }
}
