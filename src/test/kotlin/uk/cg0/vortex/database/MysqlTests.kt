package uk.cg0.vortex.database

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
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

    object OtherTable: DatabaseTable() {
        override val tableName: String
            get() = "others"
        override val primaryKey: DatabaseColumn<*>
            get() = this.id

        val id = id()
        val name = varchar("name")
    }

    @Before
    fun dropAllTable() {
        MySqlTestTable.dropIfExists()
        MysqlTestDefaultTable.dropIfExists()
        OtherTable.dropIfExists()
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

        MysqlTestDefaultTable.drop()
    }

    @Test
    fun `Can we create two tables and join them together`() {
        MySqlTestTable.create()
        OtherTable.create()

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Foo"
            it[MySqlTestTable.age] = 20
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Bar"
            it[MySqlTestTable.age] = 21
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Baz"
            it[MySqlTestTable.age] = 22
        }

        OtherTable.insert {
            it[OtherTable.name] = "Baz"
        }

        val response = OtherTable.where(OtherTable.id, 1).join(OtherTable.name, "=", MySqlTestTable.name).first()

        assertEquals(1, response[OtherTable.id])
        assertEquals("Baz", response[OtherTable.name])
        assertEquals(3, response[MySqlTestTable.id])
        assertEquals("Baz", response[MySqlTestTable.name])

        MySqlTestTable.drop()
        OtherTable.drop()
    }

    @Test
    fun `Can we create two tables and left join them together`() {
        MySqlTestTable.create()
        OtherTable.create()

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Foo"
            it[MySqlTestTable.age] = 20
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Bar"
            it[MySqlTestTable.age] = 21
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Baz"
            it[MySqlTestTable.age] = 22
        }

        OtherTable.insert {
            it[OtherTable.name] = "Baz"
        }

        val response = OtherTable.where(OtherTable.id, 1).leftJoin(OtherTable.name, "=", MySqlTestTable.name).first()

        assertEquals(1, response[OtherTable.id])
        assertEquals("Baz", response[OtherTable.name])
        assertEquals(3, response[MySqlTestTable.id])
        assertEquals("Baz", response[MySqlTestTable.name])

        MySqlTestTable.drop()
        OtherTable.drop()
    }

    @Test
    fun `Can we create two tables and right join them together`() {
        MySqlTestTable.create()
        OtherTable.create()

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Foo"
            it[MySqlTestTable.age] = 20
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Bar"
            it[MySqlTestTable.age] = 21
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Baz"
            it[MySqlTestTable.age] = 22
        }

        OtherTable.insert {
            it[OtherTable.name] = "Baz"
        }

        val response = OtherTable.where(OtherTable.id, 1).rightJoin(OtherTable.name, "=", MySqlTestTable.name).first()

        assertEquals(1, response[OtherTable.id])
        assertEquals("Baz", response[OtherTable.name])
        assertEquals(3, response[MySqlTestTable.id])
        assertEquals("Baz", response[MySqlTestTable.name])

        MySqlTestTable.drop()
        OtherTable.drop()
    }

    @Test
    fun `Can we create two tables and cross join them together`() {
        MySqlTestTable.create()
        OtherTable.create()

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Foo"
            it[MySqlTestTable.age] = 20
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Bar"
            it[MySqlTestTable.age] = 21
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "Baz"
            it[MySqlTestTable.age] = 22
        }

        OtherTable.insert {
            it[OtherTable.name] = "Baz"
        }

        val response = OtherTable.where(OtherTable.id, 1).where(MySqlTestTable.id, 1).crossJoin(MySqlTestTable).first()

        assertEquals(1, response[OtherTable.id])
        assertEquals("Baz", response[OtherTable.name])
        assertEquals(1, response[MySqlTestTable.id])
        assertEquals("Foo", response[MySqlTestTable.name])

        MySqlTestTable.drop()
        OtherTable.drop()
    }

    @Test
    fun `Can we insert into table and then check the row exists`() {
        MySqlTestTable.create()
        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "test"
            it[MySqlTestTable.age] = 0
        }

        Assert.assertTrue(MySqlTestTable.where(MySqlTestTable.name, "test").exists())

        MySqlTestTable.drop()
    }

    @Test
    fun `Can we not insert into a table and check the row doesn't exist`() {
        MySqlTestTable.create()

        Assert.assertFalse(MySqlTestTable.where(MySqlTestTable.name, "test").exists())

        MySqlTestTable.drop()
    }
}