package uk.cg0.vortex.auth

import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import uk.cg0.vortex.database.DatabaseColumn
import uk.cg0.vortex.database.DatabaseTable

class AuthenticationSystemTests {
    object User: DatabaseTable() {
        override val tableName: String
            get() = "users"
        override val primaryKey: DatabaseColumn<*>
            get() = this.id

        val id = id()
        val username = varchar("username")
        val password = varchar("password")
        val hashType = varchar("hash_type")
    }

    private val authenticationSystem = AuthenticationSystem(User.id, User.password, User.hashType,
        AuthenticationSystem.PasswordHashType.BCRYPT)

    @Before
    fun setupTable() {
        User.dropIfExists()
        User.create()
    }

    @Test
    fun `Can we insert a user into the table and check their bcrypt password matches`() {
        User.insert {
            it[User.username] = "dave"
            it[User.password] = authenticationSystem.hashPassword("hunter12".toByteArray())
            it[User.hashType] = "BCRYPT"
        }

        val user = User.where(User.username, "dave").first()
        assertTrue(authenticationSystem.checkPassword(user, "hunter12".toByteArray()))
    }

    @Test
    fun `Can we insert a user into the table and check their bcrypt password doesn't match`() {
        User.insert {
            it[User.username] = "dave"
            it[User.password] = authenticationSystem.hashPassword("hunter12".toByteArray())
            it[User.hashType] = "BCRYPT"
        }

        val user = User.where(User.username, "dave").first()
        assertFalse(authenticationSystem.checkPassword(user, "hunter24".toByteArray()))
    }

    @Test
    fun `Can we insert a user with a cleartext password and have it auto update to our preferred type when we auth`() {
        User.insert {
            it[User.username] = "dave"
            it[User.password] = "hunter12"
            it[User.hashType] = "CLEARTEXT"
        }

        val user = User.where(User.username, "dave").first()
        assertTrue(authenticationSystem.checkPassword(user, "hunter12".toByteArray()))

        user.update()
        assertEquals("BCRYPT", user[User.hashType])
    }

    @Test
    fun `Can we insert a user with a cleartext password and make sure it stays the same if we verify the wrong password`() {
        User.insert {
            it[User.username] = "dave"
            it[User.password] = "hunter12"
            it[User.hashType] = "CLEARTEXT"
        }

        val user = User.where(User.username, "dave").first()
        assertFalse(authenticationSystem.checkPassword(user, "hunter24".toByteArray()))

        user.update()
        assertEquals("CLEARTEXT", user[User.hashType])
    }

}