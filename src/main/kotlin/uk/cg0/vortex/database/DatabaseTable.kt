package uk.cg0.vortex.database

import uk.cg0.vortex.Vortex

abstract class DatabaseTable {
    abstract val tableName: String
    abstract val primaryKey: DatabaseColumn<*>
    open val columnRenames = HashMap<String, DatabaseColumn<*>>()

    // SQL queries

    fun select(vararg fields: DatabaseColumn<*>): QueryBuilder {
        return QueryBuilder(this).select(*fields)
    }

    fun insert(sqlDataBuilder: QueryBuilder.SqlDataBuilder) {
        val query = QueryBuilder(this).insert(sqlDataBuilder).toDatabaseQuery()
        Vortex.database.execute(query)
    }

    fun update(sqlDataBuilder: QueryBuilder.SqlDataBuilder): QueryBuilder {
        return QueryBuilder(this).update(sqlDataBuilder)
    }

    fun where(key: DatabaseColumn<*>, value: Any): QueryBuilder {
        return QueryBuilder(this).where(key, value)
    }

    fun where(key: DatabaseColumn<*>, condition: String, value: Any): QueryBuilder {
        return QueryBuilder(this).where(key, condition, value)
    }

    fun where(embeddedQueryBuilder: EmbeddedQueryBuilder): QueryBuilder {
        return QueryBuilder(this).where(embeddedQueryBuilder)
    }

    fun count(): Long {
        return QueryBuilder(this).count()
    }

    fun create() {
        Vortex.database.createTable(
            *Vortex.database.getFieldsFromTableModel(
                this, Database.DatabaseFieldFilter.DIRECT_FIELDS).toTypedArray())
    }

    fun drop() {
        Vortex.database.dropTable(this.tableName)
    }

    fun dropIfExists() {
        Vortex.database.dropTableIfExists(this.tableName)
    }

    fun truncate() {
        Vortex.database.truncateTable(this.tableName)
    }

    // Create builder object
    fun queryBuilder(): QueryBuilder {
        return QueryBuilder(this)
    }

    // Quick data access

    fun get(): DatabaseResult {
        return QueryBuilder(this).get()
    }

    fun first(): DatabaseRow {
        return QueryBuilder(this).first()
    }

    fun find(key: Any): DatabaseRow {
        return this.where(this.primaryKey, key).first()
    }

    // SQL datatypes

    // Numerical
    fun integer(name: String): DatabaseColumn<Int> {
        return DatabaseColumn<Int>(this, name, "int").maxLength(11)
    }

    fun id(): DatabaseColumn<Int> {
        return integer("id").autoIncrement()
    }

    fun bit(name: String): DatabaseColumn<Byte> {
        return DatabaseColumn(this, name, "bit")
    }

    fun boolean(name: String): DatabaseColumn<Boolean> {
        return DatabaseColumn(this, name, "bit")
    }

    // Strings
    fun varchar(name: String, length: Int = 255): DatabaseColumn<String> {
        return DatabaseColumn<String>(this, name, "varchar").maxLength(length)
    }

    fun text(name: String, length: Int = Int.MAX_VALUE): DatabaseColumn<String> {
        return DatabaseColumn<String>(this, name, "text").maxLength(length)
    }

    // Magic
    fun hasMany(localKey: DatabaseColumn<Int>, foreignKey: DatabaseColumn<Int>): DatabaseRelation<DatabaseResult> {
        return DatabaseRelation(localKey, foreignKey, DatabaseRelation.RelationType.ONE_TO_MANY)
    }

    fun hasOne(localKey: DatabaseColumn<Int>, foreignKey: DatabaseColumn<Int>): DatabaseRelation<DatabaseRow> {
        return DatabaseRelation(localKey, foreignKey, DatabaseRelation.RelationType.ONE_TO_ONE)
    }
}