package uk.cg0.vortex.database

import uk.cg0.vortex.database.attribute.DatabaseAttributeType
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.experimental.and
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

class Database(host: String?, database: String?, username: String?, password: String?) {
    private val connection: Connection
    init {
        Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        connection = DriverManager
            .getConnection("jdbc:mysql://$host/$database?"
                    + "user=$username&password=$password");
    }

    private fun prepareStatement(query: DatabaseQuery): PreparedStatement {
        val statement = connection.prepareStatement(query.query)

        println(query.query)

        for (attribute in query.data.withIndex()) {
            statement.setObject(attribute.index + 1, attribute.value)
        }

        return statement
    }

    fun executeUpdate(query: DatabaseQuery): Int {
        val statement = prepareStatement(query)

        return statement.executeUpdate()
    }

    fun executeQuery(query: DatabaseQuery): ResultSet {
        val statement = prepareStatement(query)

        return statement.executeQuery()
    }

    fun execute(query: DatabaseQuery): Boolean {
        val statement = prepareStatement(query)

        return statement.execute()
    }

    fun getFieldsFromTableModel(databaseTable: DatabaseTable, filter: DatabaseFieldFilter = DatabaseFieldFilter.ALL): ArrayList<DatabaseColumn<*>> {
        val fields = ArrayList<DatabaseColumn<*>>()
        val kClass = databaseTable::class

        for (property in kClass.memberProperties) {
            if (property.name == "primaryKey") {
                continue
            }
            if (property.returnType.arguments.isNotEmpty()
                && property.returnType == DatabaseColumn::class.createType(listOf(property.returnType.arguments.first()))
                && filter.has(DatabaseFieldFilter.DIRECT_FIELDS)) {

                    fields.add(property.call(databaseTable) as DatabaseColumn<*>)
            } else if (property.returnType.arguments.isNotEmpty()
                && property.returnType == DatabaseRelation::class.createType(listOf(property.returnType.arguments.first()))
                && filter.has(DatabaseFieldFilter.RELATIONAL_FIELDS)) {

                    val relationReference = property.call(databaseTable) as DatabaseRelation<*>

                    for (relationField in getFieldsFromTableModel(relationReference.foreignKey.table)) {
                        fields.add(relationField)
                    }
            }
        }

        return fields
    }

    fun createTable(vararg fields: DatabaseColumn<*>) {
        val table = fields.first().table
        var query = "CREATE TABLE `${table.tableName}` "

        val encodedFields = ArrayList<String>()
        for (field in fields) {
            var attributes = ""
            for (attribute in field.attributes) {
                attributes += attribute.value.getAttribute()
            }
            val nullable = if (field.nullable) "" else "NOT NULL"
            encodedFields.add("${field.columnName} ${field.dataType} $attributes $nullable")
        }

        encodedFields.add("PRIMARY KEY (${table.primaryKey.columnName})")

        executeUpdate(DatabaseQuery("$query (${encodedFields.joinToString(",")})", ArrayList()))
    }

    fun dropTable(tableName: String) {
        executeUpdate(DatabaseQuery("DROP TABLE `$tableName`", ArrayList()))
    }

    fun dropTableIfExists(tableName: String) {
        executeUpdate(DatabaseQuery("DROP TABLE IF EXISTS `$tableName`", ArrayList()))
    }

    fun truncateTable(tableName: String) {
        executeUpdate(DatabaseQuery("TRUNCATE TABLE `$tableName`", ArrayList()))
    }

    fun alterTable(table: DatabaseTable,
                   modify: HashMap<String, DatabaseColumn<*>> = HashMap(),
                   add: ArrayList<DatabaseColumn<*>> = ArrayList(),
                   drop: ArrayList<String> = ArrayList()): Boolean {
        var query = "ALTER TABLE ${table.tableName}"
        val changes = ArrayList<String>()

        for (field in modify) {
            if (field.key != field.value.columnName) {
                changes.add("RENAME COLUMN ${field.key} TO ${field.value.columnName}")
            } else {
                var attributes = ""
                for (attribute in field.value.attributes) {
                    attributes += attribute.value.getAttribute()
                }
                val nullable = if (field.value.nullable) "" else "NOT NULL"
                changes.add("MODIFY COLUMN ${field.value.columnName} ${field.value.dataType} $attributes $nullable")
            }
        }

        for (field in add) {
            var attributes = ""
            for (attribute in field.attributes) {
                attributes += attribute.value.getAttribute()
            }
            val nullable = if (field.nullable) "" else "NOT NULL"
            changes.add("ADD COLUMN ${field.columnName} ${field.dataType} $attributes $nullable")
        }

        for (field in drop) {
            changes.add("DROP COLUMN `$field`")
        }

        if (changes.isEmpty()) {
            return false
        }

        return executeUpdate(DatabaseQuery("$query ${changes.joinToString(",")}", ArrayList())) > 0
    }

    fun doesTableExist(tableName: String): Boolean {
        val query = "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_NAME = ? \n" +
                "AND TABLE_SCHEMA in (SELECT DATABASE());"

        val results = executeQuery(DatabaseQuery(query, arrayListOf(tableName)))
        if (results.next()) {
            return results.getInt("COUNT(*)") == 1
        }
        return false
    }

    fun handleMigration(table: DatabaseTable) {
        if (!doesTableExist(table.tableName)) {
            table.create()
            return
        }
        val response = executeQuery(DatabaseQuery("DESCRIBE `${table.tableName}`", ArrayList()))

        val columns = HashMap<String, DatabaseColumn<*>>()
        for (column in this.getFieldsFromTableModel(table)) {
            columns[column.columnName] = column
        }

        val processedFields = ArrayList<String>()

        // Fields to pass to alter
        val modify = HashMap<String, DatabaseColumn<*>>()
        val add = ArrayList<DatabaseColumn<*>>()
        val drop = ArrayList<String>()

        while (response.next()) {
            val field = response.getString("Field")
            val type = response.getString("Type")
            val nullable = response.getString("Null").equals("YES")
            val key = response.getString("Key")
            val default = response.getString("Default")
            val extra = response.getString("Extra")

            if (field !in columns) {
                val fieldWithoutLength = field.replace(Regex("\\([0-9]+\\)"), "")
                if (fieldWithoutLength in table.columnRenames.keys) {
                    val column = table.columnRenames[fieldWithoutLength]
                    if (column != null) {
                        modify[fieldWithoutLength] = column
                        processedFields.add(column.columnName)
                    }
                } else {
                    drop.add(field)
                }
            } else {
                val column = columns[field]
                if (column != null) {
                    val dataTypeMatches = column.dataType == type
                    val nullableMatches = column.nullable == nullable
                    val primaryKeyMatches = if (key == "PRI") column.table.primaryKey == column else true
                    val defaultMatches =  default == column.default
                    var extraMatches = true

                    for (attributes in column.attributes) {
                        if (attributes.value.getAttribute().lowercase() !in extra) {
                            extraMatches = false
                        }
                    }

                    if (!(dataTypeMatches && nullableMatches && primaryKeyMatches && defaultMatches && extraMatches)) {
                        modify[field] = column
                    }
                }
            }

            processedFields.add(field)
        }

        for (column in columns) {
            if (column.key !in processedFields) {
                add.add(column.value)
            }
        }

        this.alterTable(table, modify, add, drop)
    }

    enum class DatabaseFieldFilter (val id: Byte) {
        DIRECT_FIELDS(0x1),
        RELATIONAL_FIELDS(0x2),
        ALL(0xF);

        fun has(filter: DatabaseFieldFilter): Boolean {
            return (this.id and filter.id) > 0
        }
    }
}