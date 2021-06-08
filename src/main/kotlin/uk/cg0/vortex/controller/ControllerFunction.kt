package uk.cg0.vortex.controller

import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

data class ControllerFunction(val function: KFunction<Any>,
                              val variables: HashMap<String, String>) {
    constructor(function: KFunction<Any>): this(function, HashMap())

    fun execute(request: Request, response: Response): Any {
        val instance = function.javaMethod?.declaringClass?.newInstance()
        request.variables = variables
        return function.call(instance, request, response)
    }
}