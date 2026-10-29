package william.miranda.brutusfly.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/** Encrypts tokens with AES-GCM using an AES key managed by Android Keystore. */
@Singleton
class KeystoreTokenCipher @Inject constructor() : TokenCipher {
    /**
     * Returns the key stored under the application's alias, creating it when absent.
     * Synchronization prevents concurrent callers on this instance from creating competing keys.
     * The key is used through Keystore rather than persisted alongside the token.
     */
    @Synchronized
    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        (store.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        ).apply {
            init(
                KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    /**
     * Encrypts the UTF-8 token with a fresh initialization vector (IV).
     * Returns Base64(IV):Base64(ciphertext and authentication tag), which is safe to store as text.
     * Keystore and cryptographic failures propagate to the storage layer.
     */
    override fun encrypt(
        token: String
    ): String {
        val cipher = Cipher.getInstance(
            "AES/GCM/NoPadding"
        )
        cipher.init(
            Cipher.ENCRYPT_MODE,
            key()
        )
        val encrypted = cipher.doFinal(
            token.toByteArray(
                charset = Charsets.UTF_8
            )
        )
        return "${encode(
            value = cipher.iv
        )}:${encode(
            value = encrypted
        )}"
    }

    /**
     * Splits the stored IV and encrypted payload, then verifies and decrypts the AES-GCM data.
     * Rejects malformed input, tampered ciphertext and data that cannot be read with the current key.
     * Returns the original UTF-8 token only after authentication succeeds.
     */
    override fun decrypt(
        encryptedToken: String
    ): String {
        val parts = encryptedToken.split(
            ':'
        )
        require(
            value = parts.size == 2
        ) { "Invalid encrypted token format" }
        val cipher = Cipher.getInstance(
            "AES/GCM/NoPadding"
        )
        cipher.init(
            Cipher.DECRYPT_MODE,
            key(),
            GCMParameterSpec(
                128,
                decode(
                    value = parts[0]
                )
            )
        )
        return cipher.doFinal(
            decode(value = parts[1])
        ).toString(charset = Charsets.UTF_8)
    }

    /** Encodes binary cryptographic data as Base64 without inserting line breaks. */
    private fun encode(
        value: ByteArray
    ): String = Base64.encodeToString(
        value,
        Base64.NO_WRAP
    )

    /** Decodes one Base64 component of the stored token representation back into bytes. */
    private fun decode(
        value: String
    ): ByteArray = Base64.decode(
        value,
        Base64.NO_WRAP
    )

    private companion object {
        const val KEY_ALIAS = "brutus_fly_personal_access_token"
    }
}
