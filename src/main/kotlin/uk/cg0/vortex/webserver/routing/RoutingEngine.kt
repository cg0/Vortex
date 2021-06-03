package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Really basic routing engine that just takes string key to route value
 */
class RoutingEngine {
    private val routes = HashMap<String, RouteDirectory>()

    fun addRoute(httpVerb: HttpVerb, path: String, controllerFunction: ControllerFunction) {
        if (path.startsWith("/")) {
            addRoute(httpVerb, "*", path, controllerFunction)
        } else {
            val splitPath = path.split("/")
            addRoute(httpVerb, splitPath.first(), path, controllerFunction)
        }
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
        return this["*", httpVerb, path]
    }

    operator fun get(domain: String, httpVerb: HttpVerb, path: String): ControllerFunction? {
        if (domain !in routes.keys && routes.isNotEmpty()) {
            return this["*", httpVerb, path]
        }

        val splitPath = ArrayList<String>(path.split("/"))

        return routes[domain]?.get(httpVerb, splitPath)
    }

    override fun toString(): String {
        return routes.toString()
    }
}