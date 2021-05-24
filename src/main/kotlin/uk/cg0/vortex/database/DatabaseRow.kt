package uk.cg0.vortex.database

class DatabaseRow(private val rowData: HashMap<String, String>) {
    operator fun get(key: String): String? {
        return rowData[key]
    }
}