package uk.cg0.vortex.json

import junit.framework.TestCase.assertEquals
import org.junit.Test
import uk.cg0.vortex.database.DatabaseColumn
import uk.cg0.vortex.database.DatabaseTable

class JsonTests {
    object TestTable: DatabaseTable() {
        override val tableName: String
            get() = "tests"
        override val primaryKey: DatabaseColumn<*>
            get() = this.id

        val id = id()
        val test = varchar("test")
    }

    @Test
    fun `Can we parse an ArrayList of strings with pretty print enabled`() {
        val toEncode = arrayListOf("this", "is", "a", "test")

        val encoded = Json.encode(toEncode, JsonPrintType.PRETTY_PRINT)
        val decoded = Json.decode(encoded)

        assertEquals(toEncode, decoded.toArrayListOf<String>())
    }

    @Test
    fun `Can we parse an ArrayList of strings with pretty print disabled`() {
        val toEncode = arrayListOf("this", "is", "a", "test")

        val encoded = Json.encode(toEncode, JsonPrintType.MINIFIED)
        val decoded = Json.decode(encoded)

        assertEquals(toEncode, decoded.toArrayListOf<String>())
    }

    @Test
    fun `Can we parse an ArrayList of various primitives with pretty print enabled`() {
        val toEncode = arrayListOf("string", 3.14, 1, false, Long.MAX_VALUE)

        val encoded = Json.encode(toEncode, JsonPrintType.PRETTY_PRINT)
        val decoded = Json.decode(encoded).toArrayList()!!

        assertEquals(toEncode[0], decoded[0].toString())
        assertEquals(toEncode[1], decoded[1].toDouble())
        assertEquals(toEncode[2], decoded[2].toInt())
        assertEquals(toEncode[3], decoded[3].toBoolean())
        assertEquals(toEncode[4], decoded[4].toLong())

    }

    @Test
    fun `Can we parse an ArrayList of various primitives with pretty print disabled`() {
        val toEncode = arrayListOf("string", 3.14, 1, false, Long.MAX_VALUE)

        val encoded = Json.encode(toEncode, JsonPrintType.MINIFIED)
        val decoded = Json.decode(encoded).toArrayList()!!

        assertEquals(toEncode[0], decoded[0].toString())
        assertEquals(toEncode[1], decoded[1].toDouble())
        assertEquals(toEncode[2], decoded[2].toInt())
        assertEquals(toEncode[3], decoded[3].toBoolean())
        assertEquals(toEncode[4], decoded[4].toLong())
    }

    @Test
    fun `Can we parse a HashMap of strings with pretty print enabled`() {
        val toEncode = hashMapOf(
            "this" to "is",
            "a" to "test"
        )

        val encoded = Json.encode(toEncode, JsonPrintType.PRETTY_PRINT)
        val decoded = Json.decode(encoded).toHashMap()!!

        assertEquals(toEncode["this"], decoded["this"].toString())
        assertEquals(toEncode["a"], decoded["a"].toString())
    }

    @Test
    fun `Can we parse a HashMap of strings with pretty print disabled`() {
        val toEncode = hashMapOf(
            "this" to "is",
            "a" to "test"
        )

        val encoded = Json.encode(toEncode, JsonPrintType.MINIFIED)
        val decoded = Json.decode(encoded).toHashMap()!!

        assertEquals(toEncode["this"], decoded["this"].toString())
        assertEquals(toEncode["a"], decoded["a"].toString())
    }

    @Test
    fun `Can we parse a HashMap of various primitive with pretty print enabled`() {
        val toEncode = hashMapOf(
            "string" to "string",
            "long" to Long.MAX_VALUE,
            "float" to 1.23F,
            "double" to 1.23,
            "int" to Int.MAX_VALUE,
            "short" to Short.MAX_VALUE,
            "byte" to Byte.MAX_VALUE,
            "boolean" to false
        )

        val encoded = Json.encode(toEncode, JsonPrintType.PRETTY_PRINT)
        val decoded = Json.decode(encoded).toHashMap()!!

        assertEquals(toEncode["string"], decoded["string"].toString())
        assertEquals(toEncode["long"], decoded["long"]?.toLong())
        assertEquals(toEncode["float"], decoded["float"]?.toFloat())
        assertEquals(toEncode["double"], decoded["double"]?.toDouble())
        assertEquals(toEncode["int"], decoded["int"]?.toInt())
        assertEquals(toEncode["short"], decoded["short"]?.toShort())
        assertEquals(toEncode["byte"], decoded["byte"]?.toByte())
        assertEquals(toEncode["boolean"], decoded["boolean"]?.toBoolean())
    }

