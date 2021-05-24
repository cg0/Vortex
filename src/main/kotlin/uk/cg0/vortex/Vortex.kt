package uk.cg0.vortex

import uk.cg0.vortex.config.Config
import uk.cg0.vortex.database.Database
import uk.cg0.vortex.database.DatabaseModel
import uk.cg0.vortex.webserver.RoutingEngine
import uk.cg0.vortex.webserver.route.NotFoundRoute
import uk.cg0.vortex.webserver.route.Route

class Vortex {
    companion object {
        val routingEngine = RoutingEngine()
        val config = Config()
        val database: Database

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

        fun getRoute(key: String): Route {
            return routingEngine[key] ?: NotFoundRoute()
        }
    }
}