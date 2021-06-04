package uk.cg0.vortex.handler

import junit.framework.TestCase.*
import org.junit.Test

class KeyValueConfigTest {
    @Test
    fun `Can it parse a basic key value file`() {
        val read = KeyValueConfigHandler().handleRead(
            mutableListOf(
                "Foo=Bar",
                "Bar=Baz"
            )
        )

        assertNotNull(read)
        assertEquals("Bar", read["Foo"])
        assertEquals("Baz", read["Bar"])
    }

    @Test
    fun `Can it generate a basic key value file`() {
        val hashMap = HashMap<String, String>()
        hashMap["Bar"] = "Baz"
        hashMap["Foo"] = "Bar"

        val write = KeyValueConfigHandler().handleWrite(hashMap)

        assertEquals(mutableListOf(
            "Bar=Baz",
            "Foo=Bar"
        ), write)
    }

}