package uk.cg0.vortex.models

import uk.cg0.vortex.database.DatabaseModel

class Migration: DatabaseModel() {
    override val tableName: String
        get() = "migrations"
}