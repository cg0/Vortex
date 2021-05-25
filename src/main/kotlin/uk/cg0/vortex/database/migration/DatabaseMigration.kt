package uk.cg0.vortex.database.migration

interface DatabaseMigration {
    fun up()
    fun down()
}