package uk.cg0.vortex.webserver

import uk.cg0.vortex.webserver.route.Route

/**
 * Really basic routing engine that just takes string key to route value
 */
class RoutingEngine {
    val routes = HashMap<String, Route>()

    operator fun get(key: String): Route? {
        return routes[key]
    }

    operator fun set(key: String, value: Route) {
        routes[key] = value
    }
}