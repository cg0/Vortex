package uk.cg0.vortex.auth

import de.mkammerer.argon2.Argon2Factory

class Argon2PasswordHandler {
    private val argon2 = Argon2Factory.create()

    fun hash(password: ByteArray): String {
        return argon2.hash(10, 65536, 1, password)
    }

    fun verify(hashedPassword: String, password: ByteArray): Boolean {
        return argon2.verify(hashedPassword, password)
    }
}