package uk.cg0.vortex.database

import uk.cg0.vortex.Vortex
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DatabaseRow(private val rowData: HashMap<String, Any>, val table: DatabaseTable) {
    private val changedFields = ArrayList<DatabaseColumn<*>>()
    private val relational = HashMap<DatabaseRelation<*>, Any>()

    operator fun get(key: DatabaseColumn<String>): String? {
        return rowData[key.toString()] as String?
    }

    operator fun get(key: DatabaseColumn<Boolean>): Boolean? {
        return rowData[key.toString()] as Boolean?
    }

    operator fun get(key: DatabaseColumn<Int>): Int? {
        return rowData[key.toString()] as Int?
    }

    operator fun get(key: DatabaseColumn<*>): Any? {
        return rowData[key.toString()]
    }

    operator fun get(key: String): Any? {
        return rowData[key]
    }

    operator fun get(key: DatabaseRelation<DatabaseRow>): DatabaseRow {
        if (key !in relational.keys) {
            handleRelationship(key)
        }

        return relational[key] as DatabaseRow
    }

    operator fun get(key: DatabaseRelation<DatabaseResult>): DatabaseResult {
        if (key !in relational.keys) {
            handleRelationship(key)
        }

        return relational[key] as DatabaseResult
    }

    private fun handleRelationship(key: DatabaseRelation<*>) {
        relational[key] = key.get(this[key.localKey] ?: return)
    }

    operator fun set(key: DatabaseColumn<*>, value: Any) {
        rowData[key.toString()] = value
        changedFields.add(key)
    }

    /**
     * Save the modified data into the database
     */
    fun save() {
        table.where(table.primaryKey, this[table.primaryKey]!!).update {
            for (field in changedFields) {
                val fieldData = this[field]
                if (fieldData != null) {
                    it[field] = fieldData
                }
            }
        }
    }

    /**
     * Clear the data stored within the object
     *
     * This will not restore the data to the non-modified state the database
     */
    fun clear() {
        changedFields.clear()
        rowData.clear()
    }

    /**
     * Updates the data on the database to match the changes done to the object
     */
    fun update() {
        val primaryKey = this[table.primaryKey] ?: throw Exception("")
        clear()

        val newData = table.where(table.primaryKey, primaryKey).first()
        for (field in newData.rowData) {
            rowData[field.key] = field.value
        }
    }

    override fun toString(): String {
        return rowData.toString()
    }

    fun keys(): MutableSet<String> {
        return rowData.keys
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is DatabaseRow) {
            return rowData == other.rowData
        }
        return false
    }
}