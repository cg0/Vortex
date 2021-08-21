package uk.cg0.vortex.json.objects

class JsonList(private val list: ArrayList<JsonType>): JsonType() {
    override fun toArrayList(): ArrayList<JsonType> {
        return list
    }

    override fun toArray(): Array<JsonType> {
        return list.toTypedArray()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> toArrayListOf(): ArrayList<T> {
        val output = ArrayList<T>()
        for (item in list) {
            output.add(item.toAny() as T)
        }

        return output
    }

    override fun toAny(): Any {
        return list
    }
}