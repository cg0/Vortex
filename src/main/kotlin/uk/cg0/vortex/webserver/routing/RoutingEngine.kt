package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.enum.HttpVerb
import java.rmi.UnexpectedException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Really basic routing engine that just takes string key to route value
 */
class RoutingEngine {
    private val routes = HashMap<String, DomainContainerNode>()
    private val errors = HashMap<String, EnumMap<HttpStatus, ControllerFunction>>()

    operator fun set(httpVerb: HttpVerb, path: String, controllerFunction: ControllerFunction) {
        val splitPath = path.split("/")
        val domain = splitPath.first()
        val routeList = ArrayList<RouteNode>()

        routeList.add(DomainContainerNode(domain.ifEmpty { "*" }))

        for (part in splitPath.drop(1)) {
            if (part.startsWith("{") && part.endsWith("}")) {
                routeList.add(VariableContainerNode(routeList.last(), part.removeSurrounding("{", "}"), "*"))
            } else {
                routeList.add(ContainerNode(routeList.last(), part.ifBlank { "*" }))
            }
        }

        routeList.add(ControllerContainerNode(routeList.last()))

        routeList.add(ControllerTailNode(routeList.last(), httpVerb.name, controllerFunction))

        this[routeList] = controllerFunction
    }

    operator fun set(routeList: ArrayList<RouteNode>, controllerFunction: ControllerFunction) {
        val domainNode = routeList.removeFirst()

        if (domainNode !is DomainContainerNode) {
            throw UnexpectedException("The root node for the routing engine HAS to be a DomainContainerNode")
        }

        if (domainNode.routeKey !in routes) {
            routes[domainNode.routeKey] = domainNode
        }

        routes[domainNode.routeKey]?.set(routeList, controllerFunction)
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