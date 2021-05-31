package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb

interface TailNode: RouteNode {
    operator fun get(httpVerb: HttpVerb): ControllerFunction?
    operator fun set(httpVerb: HttpVerb, controllerFunction: ControllerFunction)
}