    @Test
    fun `Can we parse a HashMap of various primitive with pretty print disabled`() {
        val toEncode = hashMapOf(
            "string" to "string",
            "long" to Long.MAX_VALUE,
            "float" to 1.23F,
            "double" to 1.23,
            "int" to Int.MAX_VALUE,
            "short" to Short.MAX_VALUE,
            "byte" to Byte.MAX_VALUE,
            "boolean" to false
        )

        val encoded = Json.encode(toEncode, JsonPrintType.MINIFIED)
        val decoded = Json.decode(encoded).toHashMap()!!

        assertEquals(toEncode["string"], decoded["string"].toString())
        assertEquals(toEncode["long"], decoded["long"]?.toLong())
        assertEquals(toEncode["float"], decoded["float"]?.toFloat())
        assertEquals(toEncode["double"], decoded["double"]?.toDouble())
        assertEquals(toEncode["int"], decoded["int"]?.toInt())
        assertEquals(toEncode["short"], decoded["short"]?.toShort())
        assertEquals(toEncode["byte"], decoded["byte"]?.toByte())
        assertEquals(toEncode["boolean"], decoded["boolean"]?.toBoolean())
    }

    @Test
    fun `Can we parse a DatabaseResult with pretty print enabled`() {
        TestTable.dropIfExists()
        TestTable.create()
        TestTable.insert {
            it[TestTable.test] = "Test"
        }

        val toEncode = TestTable.find(1)
        val encoded = Json.encode(toEncode, JsonPrintType.PRETTY_PRINT)
        val decoded = Json.decode(encoded).toDatabaseRow(TestTable)

        assertEquals(toEncode, decoded)
    }

    @Test
    fun `Can we parse a DatabaseResult with pretty print disabled`() {
        TestTable.dropIfExists()
        TestTable.create()
        TestTable.insert {
            it[TestTable.test] = "Test"
        }

        val toEncode = TestTable.find(1)
        val encoded = Json.encode(toEncode, JsonPrintType.MINIFIED)
        val decoded = Json.decode(encoded).toDatabaseRow(TestTable)

        TestTable.drop()
        assertEquals(toEncode, decoded)
    }

    @Test
    fun `Can we decode a HashMap of HashMaps of Strings with pretty print enabled`() {
        val toEncode = hashMapOf(
            "numbers" to hashMapOf(
                "long" to Long.MAX_VALUE,
                "float" to 1.23F,
                "double" to 1.23,
                "int" to Int.MAX_VALUE,
                "short" to Short.MAX_VALUE,
                "byte" to Byte.MAX_VALUE,
                "boolean" to false
            ),
            "countryCapitals" to hashMapOf(
                "England" to "London",
                "Wales" to "Cardiff",
                "Scotland" to "Edinburgh",
                "Northern Ireland" to "Belfast"
            )
        )
        val encoded = Json.encode(toEncode, JsonPrintType.PRETTY_PRINT)
        val decoded = Json.decode(encoded).toHashMap()!!

        assertEquals(toEncode["test"], decoded["test"]?.toHashMapOf<String>())
        assertEquals(toEncode["countryCapitals"], decoded["countryCapitals"]?.toHashMapOf<String>())
    }

    @Test
    fun `Can we decode a HashMap of HashMaps of Strings with pretty print disabled`() {
        val toEncode = hashMapOf(
            "test" to hashMapOf(
                "foo" to "bar",
                "bar" to "baz"
            ),
            "countryCapitals" to hashMapOf(
                "England" to "London",
                "Wales" to "Cardiff",
                "Scotland" to "Edinburgh",
                "Northern Ireland" to "Belfast"
            )
        )
        val encoded = Json.encode(toEncode, JsonPrintType.MINIFIED)
        val decoded = Json.decode(encoded).toHashMap()!!

        assertEquals(toEncode["test"], decoded["test"]?.toHashMapOf<String>())
        assertEquals(toEncode["countryCapitals"], decoded["countryCapitals"]?.toHashMapOf<String>())
    }

    @Test
    fun `Can we decode a HashMap of HashMaps of various primitives with pretty print enabled`() {
        val toEncode = hashMapOf(
            "numbers" to hashMapOf(
                "long" to Long.MAX_VALUE,
                "float" to 1.23F,
                "double" to 1.23,
                "int" to Int.MAX_VALUE,
                "short" to Short.MAX_VALUE,
                "byte" to Byte.MAX_VALUE,
            ),
            "others" to hashMapOf(
                "string" to "string",
                "boolean" to false,
            )
        )
        val encoded = Json.encode(toEncode, JsonPrintType.PRETTY_PRINT)
        val decoded = Json.decode(encoded).toHashMap()!!

        assertEquals(toEncode["numbers"]?.get("long"), decoded["numbers"]?.toHashMap()?.get("long")?.toLong())
        assertEquals(toEncode["others"], decoded["others"]?.toHashMapOf<Any>())
    }
}