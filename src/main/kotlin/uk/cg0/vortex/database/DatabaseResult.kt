package uk.cg0.vortex.database

import java.sql.ResultSet
import java.util.*

class DatabaseResult(resultSet: ResultSet, table: DatabaseTable) {
    private val rows = ArrayList<DatabaseRow>();
    init {
        while(resultSet.next()) {
            val metadata = resultSet.metaData
            val rowData = HashMap<String, Any>()

            for (i in 1 .. metadata.columnCount) {
                rowData["${metadata.getTableName(i)}.${metadata.getColumnName(i)}"] = resultSet.getObject(i)
            }

            rows.add(DatabaseRow(rowData, table))
        }
    }

    operator fun iterator(): MutableIterator<DatabaseRow> {
        return rows.iterator()
    }

    fun first(): DatabaseRow {
        return rows.first()
    }

    fun last(): DatabaseRow {
        return rows.last()
    }

    fun isEmpty(): Boolean {
        return rows.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return rows.isNotEmpty()
    }

    fun size(): Int {
        return rows.size
    }

    operator fun get(key: Int): DatabaseRow {
        return rows[key]
    }

//    fun contains(key: String, value: String): Boolean {
//        for (row in rows) {
//            if (row[key] == value) {
//                return true
//            }
//        }
//        return false
//    }

    fun toArrayList(): ArrayList<DatabaseRow> {
        return rows
    }
}