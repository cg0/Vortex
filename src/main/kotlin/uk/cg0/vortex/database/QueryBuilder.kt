package uk.cg0.vortex.database

import uk.cg0.vortex.Vortex
import uk.cg0.vortex.database.exceptions.DatabaseTokenPositionMismatchException
import uk.cg0.vortex.database.exceptions.IllegalDatabaseTokenUnderModeException
import uk.cg0.vortex.database.exceptions.UnprocessableDatabaseTokenException
import uk.cg0.vortex.database.token.SqlToken
import uk.cg0.vortex.database.token.SqlTokenData
import uk.cg0.vortex.database.token.TokenisedDatabaseQuery

class QueryBuilder(val tableName: String) {
    private val tokens = ArrayList<SqlTokenData>()

    fun select(vararg fields: String): QueryBuilder {
        tokens.add(SqlTokenData(SqlToken.SELECT, fields.toList() as ArrayList<Any>))
        return this
    }

    fun where(key: String, value: String): QueryBuilder {
        tokens.add(SqlTokenData(SqlToken.WHERE, arrayListOf(key, value)))
        return this
    }

    fun orWhere(key: String, value: String): QueryBuilder {
        tokens.add(SqlTokenData(SqlToken.OR_WHERE, arrayListOf(key, value)))
        return this
    }

    fun insert(values: HashMap<String, String>): QueryBuilder {
        tokens.add(SqlTokenData(SqlToken.INSERT, arrayListOf(values)))
        return this
    }

    fun update(vararg values: String): QueryBuilder {
        val hashMap = HashMap<String, String>()

        for (i in values.indices step 2) {
            hashMap[values[i]] = values[i + 1]
        }

        tokens.add(SqlTokenData(SqlToken.UPDATE, arrayListOf(hashMap)))
        return this
    }

