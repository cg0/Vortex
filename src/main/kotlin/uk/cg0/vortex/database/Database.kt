package uk.cg0.vortex.database

import uk.cg0.vortex.database.token.TokenisedDatabaseQuery
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class Database(host: String?, database: String?, username: String?, password: String?) {
    private val connection: Connection
    init {
        println(host)
        println(database)
        println(username)
        println(password)
        Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        connection = DriverManager
            .getConnection("jdbc:mysql://$host/$database?"
                    + "user=$username&password=$password");
    }

    private fun prepareStatement(query: TokenisedDatabaseQuery): PreparedStatement {
        val statement = connection.prepareStatement(query.query)

        println(query.query)

        for (attribute in query.attributes.withIndex()) {
            statement.setString(attribute.index + 1, attribute.value)
        }

        return statement
    }

    fun runUpdateStatement(query: TokenisedDatabaseQuery): Int {
        val statement = prepareStatement(query)

        return statement.executeUpdate()
    }

    fun runQueryStatement(query: TokenisedDatabaseQuery): ResultSet {
        val statement = prepareStatement(query)

        return statement.executeQuery()
    }
}