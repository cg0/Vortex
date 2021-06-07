package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class ContainerNode(override val parent: RouteNode?, override val routeKey: String): RouteNode {
    val nodes = HashMap<String, RouteNode>()

    override operator fun get(httpVerb: HttpVerb, route: ArrayList<String>): ControllerFunction? {
        route.removeAt(0)
        val file = if (route.isEmpty() || route.first().isEmpty()) {
            "index"
        } else {
            route.first()
        }

        val node = nodes[file] ?: nodes["*"] ?: return null
        if (node is ContainerNode) {
            return node[httpVerb, route]
        } else if (node is TailNode) {
            return node[httpVerb, route]
        }

        return null
    }

    override operator fun set(routeList: ArrayList<RouteNode>, controllerFunction: ControllerFunction) {
        val node = routeList.removeFirst()

        if (node.routeKey !in nodes.keys) {
            nodes[node.routeKey] = node
        }

        nodes[node.routeKey]?.set(routeList, controllerFunction)
    }
}