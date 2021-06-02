package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Really basic routing engine that just takes string key to route value
 */
class RoutingEngine {
    private val routes = HashMap<String, RouteDirectory>()

    fun addRoute(httpVerb: HttpVerb, path: String, controllerFunction: ControllerFunction) {
        addRoute(httpVerb, "*", path, controllerFunction)
    }

    fun addRoute(httpVerb: HttpVerb, domain: String, path: String, controllerFunction: ControllerFunction) {
        val splitPath = ArrayList<String>(path.split("/"))
        splitPath.removeAt(0)

        if (domain !in routes.keys) {
            routes[domain] = RouteDirectory(null)
        }

        routes[domain]?.addRoute(httpVerb, splitPath, controllerFunction)
    }

    operator fun get(httpVerb: HttpVerb, path: String): ControllerFunction? {
        val splitPath = ArrayList<String>(path.split("/"))
        if (splitPath.first().startsWith("http")) {
            val domain = splitPath[0]
            splitPath.removeAt(0)
            return routes[domain]?.get(httpVerb, splitPath)
        }

        splitPath.removeAt(0)
        return routes["*"]?.get(httpVerb, splitPath)
    }
}