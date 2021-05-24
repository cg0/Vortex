package uk.cg0.vortex

import uk.cg0.vortex.webserver.RoutingEngine
import uk.cg0.vortex.webserver.route.NotFoundRoute
import uk.cg0.vortex.webserver.route.Route

class Vortex {
    companion object {
        val routingEngine = RoutingEngine()

        fun getVersion(): String {
            return "0.0.1";
        }

        fun getRoute(key: String): Route {
            return routingEngine[key] ?: NotFoundRoute()
        }
    }
}