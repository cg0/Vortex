package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb
import kotlin.collections.ArrayList

class ControllerContainerNode(override val parent: RouteNode?) : ContainerNode(parent, "index") {
    override fun get(httpVerb: HttpVerb, route: ArrayList<String>): ControllerFunction? {
        return super.nodes[httpVerb.name]?.get(httpVerb, route)
    }

    operator fun set(httpVerb: HttpVerb, controllerTailNode: ControllerTailNode) {
        super.nodes[httpVerb.name] = controllerTailNode
    }
}