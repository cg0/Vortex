package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb

class RouteVariableWildcard(parent: RouteNode?, private val variableName: String): RouteDirectory(parent) {
    override fun addRoute(
        httpVerb: HttpVerb,
        path: ArrayList<String>,
        controllerFunction: ControllerFunction
    ) {
        controllerFunction.variables[variableName] = path.first()
        path.removeAt(0)
        super.addRoute(httpVerb, path, controllerFunction)
    }
}