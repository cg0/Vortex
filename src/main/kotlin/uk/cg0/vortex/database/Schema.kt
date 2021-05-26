package uk.cg0.vortex.database

import uk.cg0.vortex.Vortex
import uk.cg0.vortex.database.migration.Blueprint
import uk.cg0.vortex.database.migration.tokens.SchemaToken
import uk.cg0.vortex.database.token.TokenisedDatabaseQuery

object Schema {
    /**
     * Create a table from the defined schema
     */
    fun create(tableName: String, blueprint: Blueprint) {
        val tokeniser = Blueprint.BlueprintTokeniser()
        blueprint.invoke(tokeniser)

        val query = tokenise(tableName, tokeniser.tokens, SchemaMode.CREATE)
        Vortex.database.runInsertStatement(query)
    }

    /**
     * Update a table from the defined schema
     */
    fun table(tableName: String, blueprint: Blueprint) {
        val tokeniser = Blueprint.BlueprintTokeniser()
        blueprint.invoke(tokeniser)

        val query = tokenise(tableName, tokeniser.tokens, SchemaMode.ALTER)
        Vortex.database.runInsertStatement(query)
    }

    /**
     * Drop a table from the database
     */
    fun drop(tableName: String) {
        // Since we don't need users to manually give out the tokens for dropping tables we'll do it manually
        val tokeniser = Blueprint.BlueprintTokeniser()

        Blueprint {
            it.dropTable()
        }.invoke(tokeniser)

        val query = tokenise(tableName, tokeniser.tokens, SchemaMode.DROP)
        Vortex.database.runInsertStatement(query)
    }

    private fun tokenise(tableName: String, tokens: ArrayList<SchemaToken>, mode: SchemaMode): TokenisedDatabaseQuery {
        var query = ""
        when (mode) {
            SchemaMode.CREATE -> {
                query = "CREATE TABLE `$tableName` "
            }
            SchemaMode.ALTER -> {
                query = "ALTER TABLE `$tableName`"
            }
            SchemaMode.DROP -> {
                query = "DROP TABLE `$tableName`;"
                return TokenisedDatabaseQuery(query, ArrayList())
            }
        }

        val attributes = ArrayList<String>()
        for (token in tokens) {
            attributes.add("${token.name} ${token.dataType} ${token.attributes.joinToString(" ")}")
        }
        query += "(${attributes.joinToString(", ")})"

        return TokenisedDatabaseQuery(query, ArrayList())
    }

    enum class SchemaMode {
        CREATE,
        ALTER,
        DROP
    }
}