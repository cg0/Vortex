package uk.cg0.vortex.auth

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class BcryptPasswordHandlerTests {
    @Test
    fun `Can we verify our hashed bcrypt string`() {
        val bcrypt = BcryptPasswordHandler()
        val hashed = bcrypt.hash("this is a test".toByteArray())

        assertTrue(bcrypt.verify(hashed, "this is a test".toByteArray()))
    }

    @Test
    fun `Does our bcrypt verify function fail when we pass a different string`() {
        val bcrypt = BcryptPasswordHandler()
        val hashed = bcrypt.hash("this is a test".toByteArray())

        assertFalse(bcrypt.verify(hashed, "this is a different test".toByteArray()))
    }
}