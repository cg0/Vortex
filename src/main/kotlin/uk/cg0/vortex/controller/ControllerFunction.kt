package uk.cg0.vortex.controller

import uk.cg0.vortex.webserver.enum.HttpVerb
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.createInstance

data class ControllerFunction(val controller: KClass<*>,
                              val function: KFunction<Unit>,
                              val variables: HashMap<String, String>) {
    constructor(controller: KClass<*>, function: KFunction<Unit>): this(controller, function, HashMap())

    fun execute(request: Request, response: Response) {
        val instance = controller.createInstance()
        request.variables = variables
        function.call(instance, request, response)
    }
}