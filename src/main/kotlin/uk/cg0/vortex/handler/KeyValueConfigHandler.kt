package uk.cg0.vortex.handler

class KeyValueConfigHandler {
    fun handleRead(lines: MutableList<String>): HashMap<String, String> {
        val data = HashMap<String, String>()

        for (line in lines) {
            val keyValueParts = line.split("=")
            data[keyValueParts[0]] = keyValueParts[1]
        }

        return data
    }

    fun handleWrite(hashMap: HashMap<String, String>): MutableList<String> {
        val data = ArrayList<String>()

        for (key in hashMap.keys) {
            data.add("$key=${hashMap[key]}")
        }

        return data
    }
}