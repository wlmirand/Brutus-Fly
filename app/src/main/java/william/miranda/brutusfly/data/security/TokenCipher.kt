package william.miranda.brutusfly.data.security

/** Separates token encryption from persistence so the storage layer does not manage cryptographic keys. */
interface TokenCipher {
    /** Converts a plain-text token into an encrypted representation suitable for storage. */
    fun encrypt(
        token: String
    ): String

    /** Restores the token from its encrypted representation, failing if it cannot be decrypted. */
    fun decrypt(
        encryptedToken: String
    ): String
}
