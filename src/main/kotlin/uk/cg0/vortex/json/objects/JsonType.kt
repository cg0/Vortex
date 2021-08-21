package uk.cg0.vortex.json.objects

import uk.cg0.vortex.database.DatabaseRow
import uk.cg0.vortex.database.DatabaseTable
import kotlin.reflect.KClass

abstract class JsonType {
    // Boolean
    open fun toBoolean(): Boolean? {
        if (this is JsonBoolean) {
            return toBoolean()
        }
        return null
    }

    // Numbers
    open fun toByte(): Byte? {
        if (this is JsonNumber) {
            return toByte()
        }
        return null
    }

    open fun toShort(): Short? {
        if (this is JsonNumber) {
            return toShort()
        }
        return null
    }

    open fun toInt(): Int? {
        if (this is JsonNumber) {
            return toInt()
        }
        return null
    }

    open fun toLong(): Long? {
        if (this is JsonNumber) {
            return toLong()
        }
        return null
    }

    open fun toFloat(): Float? {
        if (this is JsonNumber) {
            return toFloat()
        }
        return null
    }

    open fun toDouble(): Double? {
        if (this is JsonNumber) {
            return toDouble()
        }
        return null
    }

    // Objects
    open fun toDatabaseRow(table: DatabaseTable): DatabaseRow? {
        if (this is JsonObject) {
            return toDatabaseRow(table)
        }
        return null
    }

    open fun toHashMap(): HashMap<String, JsonType>? {
        if (this is JsonObject) {
            return toHashMap()
        }
        return null
    }

    open fun <T> toHashMapOf(): HashMap<String, T>? {
        if (this is JsonObject) {
            return toHashMapOf()
        }
        return null
    }

    override fun toString(): String {
        return if (this is JsonString) {
            this.toString()
        } else {
            super.toString()
        }
    }

    // Lists
    open fun toArrayList(): ArrayList<JsonType>? {
        if (this is JsonList) {
            return toArrayList()
        }
        return null
    }

    open fun <T> toArrayListOf(): ArrayList<T>? {
        if (this is JsonList) {
            return toArrayListOf()
        }
        return null
    }

    // Arrays
    open fun toArray(): Array<JsonType>? {
        if (this is JsonList) {
            return toArray()
        }
        return null
    }

    inline fun <reified T> toArrayOf(): Array<T>? {
        if (this is JsonList) {
            return toArrayListOf<T>().toTypedArray()
        }
        return null
    }

    // Generic
    open fun toAny(): Any? {
        return this.toAny()
    }

    fun isNull(): Boolean {
        return this is JsonNull
    }
}