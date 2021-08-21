package uk.cg0.vortex.json.objects

class JsonString(private val data: String): JsonType() {
    override fun toString(): String {
        return data
    }

    override fun toAny(): Any {
        return data
    }
}