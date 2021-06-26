package uk.cg0.vortex.controller.respondable

import uk.cg0.vortex.Vortex
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

class ServerError(private val httpStatus: HttpStatus): Respondable {
    override fun handleResponse(request: Request, response: Response) {
        val error = Vortex.routingEngine.getError(request.headers["Host"] ?: "*", httpStatus)
        error?.execute(request, response)
    }
}