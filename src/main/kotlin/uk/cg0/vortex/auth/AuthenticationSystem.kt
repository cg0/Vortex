package uk.cg0.vortex.auth

import uk.cg0.vortex.database.DatabaseColumn
import uk.cg0.vortex.database.DatabaseRow

data class AuthenticationSystem(val userKeyField: DatabaseColumn<*>,
                                val passwordField: DatabaseColumn<String>,
                                val passwordHashTypeField: DatabaseColumn<String>,
                                val preferredPasswordHash: PasswordHashType) {
    private val table = userKeyField.table
    private val bcrypt = BcryptPasswordHandler()
    private val argon2 = Argon2PasswordHandler()
    private val insecure = arrayListOf(
        PasswordHashType.CLEARTEXT
    )

    fun checkPassword(userKey: Any, password: ByteArray): Boolean {
        return checkPassword(table.where(userKeyField, userKey).first(), password)
    }

    fun checkPassword(user: DatabaseRow, password: ByteArray): Boolean {
        val hashedPassword = user[passwordField] ?: return false
        val hashType = PasswordHashType.getByValue(user[passwordHashTypeField] ?: "")
        val verified = verifyPassword(hashedPassword, password, hashType)
        if (hashType in insecure && verified && preferredPasswordHash != hashType) {
            user[passwordField] = hashPassword(password)
            user[passwordHashTypeField] = preferredPasswordHash.toString()
            user.save()
        }
        return verified
    }

    fun verifyPassword(hashedPassword: String, password: ByteArray, passwordHash: PasswordHashType): Boolean {
        return when (passwordHash) {
            PasswordHashType.BCRYPT -> {
                bcrypt.verify(hashedPassword, password)
            }
            PasswordHashType.ARGON2 -> {
                argon2.verify(hashedPassword, password)
            }
            PasswordHashType.CLEARTEXT -> {
                // Grumble grumble
                hashedPassword == String(password)
            }
        }
    }

    fun hashPassword(password: ByteArray): String {
        return when (preferredPasswordHash) {
            PasswordHashType.BCRYPT -> {
                bcrypt.hash(password)
            }
            PasswordHashType.ARGON2 -> {
                argon2.hash(password)
            }
            else -> {
                throw NotImplementedError()
            }
        }
    }

    enum class PasswordHashType {
        BCRYPT,
        ARGON2,
        CLEARTEXT; // Only here for retrieving, can't store a password this way

        companion object {
            fun getByValue(value: String): PasswordHashType {
                for (hashType in values()) {
                    if (hashType.name == value) {
                        return hashType
                    }
                }

                return CLEARTEXT
            }
        }
    }
}