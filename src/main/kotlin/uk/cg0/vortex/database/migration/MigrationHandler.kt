package uk.cg0.vortex.database.migration

import uk.cg0.vortex.database.DatabaseRow
import uk.cg0.vortex.models.Migration
import java.lang.Exception

class MigrationHandler {
    fun migrate(migrations: ArrayList<DatabaseMigration>) {
        var processedMigrations = ArrayList<DatabaseRow>()

        // Will throw error if table doesn't exist as it may if we never ran the migration to create it
        try {
            processedMigrations = Migration().get().toArrayList()
        } catch (exception: Exception) {

        }
        var batch = 0

        if (processedMigrations.isNotEmpty()) {
            batch = processedMigrations.last()["batch_id"]!!.toInt()
        }

        for (migration in migrations) {
            val className = migration::class.simpleName.toString()
            if (!processedMigrations.contains("class_name", className)) {
                try {
                    migration.up()
                    Migration().insert(
                        "class_name", className,
                        "batch_id", batch.toString()
                    )
                    println("$className: Success")
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("$className: Fail")
                }
            }
        }
    }

    private fun ArrayList<DatabaseRow>.contains(key: String, value: String): Boolean {
        for (row in this) {
            if (row[key] == value) {
                return true
            }
        }
        return false
    }
}