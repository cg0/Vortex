package uk.cg0.vortex.controller

import uk.cg0.vortex.helper.injectFunction
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

data class ControllerFunction(val function: KFunction<Any>,
                              val variables: HashMap<String, Any>) {
    constructor(function: KFunction<Any>): this(function, HashMap())

    fun execute(request: Request, response: Response): Any {
        variables["request"] = request
        variables["response"] = response
        val returnValue = injectFunction(function, variables)
        variables.remove("request")
        variables.remove("response")

        return returnValue
    }
}