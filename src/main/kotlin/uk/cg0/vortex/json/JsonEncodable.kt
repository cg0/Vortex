package uk.cg0.vortex.json

interface JsonEncodable {
    fun encodeToJson(): HashMap<*, *>
    fun decodeFromJson()
}