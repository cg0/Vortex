package uk.cg0.vortex.controller

import uk.cg0.vortex.helper.injectFunction
import uk.cg0.vortex.middleware.Middleware
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

data class ControllerFunction(val function: KFunction<Any>,
                              val variables: HashMap<String, Any>,
                              val middleware: ArrayList<Middleware>) {
    constructor(function: KFunction<Any>, middleware: ArrayList<Middleware>): this(function, HashMap(), middleware)

    fun execute(request: Request, response: Response): Any {
        for (middlewareItem in middleware) {
            val respondable = middlewareItem.handleMiddleware(request, response, variables)
            if (respondable != null) {
                return respondable
            }
        }

        variables["request"] = request
        variables["response"] = response
        val returnValue = injectFunction(function, variables)
        variables.remove("request")
        variables.remove("response")

        return returnValue
    }
}