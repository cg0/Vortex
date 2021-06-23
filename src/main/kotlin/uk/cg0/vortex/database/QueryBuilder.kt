package uk.cg0.vortex.database

import uk.cg0.vortex.Vortex
import uk.cg0.vortex.database.conditionals.Conditional
import uk.cg0.vortex.database.conditionals.ConditionalType
import uk.cg0.vortex.database.conditionals.ExternalPredicate
import uk.cg0.vortex.database.conditionals.InlinePredicate
import kotlin.collections.ArrayList

class QueryBuilder(private val table: DatabaseTable) {
    private var mode = DatabaseMode.SELECT
    private var selectFields = ArrayList<DatabaseColumn<*>>()
    private val conditionals = ArrayList<Conditional>()
    private var limit: Int? = null
    private val columnData = HashMap<DatabaseColumn<*>, Any>()

    private fun buildSelectFields(): ArrayList<DatabaseColumn<*>> {
        selectFields = Vortex.database.getFieldsFromTableModel(table, Database.DatabaseFieldFilter.DIRECT_FIELDS)
        return selectFields
    }


    fun select(vararg fields: DatabaseColumn<*>): QueryBuilder {
        selectFields.clear()

        for (field in fields) {
            selectFields.add(field)
        }

        return this
    }

    fun where(key: DatabaseColumn<*>, value: Any): QueryBuilder {
        conditionals.add(Conditional(ConditionalType.AND, InlinePredicate(key, value, "=")))
        return this
    }


    fun where(key: DatabaseColumn<*>, condition: String, value: Any): QueryBuilder {
        conditionals.add(Conditional(ConditionalType.AND, InlinePredicate(key, value, condition)))
        return this
    }

    fun where(embeddedQueryBuilder: EmbeddedQueryBuilder): QueryBuilder {
        val queryBuilder = QueryBuilder(this.table)
        embeddedQueryBuilder.invoke(queryBuilder)
        conditionals.add(Conditional(ConditionalType.AND, ExternalPredicate(queryBuilder.conditionals)))
        return this
    }

    fun orWhere(key: DatabaseColumn<*>, value: Any): QueryBuilder {
        conditionals.add(Conditional(ConditionalType.OR, InlinePredicate(key, value, "=")))
        return this
    }

    fun orWhere(key: DatabaseColumn<*>, condition: String, value: Any): QueryBuilder {
        conditionals.add(Conditional(ConditionalType.OR, InlinePredicate(key, value, condition)))
        return this
    }

    fun orWhere(embeddedQueryBuilder: EmbeddedQueryBuilder): QueryBuilder {
        val queryBuilder = QueryBuilder(this.table)
        embeddedQueryBuilder.invoke(queryBuilder)
        conditionals.add(Conditional(ConditionalType.OR, ExternalPredicate(queryBuilder.conditionals)))
        return this
    }

    fun insert(mysqlDataBuilder: SqlDataBuilder): QueryBuilder {
        mode = DatabaseMode.INSERT
        val mysqlDataMap = SqlDataBuilder.SqlDataMapBuilder()
        mysqlDataBuilder.invoke(mysqlDataMap)

        for (item in mysqlDataMap.dataMap) {
            columnData[item.key] = item.value
        }
        return this
    }

    fun update(mysqlDataBuilder: SqlDataBuilder): QueryBuilder {
        mode = DatabaseMode.UPDATE
        val mysqlDataMap = SqlDataBuilder.SqlDataMapBuilder()
        mysqlDataBuilder.invoke(mysqlDataMap)

        for (item in mysqlDataMap.dataMap) {
            columnData[item.key] = item.value
        }
        return this
    }

    fun count(): Long {
        mode = DatabaseMode.COUNT
        return get().first()[".COUNT(*)"] as Long
    }

    fun get(): DatabaseResult {
        return DatabaseResult(Vortex.database.executeQuery(this.toDatabaseQuery()), table)
    }

    fun first(): DatabaseRow {
        // Gets first result of query and set limit of 1
        this.limit(1)
        return get().first()
    }

    /**
     * Returns the SQL query string and the data passed to the prepared query
     */
    fun toDatabaseQuery(): DatabaseQuery {
        var query = ""
        val data = ArrayList<Any>()
        when (mode) {
            DatabaseMode.SELECT -> {
                if (selectFields.isEmpty()) {
                    buildSelectFields()
                }
                query = "SELECT ${selectFields.joinToString(", ")} FROM `${table.tableName}`" + buildWhereConditionals(data)
                if (limit != null) {
                    query += " LIMIT $limit"
                }
            }
            DatabaseMode.UPDATE -> {
                val encodedSchema = ArrayList<String>()

                for (column in columnData) {
                    encodedSchema.add("${column.key.columnName}=?")
                    data.add(column.value)
                }

                query = "UPDATE `${table.tableName}` SET ${encodedSchema.joinToString(",")}" + buildWhereConditionals(data)
            }
            DatabaseMode.INSERT -> {
                val columnNames = ArrayList<String>()

                for (column in columnData) {
                    columnNames.add(column.key.columnName)
                    data.add(column.value)
                }

                query = "INSERT INTO `${table.tableName}` (${columnNames.joinToString(",")}) VALUES (${"?,".repeat(columnNames.size).removeSuffix(",")})"
            }
            DatabaseMode.COUNT -> {
                query = "SELECT COUNT(*) FROM `${table.tableName}`" + buildWhereConditionals(data)
            }
        }

        return DatabaseQuery(query, data)
    }

    fun limit(limit: Int): QueryBuilder {
        this.limit = limit
        return this
    }

    /**
     * Returns the SQL string without any data attached
     *
     * @return SQL query
     */
    fun toSql(): String {
        return toDatabaseQuery().query
    }

    private fun buildWhereConditionals(data: ArrayList<Any>,
                                       includeWhere: Boolean = true,
                                       conditionals: ArrayList<Conditional> = this.conditionals): String {
        if (conditionals.isEmpty()) {
            return ""
        }
        var where = ""

        if (includeWhere) {
            where += " WHERE "
        }
        for (conditional in conditionals.withIndex()) {
            if (conditional.index > 0) {
                // Only handle AND/OR after first index
                where += when (conditional.value.conditionalType) {
                    ConditionalType.AND -> " AND "
                    ConditionalType.OR -> " OR "
                }
            }

            if (conditional.value.predicate is InlinePredicate) {
                val inlinePredicate = conditional.value.predicate as InlinePredicate
                where += "${inlinePredicate.column} ${inlinePredicate.condition} ?"
                data.add(inlinePredicate.value)
            } else if (conditional.value.predicate is ExternalPredicate) {
                val externalPredicate = conditional.value.predicate as ExternalPredicate
                val whereConditions = buildWhereConditionals(data, false, externalPredicate.conditionals)

                where += "($whereConditions)"
            }
        }

        return where
    }

    enum class DatabaseMode {
        SELECT,
        UPDATE,
        INSERT,
        COUNT
    }

    fun interface SqlDataBuilder {
        fun invoke(sqlDataMapBuilder: SqlDataMapBuilder)

        class SqlDataMapBuilder {
            val dataMap = HashMap<DatabaseColumn<*>, Any>()

            operator fun set(key: DatabaseColumn<*>, value: Any) {
                dataMap[key] = value
            }
        }
    }
}