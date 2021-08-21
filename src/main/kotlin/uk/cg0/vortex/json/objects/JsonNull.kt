package uk.cg0.vortex.json.objects

class JsonNull: JsonType() {
    override fun toString(): String {
        return "null"
    }

    override fun toAny(): Any? {
        return null
    }
}