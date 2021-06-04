package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.enum.HttpVerb
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Really basic routing engine that just takes string key to route value
 */
class RoutingEngine {
    private val routes = HashMap<String, RouteDirectory>()
    private val errors = HashMap<String, EnumMap<HttpStatus, ControllerFunction>>()

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

    fun registerError(httpStatus: HttpStatus, kClass: KClass<*>, kFunction: KFunction<Unit>) {
        registerError("*", httpStatus, kFunction)
    }

    fun registerError(domain: String, httpStatus: HttpStatus, kFunction: KFunction<Unit>) {
        if (domain !in errors.keys) {
            errors[domain] = EnumMap(HttpStatus::class.java)
        }

        errors[domain]?.set(httpStatus, ControllerFunction(kFunction))
    }

    fun getError(httpStatus: HttpStatus): ControllerFunction? {
        return getError("*", httpStatus)
    }

    fun getError(domain: String, httpStatus: HttpStatus): ControllerFunction? {
        if (domain !in errors.keys) {
            return getError("*", httpStatus)
        }
        return errors[domain]?.get(httpStatus)
    }

    override fun toString(): String {
        return routes.toString()
    }

    fun clear() {
        routes.clear()
        errors.clear()
    }
}