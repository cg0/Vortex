package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.Controller
import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response
import java.rmi.UnexpectedException
import java.util.*
import kotlin.collections.ArrayList

class ControllerTailNode(override val parent: RouteNode?,
                         override val routeKey: String,
                         var controllerFunction: ControllerFunction
): TailNode {
    override fun set(routeList: ArrayList<RouteNode>, controllerFunction: ControllerFunction) {
        this.controllerFunction = controllerFunction
    }

    override fun get(httpVerb: HttpVerb, route: ArrayList<String>): ControllerFunction {
        return controllerFunction
    }
}