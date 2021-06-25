package uk.cg0.vortex.database

import uk.cg0.vortex.database.attribute.AutoIncrementAttribute
import uk.cg0.vortex.database.attribute.DatabaseAttribute
import uk.cg0.vortex.database.attribute.DatabaseAttributeType

class DatabaseColumn<T> (val table: DatabaseTable, val columnName: String, val rawDataType: String) {
    var maxLength: Int = Int.MAX_VALUE
    val attributes = HashMap<DatabaseAttributeType, DatabaseAttribute>()
    val dataType: String
    get() {
        return if (maxLength < Int.MAX_VALUE) {
            "$rawDataType($maxLength)"
        } else {
            rawDataType
        }
    }
    var default: Any? = null
    var nullable = false

    fun maxLength(maxLength: Int): DatabaseColumn<T> {
        this.maxLength = maxLength
        return this
    }

    fun default(value: Any): DatabaseColumn<T> {
        this.default = value
        return this
    }

    fun autoIncrement(): DatabaseColumn<T> {
        this.attributes[DatabaseAttributeType.AUTO_INCREMENT] = AutoIncrementAttribute()
        return this
    }

    fun nullable(): DatabaseColumn<T> {
        this.nullable = true
        return this
    }

    override fun toString(): String {
        return "${table.tableName}.$columnName"
    }
}