package uk.cg0.vortex.models

import uk.cg0.vortex.database.DatabaseColumn
import uk.cg0.vortex.database.DatabaseTable

class Migration: DatabaseTable() {
    override val tableName: String
        get() = "migrations"
    override val primaryKey: DatabaseColumn<*>
        get() = this.id

    val id = id()
}