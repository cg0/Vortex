package uk.cg0.vortex.auth

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class Argon2PasswordHandlerTests {
    @Test
    fun `Can we verify our hashed bcrypt string`() {
        val argon2 = Argon2PasswordHandler()
        val hashed = argon2.hash("this is a test".toByteArray())

        assertTrue(argon2.verify(hashed, "this is a test".toByteArray()))
    }

    @Test
    fun `Does our argon2 verify function fail when we pass a different string`() {
        val argon2 = Argon2PasswordHandler()
        val hashed = argon2.hash("this is a test".toByteArray())

        assertFalse(argon2.verify(hashed, "this is a different test".toByteArray()))
    }
}