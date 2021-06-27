package uk.cg0.vortex.auth

import org.mindrot.jbcrypt.BCrypt

class BcryptPasswordHandler {
    fun hash(password: ByteArray): String {
        return BCrypt.hashpw(String(password), BCrypt.gensalt())
    }

    fun verify(hashedPassword: String, password: ByteArray): Boolean {
        return BCrypt.checkpw(String(password), hashedPassword)
    }
}