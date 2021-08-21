package uk.cg0.vortex.json.objects

import uk.cg0.vortex.database.*

class JsonObject(private val data: HashMap<String, JsonType>): JsonType() {
    override fun toDatabaseRow(table: DatabaseTable): DatabaseRow {
        return table.find(data["primaryKeyValue"]?.toAny() ?: "")
    }

    override fun toHashMap(): HashMap<String, JsonType> {
        return data
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> toHashMapOf(): HashMap<String, T> {
        val output = HashMap<String, T>()

        for (item in data) {
            output[item.key] = item.value.toAny() as T
        }

        return output
    }

    override fun toAny(): Any {
        return data
    }
}