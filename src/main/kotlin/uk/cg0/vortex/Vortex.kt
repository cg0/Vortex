package uk.cg0.vortex

import uk.cg0.vortex.config.Config
import uk.cg0.vortex.controller.Controller
import uk.cg0.vortex.database.Database
import uk.cg0.vortex.database.migration.MigrationHandler
import uk.cg0.vortex.migrations.CreateMigrationsTable
import uk.cg0.vortex.webserver.routing.RouteDirectory
import uk.cg0.vortex.webserver.routing.RouteNode
import uk.cg0.vortex.webserver.routing.RoutingEngine
import uk.cg0.vortex.webserver.thread.HttpServerThread

object Vortex {
    val routingEngine = RoutingEngine()
    val config = Config()
    val database: Database
    val migrationHandler = MigrationHandler()

    init {
        config.load(".env")
        database = Database(config["DB_HOST"],
            config["DB_DATABASE"],
            config["DB_USERNAME"],
            config["DB_PASSWORD"])
    }

    /**
     * Starts the Vortex web framework
     *
     * This will start the HTTP server and connect up the command processor
     *
     * @param args Command line arguments to be passed to the command processor
     */
    fun start(args: Array<String>) {
        val logo = arrayListOf(
            "____   ____            __                 ",
            "\\   \\ /   /___________/  |_  ____ ___  ___",
            " \\   Y   /  _ \\_  __ \\   __\\/ __ \\\\  \\/  /",
            "  \\     (  <_> )  | \\/|  | \\  ___/ >    < ",
            "   \\___/ \\____/|__|   |__|  \\___  >__/\\_ \\",
            "                                \\/      \\/"
        )

        for (line in logo) {
            println(line)
        }

        migrationHandler.migrate(arrayListOf(CreateMigrationsTable()))
        HttpServerThread().run()
    }

    fun getVersion(): String {
        return "0.0.1";
    }
}