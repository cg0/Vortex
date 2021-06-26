package uk.cg0.vortex.template

import uk.cg0.vortex.controller.respondable.Respondable
import uk.cg0.vortex.webserver.enum.HttpContentType
import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

interface Template: Respondable {
    fun render(): String
    override fun handleResponse(request: Request, response: Response) {
        response.statusCode = HttpStatus.OK
        response.contentType = HttpContentType.TEXT_HTML

        response.writeString(render())
    }
}