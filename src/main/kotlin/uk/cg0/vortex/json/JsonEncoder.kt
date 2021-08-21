package uk.cg0.vortex.json

import uk.cg0.vortex.database.DatabaseRow
import java.lang.StringBuilder
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class JsonEncoder(private val indentationSize: Int = 2) {
    private fun writeIndentation(jsonPrintType: JsonPrintType, indentation: Int): String {
        return when (jsonPrintType) {
            JsonPrintType.MINIFIED -> ""
            JsonPrintType.PRETTY_PRINT -> "\n" + " ".repeat(indentation)
        }
    }

    fun encode(item: Any?, jsonPrintType: JsonPrintType, initialIndentation: Int = 0): String {
        return when (item) {
            null -> "null"
            is JsonEncodable -> encodeHashMap(item.encodeToJson(), jsonPrintType, initialIndentation)
            is HashMap<*, *> -> encodeHashMap(item, jsonPrintType, initialIndentation)
            is ArrayList<*> -> encodeList(item, jsonPrintType, initialIndentation)
            is Array<*> -> encodeList(item.toList(), jsonPrintType, initialIndentation)
            is Byte, is Short, is Int, is Long, is Double, is Float, is Boolean -> item.toString()
            is DatabaseRow -> encodeDatabaseRow(item, jsonPrintType, initialIndentation)
            is String -> "\"$item\""
            else -> encodeClass(item, jsonPrintType, initialIndentation)
        }
    }

    private fun encodeHashMap(hashMap: HashMap<*, *>, jsonPrintType: JsonPrintType, initialIndentation: Int): String {
        val builder = StringBuilder()
        val indentation = initialIndentation + indentationSize
        var index = 0
        val isPrettyPrint = jsonPrintType == JsonPrintType.PRETTY_PRINT

        builder.append("{")
        for (item in hashMap) {
            builder.append(writeIndentation(jsonPrintType, indentation))
            builder.append(encode(item.key, jsonPrintType, indentation))
            builder.append(":")
            if (isPrettyPrint) {
                builder.append(" ")
            }
            builder.append(encode(item.value, jsonPrintType, indentation))

            if (hashMap.size - 1 > index) {
                builder.append(",")
            }
            index +=1
        }
        builder.append(writeIndentation(jsonPrintType, initialIndentation))
        builder.append("}")

        return builder.toString()
    }

    private fun encodeList(list: List<*>, jsonPrintType: JsonPrintType, initialIndentation: Int): String {
        val builder = StringBuilder()
        val indentation = initialIndentation + indentationSize

        builder.append("[")
        for (item in list.withIndex()) {
            builder.append(writeIndentation(jsonPrintType, indentation))
            builder.append(encode(item.value, jsonPrintType, initialIndentation))

            if (list.size - 1 > item.index) {
                builder.append(",")
            }
        }
        if (jsonPrintType == JsonPrintType.PRETTY_PRINT) {
            builder.append("\n")
        }
        builder.append("]")
        return builder.toString()
    }

    private fun encodeClass(any: Any, jsonPrintType: JsonPrintType, initialIndentation: Int): String {
        val reflection = any::class
        val data = HashMap<String, Any>()

        for (item in reflection.memberProperties){
            if (item.visibility == KVisibility.PUBLIC) {
                val value = item.getter.call(any)
                if (value != null) {
                    data[item.name] = value
                }
            }
        }

        return encodeHashMap(data, jsonPrintType, initialIndentation)
    }

    private fun encodeDatabaseRow(databaseRow: DatabaseRow, jsonPrintType: JsonPrintType, initialIndentation: Int): String {
        return encodeHashMap(hashMapOf(
            "table" to databaseRow.table.tableName,
            "primaryKey" to databaseRow.table.primaryKey.toString(),
            "primaryKeyValue" to databaseRow[databaseRow.table.primaryKey]
        ), jsonPrintType, initialIndentation)
    }
}