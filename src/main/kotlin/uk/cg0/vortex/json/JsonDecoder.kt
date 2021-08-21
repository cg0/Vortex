package uk.cg0.vortex.json

import uk.cg0.vortex.json.objects.*

class JsonDecoder {
    fun decode(json: String): JsonType {
        // This decoding works by treating the whole document a flat document treating any children of keys as just a
        // string value, this then attempts to route it back through the same decode function to decode the next layer

        return when {
            json.startsWith("{") -> {
                decodeObject(json)
            }
            json.startsWith("[") -> {
                decodeList(json)
            }
            else -> {
                // This is not a document but rather a static value
                decodeValue(json)
            }
        }
    }

    private fun decodeObject(json: String): JsonObject {
        var layer = 0
        var key = ""
        var value = ""
        val values = HashMap<String, JsonType>()

        var mode = JsonParsingState.TOP_LAYER
        for (char in json) {
            when (mode) {
                JsonParsingState.TOP_LAYER -> {
                    if (char == '{') {
                        layer += 1
                        mode = JsonParsingState.DATA_LEADUP
                    }
                }
                JsonParsingState.DATA_LEADUP -> {
                    if (char != ' ' && char != '\t' && char != '\n' && char != '\r') {
                        key += char
                        mode = JsonParsingState.KEY
                    }
                }
                JsonParsingState.KEY -> {
                    if (char == ':') {
                        mode = JsonParsingState.VALUE_LEADUP
                    } else {
                        key += char
                    }
                }
                JsonParsingState.VALUE_LEADUP -> {
                    if (char != ' ' && char != '\t' && char != '\n' && char != '\r') {
                        value += char
                        if (char == '{') {
                            layer += 1
                        } else if (char == '}') {
                            layer -= 1
                        }
                        mode = JsonParsingState.VALUE
                    }
                }
                JsonParsingState.VALUE -> {
                    if (char == '{') {
                        layer += 1
                    } else if (char == '}') {
                        layer -= 1
                    }

                    if ((char == ',' && layer == 1) || (char == '}' && layer == 0)) {
                        mode = JsonParsingState.DATA_LEADUP
                        values[key.removeSurrounding("\"")] = decode(value)
                        key = ""
                        value = ""
                    } else {
                        value += char
                    }
                }
            }
        }

        if (key.isNotEmpty()) {
            values[key] = decode(value)
        }

        return JsonObject(values)
    }

    private fun decodeList(json: String): JsonList {
        var layer = 0
        var value = ""
        val values = ArrayList<JsonType>()

        var mode = JsonParsingState.TOP_LAYER
        for (char in json) {
            println("$char: $mode")
            when (mode) {
                JsonParsingState.TOP_LAYER -> {
                    if (char == '[') {
                        layer += 1
                        mode = JsonParsingState.VALUE_LEADUP
                    }
                }
                JsonParsingState.VALUE_LEADUP -> {
                    if (char != ' ' && char != '\t') {
                        value += char
                        mode = JsonParsingState.VALUE
                    }
                }
                JsonParsingState.VALUE -> {
                    if (char == '[') {
                        layer += 1
                    } else if (char == ']') {
                        layer -= 1
                    }

                    if ((char == ',' && layer == 1) || (char == ']' && layer == 0)) {
                        mode = JsonParsingState.VALUE
                        values.add(decode(value))
                        value = ""
                    } else {
                        value += char
                    }
                }
                else -> {
                    throw NotImplementedError("Unknown JSON parsing state $mode")
                }
            }
        }

        return JsonList(values)
    }


    private fun decodeValue(json: String): JsonType {
        val trimmedJson = json.trim()
        if (trimmedJson.startsWith("\"") && trimmedJson.endsWith("\"")) {
            return JsonString(trimmedJson.removeSurrounding("\""))
        }

        if (trimmedJson == "true" || trimmedJson == "false") {
            return JsonBoolean(trimmedJson == "true")
        }

        if (trimmedJson == "null") {
            return JsonNull()
        }

        val regex = Regex("^[1-9]\\d*(\\.\\d+)?\$")
        if (trimmedJson.matches(regex)) {
            return JsonNumber(trimmedJson.toDouble())
        }

        throw NotImplementedError("Unable to parse json value \"$trimmedJson\"")
    }


    private enum class JsonParsingState {
        TOP_LAYER,
        DATA_LEADUP,
        KEY,
        VALUE_LEADUP,
        VALUE
    }
}