    /**
     * Run through the tokens reversed in order to build up a query to expose to a database query
     */
    private fun tokenise(): TokenisedDatabaseQuery {
        var query = ""
        val attributes = ArrayList<String>()

        var whereAlreadyGenerated = false
        var mode = DatabaseMode.NOT_SET

        for (token in tokens) {
            when (token.sqlToken) {
                SqlToken.SELECT -> {
                    if (query.isNotEmpty()) {
                        throw DatabaseTokenPositionMismatchException(token.sqlToken)
                    }
                    if (mode != DatabaseMode.NOT_SET) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    mode = DatabaseMode.SELECT

                    val selectAttributes = token.attributes.joinToString(", ")
                    query += "SELECT $selectAttributes FROM `$tableName` "
                }
                SqlToken.INSERT -> {
                    if (query.isNotEmpty()) {
                        throw DatabaseTokenPositionMismatchException(token.sqlToken)
                    }
                    if (mode != DatabaseMode.NOT_SET) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    val data = token.attributes[0] as HashMap<String, String>
                    val keys = data.keys
                    val variables = ArrayList<String>()

                    mode = DatabaseMode.INSERT

                    for (value in data) {
                        variables.add("?")
                        attributes.add(value.value)
                    }

                    query += "INSERT INTO `$tableName` (${keys.joinToString(", ")}) VALUES (${variables.joinToString(", ")})"
                }
                SqlToken.UPDATE -> {
                    if (query.isNotEmpty()) {
                        throw DatabaseTokenPositionMismatchException(token.sqlToken)
                    }
                    if (mode != DatabaseMode.NOT_SET) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    val data = token.attributes[0] as HashMap<String, String>
                    val values = ArrayList<String>()

                    mode = DatabaseMode.UPDATE

                    for (value in data) {
                        values.add("`${value.key}`='${value.value}'")
                    }

                    query += "UPDATE `$tableName` SET ${values.joinToString(", ")} "
                }
                SqlToken.WHERE -> {
                    if (mode == DatabaseMode.NOT_SET) {
                        // If the database mode isn't set we prepare the select all beforehand
                        tokens.add(SqlTokenData(SqlToken.SELECT, arrayListOf("*")))
                        mode = DatabaseMode.SELECT
                    }
                    if (mode == DatabaseMode.INSERT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += if (whereAlreadyGenerated) {
                        "AND ${token.attributes[0]} = ? "
                    } else {
                        whereAlreadyGenerated = true
                        "WHERE ${token.attributes[0]} = ? "
                    }
                    attributes.add(token.attributes[1].toString())
                }
                SqlToken.WHERE_EXTENDED -> {
                    if (mode == DatabaseMode.NOT_SET) {
                        // If the database mode isn't set we prepare the select all beforehand
                        tokens.add(SqlTokenData(SqlToken.SELECT, arrayListOf("*")))
                        mode = DatabaseMode.SELECT
                    }
                    if (mode == DatabaseMode.INSERT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += if (whereAlreadyGenerated) {
                        "AND ${token.attributes[0]} ${token.attributes[1]} ? "
                    } else {
                        whereAlreadyGenerated = true
                        "WHERE ${token.attributes[0]} ${token.attributes[1]} ? "
                    }
                    attributes.add(token.attributes[2].toString())
                }
                SqlToken.WHERE_NOT -> {
                    if (mode == DatabaseMode.NOT_SET) {
                        // If the database mode isn't set we prepare the select all beforehand
                        tokens.add(SqlTokenData(SqlToken.SELECT, arrayListOf("*")))
                        mode = DatabaseMode.SELECT
                    }
                    if (mode == DatabaseMode.INSERT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += if (whereAlreadyGenerated) {
                        "AND ${token.attributes[0]} NOT ? "
                    } else {
                        whereAlreadyGenerated = true
                        "WHERE ${token.attributes[0]} NOT ? "
                    }
                    attributes.add(token.attributes[1].toString())
                }
                SqlToken.WHERE_NOT_EXTENDED -> {
                    if (mode == DatabaseMode.NOT_SET) {
                        // If the database mode isn't set we prepare the select all beforehand
                        tokens.add(SqlTokenData(SqlToken.SELECT, arrayListOf("*")))
                        mode = DatabaseMode.SELECT
                    }
                    if (mode == DatabaseMode.INSERT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += if (whereAlreadyGenerated) {
                        "AND ${token.attributes[0]} NOT ${token.attributes[1]} ? "
                    } else {
                        whereAlreadyGenerated = true
                        "WHERE ${token.attributes[0]} NOT ${token.attributes[1]} ? "
                    }
                    attributes.add(token.attributes[2].toString())
                }
                SqlToken.OR_WHERE -> {
                    if (!whereAlreadyGenerated) {
                        throw DatabaseTokenPositionMismatchException(token.sqlToken)
                    }
                    if (mode == DatabaseMode.NOT_SET) {
                        // If the database mode isn't set we prepare the select all beforehand
                        tokens.add(SqlTokenData(SqlToken.SELECT, arrayListOf("*")))
                        mode = DatabaseMode.SELECT
                    }
                    if (mode == DatabaseMode.INSERT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += "OR ${token.attributes[0]} = ? "
                    attributes.add(token.attributes[1].toString())
                }
                SqlToken.OR_WHERE_EXTENDED -> {
                    if (!whereAlreadyGenerated) {
                        throw DatabaseTokenPositionMismatchException(token.sqlToken)
                    }
                    if (mode == DatabaseMode.NOT_SET) {
                        // If the database mode isn't set we prepare the select all beforehand
                        tokens.add(SqlTokenData(SqlToken.SELECT, arrayListOf("*")))
                        mode = DatabaseMode.SELECT
                    }
                    if (mode == DatabaseMode.INSERT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += "OR ${token.attributes[0]} ${token.attributes[1]} ?"
                    attributes.add(token.attributes[2].toString())
                }
                SqlToken.OR_WHERE_NOT -> {
                    if (!whereAlreadyGenerated) {
                        throw DatabaseTokenPositionMismatchException(token.sqlToken)
                    }
                    if (mode == DatabaseMode.NOT_SET) {
                        // If the database mode isn't set we prepare the select all beforehand
                        tokens.add(SqlTokenData(SqlToken.SELECT, arrayListOf("*")))
                        mode = DatabaseMode.SELECT
                    }
                    if (mode == DatabaseMode.INSERT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += "OR WHERE ${token.attributes[0]} NOT ? $query"
                    attributes.add(token.attributes[1].toString())
                }
                SqlToken.OR_WHERE_NOT_EXTENDED -> {
                    if (!whereAlreadyGenerated) {
                        throw DatabaseTokenPositionMismatchException(token.sqlToken)
                    }
                    if (mode == DatabaseMode.NOT_SET) {
                        // If the database mode isn't set we prepare the select all beforehand
                        tokens.add(SqlTokenData(SqlToken.SELECT, arrayListOf("*")))
                        mode = DatabaseMode.SELECT
                    }
                    if (mode == DatabaseMode.INSERT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += "OR ${token.attributes[0]} NOT ${token.attributes[1]} ?"
                    attributes.add(token.attributes[2].toString())
                }
                SqlToken.LIMIT -> {
                    if (mode != DatabaseMode.SELECT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += "LIMIT ${token.attributes[0]} "
                }
                SqlToken.OFFSET -> {
                    if (mode != DatabaseMode.SELECT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += "OFFSET ${token.attributes[0]} "
                }
                SqlToken.ORDER_BY -> {
                    if (mode != DatabaseMode.SELECT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += "ORDER BY ${token.attributes[0]} "
                }
                SqlToken.ORDER_BY_EXTENDED -> {
                    if (mode != DatabaseMode.SELECT) {
                        throw IllegalDatabaseTokenUnderModeException(token.sqlToken, mode)
                    }

                    query += "ORDER BY ${token.attributes[0]} ${token.attributes[1]} "
                }

                else -> throw UnprocessableDatabaseTokenException(token.sqlToken)
            }
        }

        return TokenisedDatabaseQuery("$query;", attributes)
    }

    fun get(): DatabaseResult {
        val tokenisedQuery = tokenise()
        return DatabaseResult(Vortex.database.runQueryStatement(tokenisedQuery))
    }

    fun execute(): Boolean {
        val tokenisedQuery = tokenise()
        return Vortex.database.runInsertStatement(tokenisedQuery)
    }

    /**
     * Returns the tokenised SQL string that doesn't contain any data attached
     *
     * @return SQL query
     */
    fun toSql(): String {
        return tokenise().query
    }

    private fun setupSelect() {
        tokens.add(SqlTokenData(SqlToken.SELECT, arrayListOf("*")))
    }

    enum class DatabaseMode {
        SELECT,
        UPDATE,
        INSERT,
        NOT_SET
    }
}