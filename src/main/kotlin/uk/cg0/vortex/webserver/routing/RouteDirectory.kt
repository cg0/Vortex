package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class RouteDirectory(override val parent: RouteNode?): RouteNode {
    val nodes = HashMap<String, RouteNode>()

    override operator fun get(httpVerb: HttpVerb, route: ArrayList<String>): ControllerFunction? {
        val file = if (route.isEmpty()) {
            "index"
        } else {
            route.first()
        }

        val node = nodes[file] ?: return null
        if (node is RouteDirectory) {
            route.removeAt(0)
            return node[httpVerb, route]
        } else if (node is TailNode) {
            return node[httpVerb]
        }

        return null
    }

    override fun addRoute(
        httpVerb: HttpVerb,
        path: ArrayList<String>,
        controllerFunction: ControllerFunction
    ) {
        if (path.isEmpty()) {
            if ("index" !in nodes) {
                nodes["index"] = ControllerTailNode(this, EnumMap(HttpVerb::class.java))
            }

            val node = nodes["index"]

            if (node is ControllerTailNode) {
                node[httpVerb] = controllerFunction
            }
        } else {
            val pathElement = path.first()
            if (pathElement !in nodes.keys) {
                nodes[pathElement] = RouteDirectory(this)
            }

            path.removeAt(0)
            nodes[pathElement]?.addRoute(httpVerb, path, controllerFunction)
        }
    }
}