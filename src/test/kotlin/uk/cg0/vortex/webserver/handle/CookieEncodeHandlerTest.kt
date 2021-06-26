package uk.cg0.vortex.webserver.handle

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class CookieEncodeHandlerTest {
    @Test
    fun `Can we parse a cookie string with one element`() {
        val cookie = "foo=bar"
        val parsed = CookieEncodeHandler().handleRead(cookie)

        assertEquals(1, parsed.size)
        assertTrue(parsed.keys.contains("foo"))
        assertEquals("bar", parsed["foo"])
    }

    @Test
    fun `Can we parse a cookie string with two elements`() {
        val cookie = "foo=bar; bar=baz"
        val parsed = CookieEncodeHandler().handleRead(cookie)

        assertEquals(2, parsed.size)
        assertTrue(parsed.keys.contains("foo"))
        assertEquals("bar", parsed["foo"])


        assertTrue(parsed.keys.contains("bar"))
        assertEquals("baz", parsed["bar"])
    }

    @Test
    fun `Can we parse an empty cookie string`() {
        val cookie = ""
        val parsed = CookieEncodeHandler().handleRead(cookie)

        assertEquals(0, parsed.size)
    }
}