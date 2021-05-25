package uk.cg0.vortex.database

import uk.cg0.vortex.database.migration.Blueprint

object Schema {
    /**
     * Create a table from the defined schema
     */
    fun create(tableName: String, blueprint: Blueprint) {
        blueprint.invoke()
    }

    /**
     * Update a table from the defined schema
     */
    fun table(tableName: String, blueprint: Blueprint) {

    }

    /**
     * Drop a table from the database
     */
    fun drop(tableName: String, blueprint: Blueprint) {

    }
}