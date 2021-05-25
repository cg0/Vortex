package uk.cg0.vortex.database

import java.sql.ResultSet

class DatabaseResult(resultSet: ResultSet, tableName: String) {
    private val rows = ArrayList<DatabaseRow>();
    init {
        val metadata = resultSet.metaData
        val rowData = HashMap<String, String>()

        while(resultSet.next()) {
            for (i in 1 .. metadata.columnCount) {
                rowData[metadata.getColumnName(i)] = resultSet.getString(i)
            }
        }

        rows.add(DatabaseRow(rowData, tableName))
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

    fun contains(key: String, value: String): Boolean {
        for (row in rows) {
            if (row[key] == value) {
                return true
            }
        }
        return false
    }
}