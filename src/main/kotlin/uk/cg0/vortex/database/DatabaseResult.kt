package uk.cg0.vortex.database

import java.sql.ResultSet

class DatabaseResult(resultSet: ResultSet) {
    private val rows = ArrayList<DatabaseRow>();
    init {
        val metadata = resultSet.metaData
        val rowData = HashMap<String, String>()

        while(resultSet.next()) {
            for (i in 1 .. metadata.columnCount) {
                rowData[metadata.getColumnName(i)] = resultSet.getString(i)
            }
        }

        rows.add(DatabaseRow(rowData))
    }

    operator fun iterator(): MutableIterator<DatabaseRow> {
        return rows.iterator()
    }

    fun first(): DatabaseRow {
        return rows.first()
    }
}