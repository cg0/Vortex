package uk.cg0.vortex.webserver.routing

import uk.cg0.vortex.controller.ControllerFunction
import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response
import java.util.*
import kotlin.collections.ArrayList

class ControllerTailNode(override val parent: RouteNode?,
                         private val controllers: EnumMap<HttpVerb, ControllerFunction>): TailNode {
    override operator fun get(httpVerb: HttpVerb): ControllerFunction? {
        return controllers[httpVerb]
    }

    override operator fun set(httpVerb: HttpVerb, controllerFunction: ControllerFunction) {
        controllers[httpVerb] = controllerFunction
    }

    override fun get(httpVerb: HttpVerb, route: ArrayList<String>): ControllerFunction? {
        TODO("Not yet implemented")
    }

    override fun addRoute(httpVerb: HttpVerb, path: ArrayList<String>, controllerFunction: ControllerFunction) {
        TODO("Not yet implemented")
    }
}