package uk.cg0.vortex.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
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
                attributes += attribute.getAttribute()
            }
            encodedFields.add("${field.columnName} ${field.dataType} $attributes")
        }

        encodedFields.add("PRIMARY KEY (${table.primaryKey.columnName})")

        executeUpdate(DatabaseQuery("$query (${encodedFields.joinToString(",")})", ArrayList()))
    }

    fun dropTable(tableName: String) {
        executeUpdate(DatabaseQuery("DROP TABLE `$tableName`", ArrayList()))
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
                changes.add("RENAME COLUMN `${field.key} TO `${field.value.columnName}`")
            }
            changes.add("MODIFY COLUMN ${field.value.columnName} ${field.value.dataType}")
        }

        for (field in add) {
            changes.add("ADD COLUMN ${field.columnName} ${field.dataType}")
        }

        for (field in drop) {
            changes.add("DROP COLUMN `$field`")
        }

        if (changes.isEmpty()) {
            return false;
        }

        return executeUpdate(DatabaseQuery(query, ArrayList())) > 0
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