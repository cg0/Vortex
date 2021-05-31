package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb

interface RouteNode {
    val parent: RouteNode?

    operator fun get(httpVerb: HttpVerb, route: ArrayList<String>): ControllerFunction?
    fun addRoute(
        httpVerb: HttpVerb,
        path: ArrayList<String>,
        controllerFunction: ControllerFunction
    )
}