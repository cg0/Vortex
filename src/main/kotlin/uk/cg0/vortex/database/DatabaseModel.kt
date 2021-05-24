package uk.cg0.vortex.database

abstract class DatabaseModel {
    abstract val tableName: String

    fun select(vararg fields: String): QueryBuilder {
        return QueryBuilder(tableName).select(*fields)
    }

    fun where(key: String, value: String): QueryBuilder {
        return QueryBuilder(tableName).select(key, value)
    }

    fun get(): DatabaseResult {
        return QueryBuilder(tableName).select("*").get()
    }
}