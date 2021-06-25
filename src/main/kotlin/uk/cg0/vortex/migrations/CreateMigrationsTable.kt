package uk.cg0.vortex.migrations

import uk.cg0.vortex.database.migration.DatabaseMigration

class CreateMigrationsTable: DatabaseMigration {
    override fun up() {
//        Schema.create("migrations") {
//            it.id()
//            it.string("class_name")
//            it.integer("batch_id")
//            it.timestamps()
//        }
    }

    override fun down() {
//        Schema.drop("migrations")
    }
}