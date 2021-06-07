package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb

interface RouteNode {
    val parent: RouteNode?
    val routeKey: String

    operator fun get(httpVerb: HttpVerb, route: ArrayList<String>): ControllerFunction?
    operator fun set(routeList: ArrayList<RouteNode>, controllerFunction: ControllerFunction)
}