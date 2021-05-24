package uk.cg0.vortex.test

import uk.cg0.vortex.database.DatabaseModel

class TestTable: DatabaseModel() {
    override val tableName: String
        get() = "test"

}