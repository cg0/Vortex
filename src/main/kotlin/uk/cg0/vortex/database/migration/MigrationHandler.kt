package uk.cg0.vortex.database.migration

import uk.cg0.vortex.models.Migration
import java.lang.Exception

class MigrationHandler {
    fun migrate(migrations: ArrayList<DatabaseMigration>) {
        val processedMigrations = Migration().get()
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
                    println("$className: Fail")
                }
            }
        }
    }
}