package uk.cg0.vortex.database

import org.junit.Assert.assertEquals
import org.junit.Test

class QueryBuildTest {
    object TestTable: DatabaseTable() {
        override val tableName: String
            get() = "tests"
        override val primaryKey: DatabaseColumn<*>
            get() = this.id

        val id = id()
        val name = varchar("name")
        val age = integer("age")

    }

    @Test
    fun `Can we query a database table without a conditional`() {
        val builder = QueryBuilder(TestTable)
        val sql = builder.toSql()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests`", sql)
    }

    @Test
    fun `Can we query a database table where the name is Test`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.where(TestTable.name, "test").toDatabaseQuery()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests` WHERE tests.name = ?", query.query)
        assertEquals("test", query.data[0])
    }

    @Test
    fun `Can we query a database table where the name is Test and age = 4`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.where(TestTable.name, "test").where(TestTable.age, 4).toDatabaseQuery()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests` WHERE tests.name = ? AND tests.age = ?", query.query)
        assertEquals("test", query.data[0])
        assertEquals(4, query.data[1])
    }

    @Test
    fun `Can we query a database table where the age is more than 2`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.where(TestTable.age, ">", 2).toDatabaseQuery()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests` WHERE tests.age > ?", query.query)
        assertEquals(2, query.data[0])
    }

    @Test
    fun `Can we query a database table where the age is more than 2 but less than 10`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.where(TestTable.age, ">", 2).where(TestTable.age, "<", 10).toDatabaseQuery()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests` WHERE tests.age > ? AND tests.age < ?", query.query)
        assertEquals(2, query.data[0])
        assertEquals(10, query.data[1])
    }

    @Test
    fun `Can we query a database table where name contains test`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.where(TestTable.name, "LIKE", "%test%").toDatabaseQuery()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests` WHERE tests.name LIKE ?", query.query)
        assertEquals("%test%", query.data[0])
    }

    @Test
    fun `Can we query a database tabe where the name is foo or bar`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.where(TestTable.name, "foo").orWhere(TestTable.name, "bar").toDatabaseQuery()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests` WHERE tests.name = ? OR tests.name = ?", query.query)
        assertEquals("foo", query.data[0])
        assertEquals("bar", query.data[1])
    }

    @Test
    fun `Can we query a database table where the age is 27 and the name is foo or bar`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.where(TestTable.age, 27).where {
            it.where(TestTable.name, "foo").orWhere(TestTable.name, "bar")
        }.toDatabaseQuery()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests` WHERE tests.age = ? AND (tests.name = ? OR tests.name = ?)", query.query)
        assertEquals(27, query.data[0])
        assertEquals("foo", query.data[1])
        assertEquals("bar", query.data[2])
    }

    @Test
    fun `Can we query the first item in a database table`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.limit(1).toDatabaseQuery()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests` LIMIT 1", query.query)
    }

    @Test
    fun `Can we query the first item in a database table with an age of 0`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.where(TestTable.age, 0).limit(1).toDatabaseQuery()

        assertEquals("SELECT tests.age, tests.id, tests.name FROM `tests` WHERE tests.age = ? LIMIT 1", query.query)
        assertEquals(0, query.data[0])
    }

    @Test
    fun `Can we execute a query in an odd order`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.limit(1).where(TestTable.name, "foo").select(TestTable.name).toDatabaseQuery()

        assertEquals("SELECT tests.name FROM `tests` WHERE tests.name = ? LIMIT 1", query.query)
        assertEquals("foo", query.data[0])
    }

    @Test
    fun `Can we insert data into a database table`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.insert {
            it[TestTable.name] = "Connor"
            it[TestTable.age] = 23
        }.toDatabaseQuery()

        assertEquals("INSERT INTO `tests` (name,age) VALUES ?,?", query.query)
        assertEquals("Connor", query.data[0])
        assertEquals(23, query.data[1])
    }

    @Test
    fun `Can we update the name of all rows from foo to bar in a database table`() {
        val builder = QueryBuilder(TestTable)
        val query = builder.where(TestTable.name, "foo").update {
            it[TestTable.name] = "bar"
        }.toDatabaseQuery()

        assertEquals("UPDATE `tests` SET name=? WHERE tests.name = ?", query.query)
        assertEquals("bar", query.data[0])
        assertEquals("foo", query.data[1])
    }
}