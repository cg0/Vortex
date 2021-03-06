package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb

class VariableContainerNode(parent: RouteNode?,
                            private val variableName: String,
                            routeKey: String
): ContainerNode(parent, routeKey) {
    override fun get(httpVerb: HttpVerb, route: ArrayList<String>): ControllerFunction? {
        val variable = route.first()
        val controllerFunction = super.get(httpVerb, route)
        controllerFunction?.variables?.set(variableName, variable)
        return controllerFunction
    }
}