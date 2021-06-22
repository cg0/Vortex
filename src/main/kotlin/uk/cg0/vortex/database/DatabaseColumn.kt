package uk.cg0.vortex.database

import uk.cg0.vortex.database.attribute.AutoIncrementAttribute
import uk.cg0.vortex.database.attribute.DatabaseAttribute
import uk.cg0.vortex.database.attribute.DefaultAttribute

class DatabaseColumn<T> (val table: DatabaseTable, val columnName: String, val rawDataType: String) {
    var maxLength: Int = Int.MAX_VALUE
    val attributes = ArrayList<DatabaseAttribute>()
    val dataType: String
    get() {
        return when(rawDataType) {
            "varchar" -> {
                "$rawDataType($maxLength)"
            }
            else -> rawDataType
        }
    }

    fun maxLength(maxLength: Int): DatabaseColumn<T> {
        this.maxLength = maxLength
        return this
    }

    fun default(value: Any): DatabaseColumn<T> {
        this.attributes.add(DefaultAttribute(value))
        return this
    }

    fun autoIncrement(): DatabaseColumn<T> {
        this.attributes.add(AutoIncrementAttribute())
        return this
    }

    override fun toString(): String {
        return "${table.tableName}.$columnName"
    }
}