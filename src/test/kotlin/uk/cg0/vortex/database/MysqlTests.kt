package uk.cg0.vortex.database

import org.junit.Assert.assertEquals
import org.junit.Test

class MysqlTests {
    object MySqlTestTable: DatabaseTable() {
        override val tableName: String
            get() = "mysql_tests"
        override val primaryKey: DatabaseColumn<*>
            get() = this.id

        val id = id()
        val name = varchar("name")
        val age = integer("age")
    }

    object MysqlTestDefaultTable: DatabaseTable() {
        override val tableName: String
            get() = "mysql_default_tests"
        override val primaryKey: DatabaseColumn<*>
            get() = this.id

        val id = id()
        val name = varchar("name")
        val age = integer("age").default(25)
    }

    @Test
    fun `Can we create and drop a table`() {
        // There are no asserts but any errors would throw an exception and fail the test
        MySqlTestTable.create()
        MySqlTestTable.drop()
    }

    @Test
    fun `Can we create, insert and query a table`() {
        MySqlTestTable.create()

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Foo"
            it[MySqlTestTable.age] = 20
        }

        val result = MySqlTestTable.first()

        assertEquals("Foo", result[MySqlTestTable.name])
        assertEquals(20, result[MySqlTestTable.age])

        MySqlTestTable.drop()
    }

    @Test
    fun `Can we create, insert and run a count on a table`() {
        MySqlTestTable.create()

        for (i in 0 until 5) {
            MySqlTestTable.insert {
                it[MySqlTestTable.name] = "Test"
                it[MySqlTestTable.age] = i * i
            }
        }

        assertEquals(5, MySqlTestTable.count())
        assertEquals(2, MySqlTestTable.where(MySqlTestTable.id, "<", 3).count())

        MySqlTestTable.drop()
    }

    @Test
    fun `Can we create a table with a default value and insert without it`() {
        MysqlTestDefaultTable.create()

        MysqlTestDefaultTable.insert {
            it[MySqlTestTable.name] = "Test"
        }

        val data = MysqlTestDefaultTable.first()

        assertEquals("Test", data[MysqlTestDefaultTable.name])
        assertEquals(25, data[MysqlTestDefaultTable.age])
    }
}