package uk.cg0.vortex.json.objects

class JsonNumber(private val data: Double): JsonType() {
    override fun toByte(): Byte {
        return data.toInt().toByte()
    }

    override fun toShort(): Short {
        return data.toInt().toShort()
    }

    override fun toInt(): Int {
        return data.toInt()
    }

    override fun toDouble(): Double {
        return data
    }

    override fun toFloat(): Float {
        return data.toFloat()
    }

    override fun toLong(): Long {
        return data.toLong()
    }

    override fun toAny(): Any {
        return data
    }
}