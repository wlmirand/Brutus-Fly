package william.miranda.brutusfly.data.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeystoreTokenCipherTest {
    @Test
    fun encryptsWithFreshIvAndDecryptsAcrossInstances() {
        val cipher = KeystoreTokenCipher()
        val token = "instrumentation-test-token"
        val first = cipher.encrypt(token)
        val second = cipher.encrypt(token)

        assertNotEquals(first, second)
        assertFalse(first.contains(token))
        assertEquals(token, KeystoreTokenCipher().decrypt(first))
        assertEquals(token, cipher.decrypt(second))
    }
}
