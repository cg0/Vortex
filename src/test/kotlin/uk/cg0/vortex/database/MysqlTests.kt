package uk.cg0.vortex.database

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.cg0.vortex.Vortex

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

    object ModifiedMysqlTestTable: DatabaseTable() {
        override val tableName: String
            get() = "mysql_tests"
        override val primaryKey: DatabaseColumn<*>
            get() = this.id

        val id = id()
        val name = varchar("name")
        val ageChanged = integer("age_changed")
        val new = integer("new")

        override val columnRenames: HashMap<String, DatabaseColumn<*>>
            get() = hashMapOf(
                "age" to ageChanged
            )
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

    object CityTable: DatabaseTable() {
        override val tableName: String
            get() = "cities"
        override val primaryKey: DatabaseColumn<*>
            get() = this.id

        val id = id()
        val name = varchar("name")
        val countryId = integer("country_id")
        val country = hasOne(countryId, CountryTable.id)
    }

    object CountryTable: DatabaseTable() {
        override val tableName: String
            get() = "countries"
        override val primaryKey: DatabaseColumn<*>
            get() = id

        val id = id()
        val name = varchar("name")
        val cities = hasMany(id, CityTable.countryId)
    }

    @Before
    fun dropAllTable() {
        MySqlTestTable.dropIfExists()
        MysqlTestDefaultTable.dropIfExists()
        OtherTable.dropIfExists()
        CityTable.dropIfExists()
        CountryTable.dropIfExists()
    }

    private fun setupCitiesAndCountries() {
        CityTable.create()
        CountryTable.create()

        CountryTable.insert {
            it[CountryTable.name] = "England"
        }
        CountryTable.insert {
            it[CountryTable.name] = "Wales"
        }
        CountryTable.insert {
            it[CountryTable.name] = "Scotland"
        }
        CountryTable.insert {
            it[CountryTable.name] = "The Netherlands"
        }

        CityTable.insert {
            it[CityTable.name] = "London"
            it[CityTable.countryId] = 1
        }
        CityTable.insert {
            it[CityTable.name] = "Manchester"
            it[CityTable.countryId] = 1
        }
        CityTable.insert {
            it[CityTable.name] = "Birmingham"
            it[CityTable.countryId] = 1
        }
        CityTable.insert {
            it[CityTable.name] = "Cardiff"
            it[CityTable.countryId] = 2
        }
        CityTable.insert {
            it[CityTable.name] = "Swansea"
            it[CityTable.countryId] = 2
        }
        CityTable.insert {
            it[CityTable.name] = "Edinburgh"
            it[CityTable.countryId] = 3
        }
        CityTable.insert {
            it[CityTable.name] = "Glasgow"
            it[CityTable.countryId] = 3
        }
        CityTable.insert {
            it[CityTable.name] = "Amsterdam"
            it[CityTable.countryId] = 4
        }
        CityTable.insert {
            it[CityTable.name] = "Eindhoven"
            it[CityTable.countryId] = 4
        }
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

    @Test
    fun `Can we insert data into a table and find by ID using the find shorthand`() {
        MySqlTestTable.create()

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "foo"
            it[MySqlTestTable.age] = 0
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "bar"
            it[MySqlTestTable.age] = 0
        }

        MySqlTestTable.insert {
            it[MySqlTestTable.name] = "baz"
            it[MySqlTestTable.age] = 0
        }

        assertEquals("bar", MySqlTestTable.find(2)[MySqlTestTable.name])
        assertEquals("baz", MySqlTestTable.find(3)[MySqlTestTable.name])

        MySqlTestTable.drop()
    }

    @Test
    fun `Can we access country information through single a single city`() {
        setupCitiesAndCountries()

        assertEquals("England", CityTable.first()[CityTable.country][CountryTable.name])
        assertEquals("Wales", CityTable.find(4)[CityTable.country][CountryTable.name])

        CityTable.drop()
        CountryTable.drop()
    }

    @Test
    fun `Can we access country information for all cities`() {
        setupCitiesAndCountries()
        val cities = CityTable.get()

        for (city in cities) {
            assertEquals(when (city[CityTable.countryId]) {
                1 -> "England"
                2 -> "Wales"
                3 -> "Scotland"
                4 -> "The Netherlands"
                else -> "FAIL"
            }, city[CityTable.country][CountryTable.name])
        }

        CityTable.drop()
        CountryTable.drop()
    }

    @Test
    fun `Can we access all cities belonging to a country`() {
        setupCitiesAndCountries()

        val england = CountryTable.find(1)
        val netherlands = CountryTable.find(4)

        for (city in england[CountryTable.cities]) {
            assertEquals(when (city[CityTable.id]) {
                1 -> "London"
                2 -> "Manchester"
                3 -> "Birmingham"
                else -> "FAIL"
            }, city[CityTable.name])
        }

        for (city in netherlands[CountryTable.cities]) {
            assertEquals(when (city[CityTable.id]) {
                8 -> "Amsterdam"
                9 -> "Eindhoven"
                else -> "FAIL"
            }, city[CityTable.name])
        }

        CityTable.drop()
        CountryTable.drop()
    }

    @Test
    fun `Can we list all cities belonging to all countries`() {
        setupCitiesAndCountries()
        val countries = CountryTable.get()

        for (country in countries) {
            for (city in country[CountryTable.cities]) {
                assertEquals(when (city[CityTable.id]) {
                    1 -> "London"
                    2 -> "Manchester"
                    3 -> "Birmingham"
                    4 -> "Cardiff"
                    5 -> "Swansea"
                    6 -> "Edinburgh"
                    7 -> "Glasgow"
                    8 -> "Amsterdam"
                    9 -> "Eindhoven"
                    else -> "FAIL"
                }, city[CityTable.name])
            }
        }

        CityTable.drop()
        CountryTable.drop()
    }

    @Test
    fun `Can we migrate a table to another schema`() {
        MySqlTestTable.create()

        val initialResponse = Vortex.database.executeQuery(DatabaseQuery("EXPLAIN ${MySqlTestTable.tableName}", ArrayList()))
        val initialFields = ArrayList<String>()
        while (initialResponse.next()) {
            initialFields.add(initialResponse.getString("Field"))
        }

        Vortex.migrateTables(arrayListOf(
            ModifiedMysqlTestTable
        ))

        val newResponse = Vortex.database.executeQuery(DatabaseQuery("EXPLAIN ${MySqlTestTable.tableName}", ArrayList()))
        val newFields = ArrayList<String>()
        while (newResponse.next()) {
            newFields.add(newResponse.getString("Field"))
        }

        assertFalse(initialFields.contains("age_changed"))
        assertTrue(initialFields.contains("age"))

        assertTrue(newFields.contains("age_changed"))
        assertFalse(newFields.contains("age"))
    }
}