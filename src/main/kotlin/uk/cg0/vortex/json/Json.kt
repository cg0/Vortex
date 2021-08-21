package uk.cg0.vortex.json

import uk.cg0.vortex.json.objects.JsonType

object Json {
    private val encoder = JsonEncoder()
    private val decoder = JsonDecoder()

    fun decode(json: String): JsonType {
        return decoder.decode(json)
    }

    fun encode(item: Any, jsonPrintType: JsonPrintType = JsonPrintType.PRETTY_PRINT): String {
        return encoder.encode(item, jsonPrintType)
    }
}