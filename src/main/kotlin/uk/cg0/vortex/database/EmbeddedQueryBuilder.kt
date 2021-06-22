package uk.cg0.vortex.database

fun interface EmbeddedQueryBuilder {
    fun invoke(queryBuilder: QueryBuilder)
}