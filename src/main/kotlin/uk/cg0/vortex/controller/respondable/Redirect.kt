package uk.cg0.vortex.controller.respondable

import uk.cg0.vortex.webserver.enum.HttpStatus
import uk.cg0.vortex.webserver.objects.Request
import uk.cg0.vortex.webserver.objects.Response

class Redirect(private val path: String): Respondable {
    override fun handleResponse(request: Request, response: Response) {
        response.headers["Location"] = path
        response.statusCode = HttpStatus.TEMPORARY_REDIRECT
    }

}