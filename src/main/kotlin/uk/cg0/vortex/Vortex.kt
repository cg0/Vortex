package uk.cg0.vortex

import uk.cg0.vortex.config.Config
import uk.cg0.vortex.controller.Controller
import uk.cg0.vortex.database.Database
import uk.cg0.vortex.database.migration.MigrationHandler
import uk.cg0.vortex.webserver.routing.RouteDirectory
import uk.cg0.vortex.webserver.routing.RouteNode
import uk.cg0.vortex.webserver.routing.RoutingEngine

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

    fun getVersion(): String {
        return "0.0.1";
    }
}