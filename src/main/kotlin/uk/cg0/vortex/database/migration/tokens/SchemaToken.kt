package uk.cg0.vortex.database.migration.tokens

abstract class SchemaToken {
    abstract var name: String
    abstract val dataType: String
    val attributes = arrayListOf("NOT NULL")

    fun nullable() {
        attributes.remove("NOT NULL")
    }

    fun primaryKey(): SchemaToken {
        attributes.add("PRIMARY KEY")
        return this
    }
}