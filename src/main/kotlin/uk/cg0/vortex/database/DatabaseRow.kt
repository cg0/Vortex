package uk.cg0.vortex.database

class DatabaseRow(private val rowData: HashMap<String, String>, private val tableName: String) {
    operator fun get(key: String): String? {
        return rowData[key]
    }

    operator fun set(key: String, value: String) {
        rowData[key] = value
    }

    fun update() {
        val values = ArrayList<String>()

        for (data in rowData) {
            values.add(data.key)
            values.add(data.value)
        }
        QueryBuilder(tableName).update(*values.toTypedArray()).where("id", this["id"].toString()).execute()
    }

    override fun toString(): String {
        return rowData.toString()
    }
}