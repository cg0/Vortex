package uk.cg0.vortex.json.objects

class JsonBoolean(private val data: Boolean): JsonType() {
    override fun toBoolean(): Boolean {
        return data
    }

    override fun toAny(): Any {
        return data
    }
}